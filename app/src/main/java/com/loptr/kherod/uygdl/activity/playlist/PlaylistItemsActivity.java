package com.loptr.kherod.uygdl.activity.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.loptr.kherod.uygdl.PlaylistDatabase.FavouriteItem;
import com.loptr.kherod.uygdl.PlaylistDatabase.Playlist;
import com.loptr.kherod.uygdl.PlaylistDatabase.PlaylistViewModel;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.activity.ExoplayerActivity;
import com.loptr.kherod.uygdl.adapter.PlaylistItemsAdapter;
import com.loptr.kherod.uygdl.utlities.Config;
import com.loptr.kherod.uygdl.databinding.ActivityPlaylistItemsBinding;
import com.loptr.kherod.uygdl.model.PlaylistItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlaylistItemsActivity extends AppCompatActivity implements PlaylistItemsAdapter.onSelectPlaylistItem , PlaylistItemsAdapter.onSelectPlaylistItemMenu {
    BottomSheetDialog editPlaylistBottomSheetDialog ;
    ActivityPlaylistItemsBinding binding ;
    Playlist currentPlaylist ;
    PlaylistItemsAdapter adapter ;
    List<PlaylistItem> playlistItemsList;
    SearchView searchView ;
    PlaylistViewModel playlistViewModel ;
    AlertDialog renameDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Config.updateTheme(this);
        Config.updateLocale(this);
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        initPlaylist();
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (currentPlaylist!=null)
            binding.toolbarTxt.setText(String.format(getString(R.string.playlist_name) , currentPlaylist.getName()));
        }
        initListeners();
        initRecycleView();
        initViewModel();
    }

    private void initViewModel() {
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
    }

    private void initRecycleView() {
        adapter = new PlaylistItemsAdapter(this);
        if (playlistItemsList != null && playlistItemsList.size() != 0) {
            adapter.Full_data = true ;
            adapter.submitList(playlistItemsList);
        }
        binding.rvPlaylistsItems.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu , menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        menu.findItem(R.id.done).setVisible(false);
        menu.findItem(R.id.selectAll).setVisible(false);
        prepareSearchView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initListeners() {
        binding.addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open add items activity
                Intent intent = new Intent(getBaseContext() , AddPlaylistItemsActivity.class);
                intent.putExtra(Config.PLAYLIST_KEY , currentPlaylist);
                startActivity(intent);
            }
        });
    }

    private void initPlaylist() {
        currentPlaylist = getIntent().getParcelableExtra(Config.PLAYLIST_KEY);
        if (currentPlaylist == null) return;
        if (currentPlaylist.isFavourite()) {
            binding.addItems.setVisibility(View.GONE);
        }
        playlistItemsList = currentPlaylist.getPlaylistItems();
    }


    private void prepareSearchView() {
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter.getFilter() == null) return false ;
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter.getFilter() == null) return false ;
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onSelectItem(PlaylistItem playlistItem) {
        openUrl(playlistItem.getPath());
    }

    @Override
    public void onSelectMenu(PlaylistItem playlistItem) {
        createEditPlaylistBottomSheetDialog(playlistItem);
    }

    public void createEditPlaylistBottomSheetDialog(PlaylistItem playlistItem) {
        editPlaylistBottomSheetDialog = new BottomSheetDialog(this, R.style.bottomSheetDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.edit_playlist_layout, (ViewGroup) binding.getRoot(), false);
        editPlaylistBottomSheetDialog.setContentView(view);
        TextView delete  = view.findViewById(R.id.removePlaylist);
        TextView rename = view.findViewById(R.id.renamePlaylist);
        TextView playlistName = view.findViewById(R.id.playlistName);
        playlistName.setText(getString(R.string.edit_file));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePlaylistItem(playlistItem);
                editPlaylistBottomSheetDialog.dismiss();
            }
        });
        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditPlaylistDialog(playlistItem);
                editPlaylistBottomSheetDialog.dismiss();
            }
        });
        editPlaylistBottomSheetDialog.show();
    }

    private void deletePlaylistItem(PlaylistItem playlistItem) {
        Playlist playlist = playlistViewModel.getPlaylistById(playlistItem.getPlaylist_id()) ;
        ArrayList<PlaylistItem> playlistItems = playlist.getPlaylistItems();
        removeItem(playlistItem , playlistItems);
        playlist.setPlaylistItems(playlistItems);
        playlistViewModel.updatePlaylist(playlist);
        Snackbar.make(binding.rvPlaylistsItems , getString(R.string.file_removed_succes) , Snackbar.LENGTH_SHORT).show();
        adapter.submitList(playlist.getPlaylistItems());
        removeItemFromFavouriteItems(playlistItem.getName() , playlistItem.getPath());
    }

    private void removeItemFromFavouriteItems(String name, String path) {
        List<FavouriteItem> favouriteItems = playlistViewModel.getAllFavouriteItems();
        Iterator<FavouriteItem> itr = favouriteItems.iterator();
        while (itr.hasNext()) {
            FavouriteItem item = itr.next();
            if(item.getPath().equals(path) && item.getName().equals(name)) {
                itr.remove();
                break;
            }
        }
        playlistViewModel.deleteAllFavouriteItems();
        playlistViewModel.insertAllFavouriteItems(favouriteItems);
    }

    private void removeItem(PlaylistItem item , List<PlaylistItem> playlistItems) {
        Iterator<PlaylistItem> itr = playlistItems.iterator();
        while (itr.hasNext()) {
            PlaylistItem item_itr = itr.next();
                if (item_itr.getPath().equals(item.getPath()) && item_itr.getName().equals(item.getName())) {
                    itr.remove();
                    break;
                }
            }
        }



    private void ShowEditPlaylistDialog(PlaylistItem playlistItem) {
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_url , (ViewGroup) binding.getRoot(), false);
        EditText editText = view.findViewById(R.id.etUrl);
        editText.setText(playlistItem.getName());
        editText.setHint(getString(R.string.enter_file));
        Button button = view.findViewById(R.id.btnOpen);
        button.setText(getString(R.string.edit_file));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renamePlaylistItem(playlistItem , editText.getText().toString());
                renameDialog.dismiss();
            }
        });
        dialog_builder.setView(view);
        dialog_builder.setCancelable(true);
        renameDialog = dialog_builder.create();
        renameDialog.show();
    }

    private void renamePlaylistItem(PlaylistItem playlistItem, String newName) {
        Playlist playlist = playlistViewModel.getPlaylistById(playlistItem.getPlaylist_id()) ;
        ArrayList<PlaylistItem> playlistItems = playlist.getPlaylistItems();
        renameItem(playlistItem , playlistItems , newName);
        playlist.setPlaylistItems(playlistItems);
        playlistViewModel.updatePlaylist(playlist);
        Snackbar.make(binding.rvPlaylistsItems , getString(R.string.file_edit_succes) , Snackbar.LENGTH_SHORT).show();
        adapter.submitList(playlistItems);
    }

    private void renameItem(PlaylistItem playlistItem, ArrayList<PlaylistItem> playlistItems, String newName) {
        for (PlaylistItem item_itr : playlistItems) {
            if (item_itr.getPath().equals(playlistItem.getPath()) && item_itr.getName().equals(playlistItem.getName())) {
                item_itr.setName(newName);
                break;
            }
        }
    }

    public void openUrl(String url){
        Intent intent = new Intent(this, ExoplayerActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
        //finish();
        //mBinding.progressBarLayout.setVisibility(View.GONE);
    }
}