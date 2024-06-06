package com.loptr.kherod.uygdl.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.loptr.kherod.uygdl.PlaylistDatabase.Playlist;
import com.loptr.kherod.uygdl.PlaylistDatabase.PlaylistViewModel;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.adapter.PlaylistAdapter;
import com.loptr.kherod.uygdl.databinding.FragmentPlaylistBinding;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    SwipeRefreshLayout refreshLayout ;
    FragmentPlaylistBinding binding ;
    BottomSheetDialog addPlaylistBottomSheetDialog;
    BottomSheetDialog editPlaylistBottomSheetDialog;
    public PlaylistAdapter adapter ;
    PlaylistViewModel viewModel ;
    List<Playlist> currentPlaylists ;
    public Playlist selectedPlaylist ;
    boolean edit = false ;
    String currentTxt = " " ;
    public static PlaylistFragment playlistFragment;

    public static PlaylistFragment getPlaylistFragment(SwipeRefreshLayout refreshLayout) {
        if (playlistFragment == null) {
            playlistFragment = new PlaylistFragment(refreshLayout);
        }
        return playlistFragment ;
    }


    public PlaylistFragment() {
        // Required empty public constructor
    }

    private PlaylistFragment(SwipeRefreshLayout refreshLayout) {
        // Required empty public constructor
        this.refreshLayout = refreshLayout ;
        currentPlaylists = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(inflater , R.layout.fragment_playlist , container , false);
        binding.addPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = false ;
                showBottomSheetDialog();
            }
        });
        initRecycleView();
        return binding.getRoot();
    }

    private void initRecycleView() {
        binding.rvPlaylists.setAdapter(adapter);
    }

    private void showBottomSheetDialog() {
        createBottomSheetDialog();
        try {
            addPlaylistBottomSheetDialog.show();
        }catch (Exception exception) {

        }
    }


    private void createBottomSheetDialog() {
            addPlaylistBottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.bottomSheetDialogTheme);
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.add_playlist_layout, (ViewGroup) binding.getRoot(), false);
            addPlaylistBottomSheetDialog.setContentView(view);
            Button addPlaylistBtn  = view.findViewById(R.id.addPlaylistBtn);
            EditText playlistName = view.findViewById(R.id.playlistEditTxt);
            if (edit) {
                addPlaylistBtn.setText(R.string.edit_playlist);
                playlistName.setText(currentTxt);
            }
            else {
                addPlaylistBtn.setText(R.string.create_playlist);
                currentTxt = " " ;
                playlistName.setText("");
            }
            addPlaylistBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(edit) {editPlaylistName(playlistName.getText().toString());}
                    else
                    createPlaylist(playlistName.getText().toString());
                    addPlaylistBottomSheetDialog.dismiss();
                }
            });
    }

    private void editPlaylistName(String name) {
        selectedPlaylist.setName(name);
        viewModel.updatePlaylist(selectedPlaylist);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(PlaylistViewModel.class) ;
        extracted();
        viewModel.get_playlists().observe(getViewLifecycleOwner(), new Observer<List<Playlist>>() {
            @Override
            public void onChanged(List<Playlist> playlists) {
                if (adapter !=null) {
                    Log.i("ab_do",  "Change");
                    adapter.Full_data = true ;
                    adapter.submitList(playlists);
                    currentPlaylists = playlists;
                }
            }
        });
    }

    private void extracted() {
        Playlist playlist = viewModel.getFavouritePlaylist();
        playlist.setName(getString(R.string.Favourite_playlist));
        viewModel.updatePlaylist(playlist);
    }


    private void createPlaylist(String name) {
        if (name.trim().length() == 0) {
            addPlaylistBottomSheetDialog.dismiss();
            Snackbar.make(binding.rvPlaylists , getString(R.string.erorr_name) , Snackbar.LENGTH_SHORT).show();
        }
        else {
            // create playlist and add it to the database
            Playlist playlist = new Playlist();
            playlist.setName(name);
            playlist.setMusicsSize(0);
            playlist.setVideosSize(0);
            viewModel.insertPlaylist(playlist);
        }
    }

    public void doRefresh() {
        Log.i("ab_do" , "PlaylistFragmentRefreshed" );
        refreshLayout.setRefreshing(false);
        adapter.Full_data = true ;
        adapter.refreshList(currentPlaylists , refreshLayout);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        adapter = new PlaylistAdapter(context);
    }

    public void showEditPlaylistBottomSheetDialog() {
        createEditPlaylistBottomSheetDialog();
        editPlaylistBottomSheetDialog.show();
    }

    public void createEditPlaylistBottomSheetDialog() {
        editPlaylistBottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.bottomSheetDialogTheme);
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.edit_playlist_layout, (ViewGroup) binding.getRoot(), false);
            editPlaylistBottomSheetDialog.setContentView(view);
            TextView delete  = view.findViewById(R.id.removePlaylist);
            TextView rename = view.findViewById(R.id.renamePlaylist);
            TextView playlistName = view.findViewById(R.id.playlistName);
            playlistName.setText(String.format(getString(R.string.playlist_name) , selectedPlaylist.getName()));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePlaylist(selectedPlaylist);
                    editPlaylistBottomSheetDialog.dismiss();
                }
            });
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renamePlaylist(selectedPlaylist);
                    editPlaylistBottomSheetDialog.dismiss();
                }
            });
    }

    private void deletePlaylist(Playlist selectedPlaylist) {
        Log.i("ab_do" , "deletePlaylist " + selectedPlaylist.getName());
        String msg = getString(R.string.delete_ask) ;
        showDeleteAlertDialog(msg , null ,false);
    }

    private void renamePlaylist(Playlist selectedPlaylist) {
       edit = true ;
       currentTxt = selectedPlaylist.getName();
       showBottomSheetDialog();
    }

    private void showDeleteAlertDialog(String Message , String title , boolean deleteAll) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext() , R.style.AlertDialogCustom);
        builder.setMessage(Message);
        if (title!=null) builder.setTitle(title);
        builder.setIcon(ContextCompat.getDrawable(requireContext() , R.drawable.gmts_quantum_ic_info_grey_24));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!deleteAll)
                // delete playlist :
                viewModel.deletePlaylist(selectedPlaylist);
                else
                    //delete All Playlists
                viewModel.deleteAllPlaylists();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void deleteAllPlaylists() {
        String msg = getString(R.string.delete_all_ask) ;
        if(currentPlaylists.size()<=1) {
            showSnackbar();
        }
        else
        showDeleteAlertDialog(msg , getString(R.string.delete_f) ,true);
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(binding.getRoot() , R.string.no_playlist_found , Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.lightBlue));
        snackbar.setAction( getString(R.string.ok) , new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                })
        .show();
    }
}