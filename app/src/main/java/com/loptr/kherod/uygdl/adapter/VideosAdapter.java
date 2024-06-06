package com.loptr.kherod.uygdl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.utlities.Config;
import com.loptr.kherod.uygdl.databinding.LayoutVideoItemBinding;
import com.loptr.kherod.uygdl.model.VideoModel;

import java.util.ArrayList;
import java.util.List;

public class VideosAdapter extends ListAdapter<VideoModel ,RecyclerView.ViewHolder> implements Filterable {

    private static final DiffUtil.ItemCallback<VideoModel> diffUtil = new DiffUtil.ItemCallback<VideoModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull VideoModel oldItem, @NonNull VideoModel newItem) {
            return oldItem.getVideoItem().getPath().equals(newItem.getVideoItem().getPath());
        }

        @Override
        public boolean areContentsTheSame(@NonNull VideoModel oldItem, @NonNull VideoModel newItem) {
            //return oldItem.getVideoItem().getDisplayName().equals(newItem.getVideoItem().getDisplayName()) ;
            return false ;
        }
    };

    private boolean isCheckable = false ;
    private boolean isGrid;
    private onVideoSelected listener ;
    private final Context mContext;
    private onVideoClickCheckBox checkBoxListener ;
    private onVideoMenuClicked menu_listener ;
    private String SearchTxt = "";
    private boolean Full_data  ;
    List<VideoModel> videos = new ArrayList<>();
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<VideoModel> videoFilters = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                videoFilters.clear();
                // Search Result Not Found // user exit the search view
                SearchTxt = "";
                videoFilters.addAll(videos);
                Log.i("ab_do" , "Search is empty");
            } else {
                videoFilters.clear();
                SearchTxt = constraint.toString().toLowerCase().trim();
                for (VideoModel videoModel : videos) {
                    if (videoModel.getVideoItem().getDisplayName().toLowerCase().trim().contains(SearchTxt)
                    ) {
                        videoFilters.add(videoModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = videoFilters;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            Full_data = false ;
            submitList((List<VideoModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public VideosAdapter(Context mContext , boolean isCheckable ) {
        super(diffUtil);
        this.mContext = mContext;
        listener = (onVideoSelected) mContext;
        this.isCheckable = isCheckable ;
        checkBoxListener = (onVideoClickCheckBox) mContext;
        this.menu_listener = (onVideoMenuClicked) mContext;
        //this.thumbnailList = thumbnailList;
    }

    @Override
    public void submitList(@Nullable List<VideoModel> list) {
        if (list!=null) {
            super.submitList(list);
            if (Full_data) {
                videos.clear();
                videos.addAll(list);
            }
            //notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutVideoItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.layout_video_item, parent, false);

        return new VideoHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (position == RecyclerView.NO_POSITION) return;
        VideoHolder videoHolder = (VideoHolder) holder;
        VideoModel videoItem = getItem(position);
        videoHolder.mBinding.videoDuration.setText(Config.formatTime(videoItem.getVideoItem().getDuration()));
        Bitmap thumbnail = videoItem.getThumbnail();
        if(thumbnail != null){
            Glide.with(mContext)
                    .load(thumbnail)
                    .into(videoHolder.mBinding.videoImg);
        }
        if (isCheckable) {
            videoHolder.mBinding.checkbox.setVisibility(View.VISIBLE);
            videoHolder.mBinding.checkbox.setChecked(videoItem.isChecked());
            videoHolder.mBinding.menu.setVisibility(View.GONE);
            videoHolder.mBinding.VideoTitle.setVisibility(View.GONE);
        }
        else  {
            videoHolder.mBinding.checkbox.setVisibility(View.GONE);
            videoHolder.mBinding.menu.setVisibility(View.VISIBLE);
        }

        if (!isGrid) {
            if (SearchTxt.trim().length() != 0) {
                int StartIndex, EndIndex;
                String name = videoItem.getVideoItem().getDisplayName().trim().toLowerCase();
                SpannableStringBuilder spannableString;

                if (name.contains(SearchTxt)) {
                    StartIndex = name.indexOf(SearchTxt);  // word -- > world in --> title HelloWorld
                    // s = 6 , e = 11 the 11 th character will not be colored
                    EndIndex = StartIndex + SearchTxt.length();
                    spannableString = new SpannableStringBuilder(videoItem.getVideoItem().getDisplayName());
                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.lightBlue)), StartIndex, EndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    videoHolder.mBinding.VideoTitle.setText(spannableString);
                }
                else
                    videoHolder.mBinding.VideoTitle.setText(videoItem.getVideoItem().getDisplayName());
            }
                else{
                    videoHolder.mBinding.VideoTitle.setText(videoItem.getVideoItem().getDisplayName());
                }
                videoHolder.mBinding.VideoTitle.setVisibility(View.VISIBLE);
            }
        else {
            videoHolder.mBinding.VideoTitle.setVisibility(View.GONE);
        }
    }

    public void updateList(List<VideoModel> videos, boolean isGrid) {
        Log.i("ab_do" , "updateList");
        Full_data = true;
        if (SearchTxt.trim().length() > 0) {
            Log.i("ab_do" , "Searching now ...");
            // user is searching now so just save the list and not sumbit it
            this.videos.clear();
            this.videos.addAll(videos);
        }
        else {
            submitList(videos);
            try {
                notifyItemChanged(videos.size()-1);
            }
            catch (Exception exception) {
                notifyDataSetChanged();
            }
            Log.i("ab_do" , "Not Searching now ...");
        }
        this.isGrid = isGrid ;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class VideoHolder extends RecyclerView.ViewHolder{

        LayoutVideoItemBinding mBinding;

        public VideoHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            mBinding.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      checkBoxListener.onCheckBoxClicked(getItem(getAdapterPosition()) , mBinding.checkbox.isChecked());
                      getItem(getAdapterPosition()).setChecked(mBinding.checkbox.isChecked());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        listener.onSelectedVideo(getItem(getAdapterPosition()).getVideoItem().getPath());
                    }catch (Exception exception) {
                        Log.i("ab_do" , exception.getMessage());
                    }
                }
            });
            mBinding.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu_listener.onClickVideoMenu(getItem(getAdapterPosition()));
                }
            });
        }
    }

    public interface onVideoSelected {
        void onSelectedVideo (String url) ;
    }

    public interface onVideoMenuClicked {
        void onClickVideoMenu (VideoModel videoModel) ;
    }

    public interface onVideoClickCheckBox {
        void onCheckBoxClicked (VideoModel videoModel , boolean isChecked) ;
    }
}
