package com.loptr.kherod.uygdl.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.VideoFolder;
import com.jiajunhui.xapp.medialoader.bean.VideoResult;
import com.jiajunhui.xapp.medialoader.callback.OnVideoLoaderCallBack;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.adapter.FoldersAdapter;
import com.loptr.kherod.uygdl.databinding.FragmentFoldersBinding;
import java.util.ArrayList;
import java.util.List;

public class FolderFragment extends Fragment {

    FragmentFoldersBinding binding = null ;
    public FoldersAdapter adapter;
    List<VideoFolder> folders = new ArrayList<>();
    SwipeRefreshLayout refreshLayout = null ;
    public static FolderFragment folderFragment;

    public static FolderFragment getFolderFragment(SwipeRefreshLayout refreshLayout) {
        if (folderFragment == null) {
            folderFragment = new FolderFragment(refreshLayout);
        }
        return folderFragment ;
    }

    public FolderFragment() {
        // Required empty public constructor
    }

    private FolderFragment(SwipeRefreshLayout refreshLayout) {
        // Required empty public constructor
        this.refreshLayout = refreshLayout ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("ab_do"  , "onCreateView");
        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_folders, container , false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.i("ab_do"  , "onAttach");
        adapter = new FoldersAdapter(context);
        super.onAttach(context);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerview();
        loadFolders();
    }

    public void doRefresh() {
        Log.i("ab_do" , "VideoFragmentRefreshed" );
        loadFolders();
    }

    private void initRecyclerview(){
        binding.rvFolders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFolders.setAdapter(adapter);
    }

    private void loadFolders(){
        folders.clear();
        MediaLoader.getLoader().loadVideos(requireActivity(), new OnVideoLoaderCallBack() {
            @Override
            public void onResult(VideoResult result) {
                Log.i("ab_do" , "onResult");
                folders = result.getFolders();
                updateFolders(folders);
            }
        });
    }

    private void updateFolders(List<VideoFolder> videoFolders) {
        adapter.Full_data = true ;
        adapter.refreshList(videoFolders , refreshLayout);
    }




}