package com.loptr.kherod.uygdl.activity.playlist;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.loptr.kherod.uygdl.PlaylistDatabase.Playlist;
import com.loptr.kherod.uygdl.PlaylistDatabase.PlaylistViewModel;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.activity.ExoplayerActivity;
import com.loptr.kherod.uygdl.adapter.MusicAdapter;
import com.loptr.kherod.uygdl.adapter.SectionsPagerAdapter;
import com.loptr.kherod.uygdl.adapter.VideosAdapter;
import com.loptr.kherod.uygdl.utlities.Config;
import com.loptr.kherod.uygdl.fragments.MusicFragment;
import com.loptr.kherod.uygdl.fragments.VideosFragment;
import com.loptr.kherod.uygdl.model.MusicModel;
import com.loptr.kherod.uygdl.model.PlaylistItem;
import com.loptr.kherod.uygdl.model.VideoModel;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddPlaylistItemsActivity extends AppCompatActivity implements VideosAdapter.onVideoClickCheckBox , MusicAdapter.onMusicClickCheckBox , MusicAdapter.onMusicSelected , VideosAdapter.onVideoSelected , MusicAdapter.onMusicMenuClicked , VideosAdapter.onVideoMenuClicked {

    ArrayList<PlaylistItem> selectedPlaylistItems = new ArrayList<>() ;
    ArrayList<PlaylistItem> currentPlaylistItemsList ;
     SearchView  searchView ;
    SectionsPagerAdapter sectionsPagerAdapter ;
    Playlist currentPlaylist ;
    int SelectedItems = 0 ;
    TextView toolbar_txt ;
    boolean isSelectedAllMusics = false ;
    boolean isSelectedAllVideos = false ;
    MenuItem SelectAllItem ;
    Fragment currentFragment ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Config.updateTheme(this);
        Log.i("ab_do" , "onCreate");
        Config.updateLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist_items);
        initPlaylist();
        initToolbar();
        initViewPager();
    }

    private void initPlaylist() {
        currentPlaylist = getIntent().getParcelableExtra(Config.PLAYLIST_KEY);
        if (currentPlaylist!=null)
        currentPlaylistItemsList = currentPlaylist.getPlaylistItems();
    }

    private void initViewPager() {
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        currentFragment = sectionsPagerAdapter.getVideosFragment();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                 if (position == 0) {
                     // video Fragment
                     Log.i("ab_do" , "Video");
                     currentFragment = sectionsPagerAdapter.getVideosFragment();
                 }
                 else {
                     Log.i("ab_do" , "Music");
                     currentFragment = sectionsPagerAdapter.getMusicFragment();
                 }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar) ;
        toolbar_txt = findViewById(R.id.toolbar_txt);
        updateToolbarTxt();
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu , menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SelectAllItem = menu.findItem(R.id.selectAll);
        prepareSearchView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }

        if (itemId == R.id.done) {
            addItemsToThePlaylist();
            return true;
        }

        if (itemId == R.id.selectAll) {
            return ApplySelection();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean ApplySelection() {
        if (getCurrentFragment() instanceof MusicFragment) {
            if (isSelectedAllMusics) {
                //unSelectAllMusics
                unSelectAllMusics();
            }
            else {
                //selectAllMusics
                selectAllMusics();
            }
        }
        else if (getCurrentFragment() instanceof VideosFragment) {
            if (isSelectedAllVideos) {
                //unSelectAllVideos
                unSelectAllVideos();
            }
            else {
                //selectAllVideos
                 selectAllVideos();
            }
        }
        return true;
    }

    private void unSelectAllVideos() {
        DeleteAllVideos();
        updateToolbarTxt();
        isSelectedAllVideos = false ;
        updateCheckBoxes(false);
    }

    private void selectAllVideos() {
        DeleteAllVideos();
        List<VideoModel> videoModels = ((VideosFragment)getCurrentFragment()).adapter.getCurrentList();
        SelectedItems += videoModels.size();
        isSelectedAllVideos = true ;
        addAllVideos(videoModels);
        updateToolbarTxt();
        updateCheckBoxes(true);
    }

    private void unSelectAllMusics() {
        DeleteAllMusics();
        updateToolbarTxt();
        isSelectedAllMusics = false ;
        updateCheckBoxes(false);
    }

    private void selectAllMusics() {
        DeleteAllMusics();
        List<MusicModel> musicModels = ((MusicFragment)getCurrentFragment()).adapter.getCurrentList();
        SelectedItems += musicModels.size();
        isSelectedAllMusics = true ;
        addAllMusics(musicModels);
        updateToolbarTxt();
        updateCheckBoxes(true);
    }

    private void addAllMusics(List<MusicModel> musicModels) {
        Iterator<MusicModel> itr = musicModels.iterator();
        while (itr.hasNext()) {
            AddMusicModel(itr.next());
        }
    }

    private void addAllVideos(List<VideoModel> videoModels) {
        Iterator<VideoModel> itr = videoModels.iterator();
        while (itr.hasNext()) {
            VideoModel item = itr.next();
            AddVideoModel(item);
        }
    }


    private void updateCheckBoxes(boolean isChecked) {
        for (Parcelable item : getCurrentAllList()) {
            if (item instanceof MusicModel) {
                ((MusicModel) item).setChecked(isChecked);
            }
            else {
                ((VideoModel) item).setChecked(isChecked);
            }
        }
        updateList();
    }

    private void updateList() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof MusicFragment) {
            ((MusicFragment) fragment).adapter.notifyDataSetChanged();
        }
        else if (fragment instanceof VideosFragment) {
            ((VideosFragment) fragment).adapter.notifyDataSetChanged();
        }
    }

    private Fragment getCurrentFragment() {
        if (currentFragment==null) {
            currentFragment = sectionsPagerAdapter.getVideosFragment();
        }
        return currentFragment;
    }

    private List<? extends Parcelable> getCurrentAllList() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof MusicFragment) {
            return ((MusicFragment) fragment).adapter.getCurrentList();
        }
        else if (fragment instanceof VideosFragment) {
            return ((VideosFragment) fragment).adapter.getCurrentList();
        }
        return null ;
    }

    private void addItemsToThePlaylist() {
          PlaylistViewModel viewModel = new ViewModelProvider(this).get(PlaylistViewModel.class) ;
          currentPlaylistItemsList.addAll(selectedPlaylistItems);
          currentPlaylist.setPlaylistItems(currentPlaylistItemsList);
          viewModel.updatePlaylist(currentPlaylist);
          Intent intent = new Intent(getBaseContext() , PlaylistItemsActivity.class);
          intent.putExtra(Config.PLAYLIST_KEY , currentPlaylist);
          startActivity(intent);
          finish();
    }

    private void prepareSearchView() {
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Fragment fragment = getCurrentFragment() ;
                if ( fragment instanceof MusicFragment)
                ((MusicFragment) fragment).adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Fragment fragment = getCurrentFragment() ;
                if ( fragment instanceof MusicFragment)
                    ((MusicFragment) fragment).adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onCheckBoxClicked(MusicModel musicModel, boolean isChecked) {
        if (isChecked) {
            // add music model
            AddMusicModel(musicModel);
            SelectedItems++ ;
        }
        else {
            // remove music model
            removeMusic(musicModel);
            SelectedItems-- ;
        }
        updateToolbarTxt();
    }

    private void AddMusicModel(MusicModel musicModel) {
        PlaylistItem playlistItem = new PlaylistItem((int) currentPlaylist.getId(), musicModel.getTitle() , musicModel.getArtist() , musicModel.getUrl() , true);
        selectedPlaylistItems.add(playlistItem);
    }

    @Override
    public void onCheckBoxClicked(VideoModel videoModel, boolean isChecked) {
        if (isChecked) {
            // add video model
            AddVideoModel(videoModel);
            SelectedItems++ ;
        }
        else {
            // remove video model
            removeVideo(videoModel);
            SelectedItems-- ;
        }
        updateToolbarTxt();
    }

    private void updateToolbarTxt() {
        toolbar_txt.setText(String.format(getString(R.string.toolbarTxt) , SelectedItems));
    }

    private void AddVideoModel(VideoModel videoModel) {
        PlaylistItem playlistItem = new PlaylistItem((int) currentPlaylist.getId(), videoModel.getVideoItem().getDisplayName() , Config.formatTime(videoModel.getVideoItem().getDuration()) , videoModel.getVideoItem().getPath() , false);
        selectedPlaylistItems.add(playlistItem);
    }

    private void removeVideo(VideoModel videoModel) {
        Iterator<PlaylistItem> itr = selectedPlaylistItems.iterator();
        while (itr.hasNext()) {
            PlaylistItem item = itr.next();
            if (!item.isMusic()) {
                if (item.getPath().equals(videoModel.getVideoItem().getPath())
                && item.getName().equals(videoModel.getVideoItem().getDisplayName())) {
                    itr.remove();
                }
            }
        }
    }

    private void removeMusic(MusicModel musicModel) {
        Iterator<PlaylistItem> itr = selectedPlaylistItems.iterator();
        while (itr.hasNext()) {
            PlaylistItem item = itr.next();
            if (item.isMusic()) {
                if (item.getPath().equals(musicModel.getUrl())
                && item.getName().equals(musicModel.getTitle())) {
                    itr.remove();
                }
            }
        }
    }



    private void DeleteAllMusics() {
        Iterator<PlaylistItem> itr = selectedPlaylistItems.iterator();
        while (itr.hasNext()) {
            PlaylistItem item = itr.next();
            if (item.isMusic()) {
                itr.remove();
                SelectedItems-- ;
            }
        }
    }

    private void DeleteAllVideos() {
        Iterator<PlaylistItem> itr = selectedPlaylistItems.iterator();
        while (itr.hasNext()) {
            PlaylistItem item = itr.next();
            if (!item.isMusic()) {
                itr.remove();
                SelectedItems--;
            }
        }
    }

    @Override
    public void onSelectMusic(String url) {
        openUrl(url);
    }

    @Override
    public void onSelectedVideo(String url) {
        openUrl(url);
    }

    public void openUrl(String url){
        Intent intent = new Intent(this, ExoplayerActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
        finish();
        //mBinding.progressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClickMusicMenu(MusicModel musicModel) {

    }

    @Override
    public void onClickVideoMenu(VideoModel videoModel) {

    }
}