package com.loptr.kherod.uygdl.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.activity.playlist.AddPlaylistItemsActivity;
import com.loptr.kherod.uygdl.fragments.MusicFragment;
import com.loptr.kherod.uygdl.fragments.VideosFragment;

import org.jetbrains.annotations.NotNull;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    private  MusicFragment musicFragment ;
    private  VideosFragment videosFragment ;
    public AddPlaylistItemsActivity currentActivity ;
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm , BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        currentActivity = (AddPlaylistItemsActivity) context ;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 :
                videosFragment = new VideosFragment(mContext);
                return videosFragment;
            case 1  :
                Log.i("ab_do" , "MusicFragment");
                musicFragment = new MusicFragment();
                ((MusicFragment)musicFragment).isCheckable = true ;
                return musicFragment ;
        }
        return null;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    public MusicFragment getMusicFragment() {
        return musicFragment;
    }

    public void setMusicFragment(MusicFragment musicFragment) {
        this.musicFragment = musicFragment;
    }

    public VideosFragment getVideosFragment() {
        return videosFragment;
    }

    public void setVideosFragment(VideosFragment videosFragment) {
        this.videosFragment = videosFragment;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}