package com.loptr.kherod.uygdl.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.model.PlaylistItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlaylistItemsAdapter extends ListAdapter<PlaylistItem, RecyclerView.ViewHolder> implements Filterable {

    private final Context context;
    private final List<PlaylistItem> playlistItems = new ArrayList<>();
    private final PlaylistItemsAdapter.onSelectPlaylistItem listener ;
    private final PlaylistItemsAdapter.onSelectPlaylistItemMenu listener_menu ;
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List <PlaylistItem> playlistsFilters = new ArrayList<>() ;
            if (constraint == null || constraint.length()==0) {
                playlistsFilters.clear();
                // Search Result Not Found // user exit the search view
                SearchTxt = "" ;
                playlistsFilters.addAll(playlistItems) ;
            }
            else {
                playlistsFilters.clear();
                SearchTxt  = constraint.toString().toLowerCase().trim() ;
                for (PlaylistItem playlist_item : playlistItems) {
                    if (playlist_item.isMusic()) {
                        if (playlist_item.getName().toLowerCase().trim().contains(SearchTxt)
                        || playlist_item.getSummary().toLowerCase().trim().contains(SearchTxt) ) {
                            playlistsFilters.add(playlist_item) ;
                        }
                    }
                    else {
                        // video
                        if (playlist_item.getName().toLowerCase().trim().contains(SearchTxt)) {
                            playlistsFilters.add(playlist_item) ;
                        }
                    }

                }
            }
            FilterResults filterResults = new FilterResults() ;
            filterResults.values = playlistsFilters ;
            return filterResults ;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Full_data = false ;
            Log.i("ab_do" , "size " + ((List<PlaylistItem>) results.values).size());
            submitList((List<PlaylistItem>) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public void submitList(List<PlaylistItem> values) {
        Log.i("ab_do" , "submitList");
        if (values!=null) {
            super.submitList(values);
            if (Full_data) {
                playlistItems.clear();
                playlistItems.addAll(values);
            }
            //notifyDataSetChanged();
        }
    }

    private String SearchTxt = "";
    public boolean Full_data = false;

    private static final DiffUtil.ItemCallback<PlaylistItem> diffUtil = new DiffUtil.ItemCallback<PlaylistItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull PlaylistItem oldItem, @NonNull PlaylistItem newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getPath().equals(newItem.getPath());

        }

        @Override
        public boolean areContentsTheSame(@NonNull PlaylistItem oldItem, @NonNull PlaylistItem newItem) {
           return false ;
        }
    };


    public PlaylistItemsAdapter(Context requireContext) {
        super(diffUtil);
        this.context = requireContext ;
        listener = (PlaylistItemsAdapter.onSelectPlaylistItem) requireContext;
        listener_menu = (onSelectPlaylistItemMenu) requireContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View playlist_item_view = LayoutInflater.from(context).inflate(R.layout.playlist_item_itemview , parent , false);
        return new PlaylistItemsAdapter.PlaylistItemHolder(playlist_item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaylistItem playlistItem = getItem(position);
        PlaylistItemHolder playlistItemHolder = (PlaylistItemHolder) holder;
        // load img
        if (playlistItem.isMusic()) {
            playlistItemHolder.playlistImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_disc));
            playlistItemHolder.playlistImg.setPadding(8 , 8 , 0 , 8);
        }
        else {
            playlistItemHolder.playlistImg.setImageDrawable(ContextCompat.getDrawable(context , R.drawable.ic_video_camera));
            Thread thread = getThread(playlistItem, playlistItemHolder);
            thread.start();
        }

            if (SearchTxt.trim().length() != 0) {
                int StartIndex, EndIndex;
                String name = playlistItem.getName();
                String summary = playlistItem.getSummary();
                SpannableStringBuilder spannableString;
                if (name.contains(SearchTxt)) {
                    StartIndex = name.indexOf(SearchTxt);  // word -- > world in --> title HelloWorld
                    // s = 6 , e = 11 the 11 th character will not be colored
                    EndIndex = StartIndex + SearchTxt.length();
                    spannableString = new SpannableStringBuilder(playlistItem.getName());
                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.lightBlue)), StartIndex, EndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    playlistItemHolder.name.setText(spannableString);
                }
                else
                    playlistItemHolder.name.setText(playlistItem.getName());

                if (summary.contains(SearchTxt)) {
                    StartIndex = summary.indexOf(SearchTxt);  // word -- > world in --> title HelloWorld
                    // s = 6 , e = 11 the 11 th character will not be colored
                    EndIndex = StartIndex + SearchTxt.length();
                    spannableString = new SpannableStringBuilder(playlistItem.getSummary());
                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.lightBlue)), StartIndex, EndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    playlistItemHolder.summary.setText(spannableString);
                }
                else
                    playlistItemHolder.summary.setText(playlistItem.getSummary());
            }
            else {
                playlistItemHolder.name.setText(playlistItem.getName());
                playlistItemHolder.summary.setText(playlistItem.getSummary());
            }
        }

    @NotNull
    private Thread getThread(PlaylistItem playlistItem, PlaylistItemHolder playlistItemHolder) {
        return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
                                playlistItem.getPath(),
                                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        if(thumbnail != null){
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(context)
                                                .load(thumbnail)
                                                .centerCrop()
                                                .into(playlistItemHolder.playlistImg);
                                    }
                                    catch (Exception exception) {
                                        Log.i("ab_do" , exception.getMessage());
                                    }

                                }
                            });

                        }
                    }
                });
    }

    public void refreshList(List<PlaylistItem> playlist_items , SwipeRefreshLayout refreshLayout) {
        if (SearchTxt.trim().length() == 0) {
            // user is not searching so ok refresh :)
            submitList(playlist_items);
        }
        if (refreshLayout!=null)
            refreshLayout.setRefreshing(false);
    }

    @Override
    public Filter getFilter() {
        return filter ;
    }

    public class PlaylistItemHolder extends RecyclerView.ViewHolder{
        TextView name ;
        TextView summary ;
        ImageView playlistImg ;
        ImageView playlistItemMenu ;
        public PlaylistItemHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.playlist_item_Name);
            summary = itemView.findViewById(R.id.playlist_item_summary);
            playlistImg = itemView.findViewById(R.id.playlist_item_img);
            playlistItemMenu = itemView.findViewById(R.id.playlist_item_menu);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelectItem(getItem(getAdapterPosition()));
                }
            });
            playlistItemMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener_menu.onSelectMenu(getItem(getAdapterPosition()));
                }
            });
        }
    }

    public interface onSelectPlaylistItem {
        void onSelectItem (PlaylistItem playlistItem) ;
    }

    public interface onSelectPlaylistItemMenu {
        void onSelectMenu (PlaylistItem playlistItem) ;
    }

}

