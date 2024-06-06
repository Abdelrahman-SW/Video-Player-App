package com.loptr.kherod.uygdl.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loptr.kherod.uygdl.PlaylistDatabase.FavouriteItem;
import com.loptr.kherod.uygdl.PlaylistDatabase.PlaylistViewModel;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.adapter.MusicAdapter;
import com.loptr.kherod.uygdl.databinding.FragmentMusicBinding;
import com.loptr.kherod.uygdl.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends Fragment   {

    FragmentMusicBinding binding  = null ;
    public MusicAdapter adapter;
    public List<MusicModel> musics = new ArrayList<>();
    SwipeRefreshLayout refreshLayout = null ;
    public boolean isCheckable = false;
    public static MusicFragment musicFragment;



    public static MusicFragment getMusicFragment(SwipeRefreshLayout refreshLayout) {
        if (musicFragment == null) {
            if (refreshLayout != null)
            musicFragment = new MusicFragment(refreshLayout);
            else musicFragment = new MusicFragment();
        }
        return musicFragment ;
    }




    public MusicFragment() {
        // Required empty public constructor
    }

    private MusicFragment(SwipeRefreshLayout refreshLayout) {
        // Required empty public constructor
        this.refreshLayout = refreshLayout ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_music , container , false);
        return binding.getRoot();
    }

    public void doRefresh() {
        Log.i("ab_do" , "MusicFragmentRefreshed" );
        loadMusic();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerview();
        loadMusic();
    }

    private void loadMusic() {
        Log.i("music" , "LoadMusic");
        musics.clear();
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Uri songUri ;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ;
        }
        else songUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL) ;
        String [] Columns = {MediaStore.Audio.Media.DISPLAY_NAME , MediaStore.Audio.Media.ARTIST , MediaStore.Audio.Media.DATA};
        String where = MediaStore.Audio.Media.IS_MUSIC + "=1" ;
        Cursor songs = contentResolver.query(songUri , Columns , where , null , null);
        if (songs != null && songs.moveToFirst()) {
            int titleIndex = songs.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int artistIndex = songs.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataIndex = songs.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String title = songs.getString(titleIndex);
                String artist = songs.getString(artistIndex);
                String data = songs.getString(dataIndex);
                musics.add(new MusicModel(title , artist , data , isFavourite(title , data)));
            }
            while (songs.moveToNext());
            songs.close();
        }
            else {
                Log.i("ab_do" , "cursor is null");
            }
            updateMusics(musics);
    }

    private boolean isFavourite(String title , String path) {
        if (getActivity() == null) return false;
        PlaylistViewModel playlistViewModel = new ViewModelProvider(requireActivity()).get(PlaylistViewModel.class);
        List<FavouriteItem> favouriteItems = playlistViewModel.getAllFavouriteItems();
        for (FavouriteItem item : favouriteItems) {
            if (item.getName().equals(title) && item.getPath().equals(path)) {
                return  true ;
            }
        }
        return false ;
    }

    private void initRecyclerview(){
        binding.rvMusics.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMusics.setAdapter(adapter);
    }

    private void updateMusics(List<MusicModel> Musics) {
        adapter.Full_data = true ;
        adapter.refreshList(Musics ,refreshLayout);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        adapter = new MusicAdapter(context , isCheckable);
    }
}