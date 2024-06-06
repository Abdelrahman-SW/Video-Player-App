package com.loptr.kherod.uygdl.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.VideoItem;
import com.jiajunhui.xapp.medialoader.bean.VideoResult;
import com.jiajunhui.xapp.medialoader.callback.OnVideoLoaderCallBack;
import com.loptr.kherod.uygdl.PlaylistDatabase.FavouriteItem;
import com.loptr.kherod.uygdl.PlaylistDatabase.PlaylistViewModel;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.adapter.VideosAdapter;
import com.loptr.kherod.uygdl.databinding.FragmentVideosBinding;
import com.loptr.kherod.uygdl.model.VideoModel;

import java.util.ArrayList;
import java.util.List;


public class VideosFragment extends Fragment  {
    FragmentVideosBinding binding ;
    public VideosAdapter adapter;
    List<VideoItem> videoItems ;
    SwipeRefreshLayout refreshLayout ;
    public List<VideoModel> videos = new ArrayList<>();
    // all Videos is displayed to select your video
    public boolean isCheckable = false ;
    boolean isGrid = false ;
    Thread thread ;
    public VideosFragment() {
        // Required empty public constructor
    }


    public VideosFragment(Context context) {
        isCheckable = true ;
        isGrid = true ;
        adapter = new VideosAdapter(context , isCheckable);
    }

    public VideosFragment(Context context , List<VideoItem> videoItems , SwipeRefreshLayout refreshLayout) {
        isCheckable = false ;
        isGrid = false ;
        this.videoItems = videoItems ;
        this.refreshLayout = refreshLayout ;
        adapter = new VideosAdapter(context , isCheckable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_videos , container , false);
        initRecyclerview(isGrid);
        return binding.getRoot();
    }

    public void doRefresh() {
        Log.i("ab_do" , "VideoFragmentRefreshed" );
        adapter.notifyDataSetChanged();
        if (refreshLayout!=null)
        refreshLayout.setRefreshing(false);
    }

    public void loadVideosItems() {
        if (videoItems == null) return;
        videos.clear();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) return;
                for (VideoItem videoItem : videoItems) {
                    if (thread.isInterrupted()) return;
                    Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
                            videoItem.getPath(),
                            MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    videos.add(new VideoModel(videoItem, thumbnail , isFavourite(videoItem.getDisplayName() , videoItem.getPath())));
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAdded())  return;
                            adapter.updateList(videos , isGrid);
                        }
                    });
                }
            }
        });

        thread.start();
 }


    @Override
    public void onDetach() {
        if (thread != null)
        thread.interrupt();
        super.onDetach();
    }

    private boolean isFavourite(String title , String path) {
        if (getActivity() == null) return false;
        PlaylistViewModel playlistViewModel = new ViewModelProvider(getActivity()).get(PlaylistViewModel.class);
        List<FavouriteItem> favouriteItems = playlistViewModel.getAllFavouriteItems();
        for (FavouriteItem item : favouriteItems) {
            if (item == null) continue;
            if (item.getName() == null || item.getPath() == null ) return false ;
            if (item.getName().equals(title) && item.getPath().equals(path)) {
                return  true ;
            }
        }
        return false ;
    }

    public void initRecyclerview(boolean grid){
        isGrid = grid ;
        if (!grid)
        binding.rvVideos.setLayoutManager(new LinearLayoutManager(requireContext()));
        else
        binding.rvVideos.setLayoutManager(new GridLayoutManager(requireContext() , 3));
        binding.rvVideos.setAdapter(adapter);
        if (isCheckable) {
            // load all videos
            loadAllVideos();
        }
        else loadVideosItems();
    }

    private void loadAllVideos() {
        MediaLoader.getLoader().loadVideos(requireActivity(), new OnVideoLoaderCallBack() {
            @Override
            public void onResult(VideoResult result) {
                Log.i("ab_do" , "onResult");
                videoItems = result.getItems();
                loadVideosItems();
            }
        });
    }

}