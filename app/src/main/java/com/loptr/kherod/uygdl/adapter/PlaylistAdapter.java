package com.loptr.kherod.uygdl.adapter;

import android.content.Context;
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
import com.loptr.kherod.uygdl.PlaylistDatabase.Playlist;
import com.loptr.kherod.uygdl.R;
import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends ListAdapter<Playlist, RecyclerView.ViewHolder> implements Filterable  {

    private final Context context;

    private final List<Playlist> playlists = new ArrayList<>();
    private final PlaylistAdapter.onPlaylistSelected playlistListener ;
    private final PlaylistAdapter.onPlayMenuSelected menuListener ;
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List <Playlist> playlistsFilters = new ArrayList<>() ;
            if (constraint == null || constraint.length()==0) {
                playlistsFilters.clear();
                // Search Result Not Found // user exit the search view
                SearchTxt = "" ;
                playlistsFilters.addAll(playlists) ;
            }
            else {
                playlistsFilters.clear();
                SearchTxt  = constraint.toString().toLowerCase().trim() ;
                for (Playlist playlist : playlists) {
                    if (playlist.getName().toLowerCase().trim().contains(SearchTxt)) {
                        playlistsFilters.add(playlist) ;
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
            Log.i("ab_do" , "size " + ((List<Playlist>) results.values).size());
            submitList((List<Playlist>) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public void submitList(List<Playlist> values) {
        Log.i("ab_do" , "submitList");
        if (values!=null) {
            super.submitList(values);
            if (Full_data) {
                playlists.clear();
                playlists.addAll(values);
            }
            //notifyDataSetChanged();
        }
    }

    private String SearchTxt = "";
    public boolean Full_data = false;

    private static final DiffUtil.ItemCallback<Playlist> diffUtil = new DiffUtil.ItemCallback<Playlist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
            return oldItem.getId() == (newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
            Log.i("ab_do " , "old " + oldItem.getName());
            Log.i("ab_do " , "newItem " + newItem.getName());
            return oldItem.isSameAs(newItem);
        }
    };


    public PlaylistAdapter(Context requireContext) {
        super(diffUtil);
        this.context = requireContext ;
        playlistListener = (PlaylistAdapter.onPlaylistSelected) requireContext;
        menuListener = (PlaylistAdapter.onPlayMenuSelected) requireContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View playlist_view = LayoutInflater.from(context).inflate(R.layout.playlist_item , parent , false);
        return new PlaylistHolder(playlist_view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Playlist playlist = getItem(position);
        PlaylistAdapter.PlaylistHolder playlistHolder = (PlaylistHolder) holder;
        if (SearchTxt.trim().length()!= 0){
            int StartIndex , EndIndex;
            String name = playlist.getName().trim().toLowerCase() ;
            SpannableStringBuilder spannableString;
            if (name.contains(SearchTxt)) {
                StartIndex = name.indexOf(SearchTxt) ;  // word -- > world in --> title HelloWorld
                // s = 6 , e = 11 the 11 th character will not be colored
                EndIndex = StartIndex + SearchTxt.length() ;
                spannableString = new SpannableStringBuilder(playlist.getName()) ;
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context , R.color.lightBlue)), StartIndex , EndIndex , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) ;
                playlistHolder.name.setText(spannableString);
            }
            else playlistHolder.name.setText(playlist.getName());
        }
        else {
            playlistHolder.name.setText(playlist.getName());
            playlistHolder.summary.setText(SetPlaylistSummary(playlist.getVideosSize(), playlist.getMusicsSize()));
        }
        if (playlist.isFavourite()) {
            Log.i("ab_do" , "yes");
            playlistHolder.playlistMenu.setVisibility(View.GONE);
            playlistHolder.playlistImg.setImageResource(R.drawable.ic_playlist__4_);
        }
        else {
            playlistHolder.playlistMenu.setVisibility(View.VISIBLE);
            playlistHolder.playlistImg.setImageDrawable(ContextCompat.getDrawable(context , R.drawable.ic_playlist__3_));
        }
    }

    private String SetPlaylistSummary(int videosSize, int musicsSize) {
        return String.format(context.getString(R.string.playlist_summary) , videosSize , musicsSize);
    }


    public void refreshList(List<Playlist> playlists , SwipeRefreshLayout refreshLayout) {
        if (SearchTxt.trim().length() == 0) {
            // user is not searching so ok refresh :)
            submitList(playlists);
        }
        if (refreshLayout!=null)
            refreshLayout.setRefreshing(false);
    }

    @Override
    public Filter getFilter() {
        return filter ;
    }

    public class PlaylistHolder extends RecyclerView.ViewHolder{
        TextView name ;
        TextView summary ;
        ImageView playlistImg ;
        ImageView playlistMenu ;
        public PlaylistHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.playlist_Name);
            summary = itemView.findViewById(R.id.playlist_summary);
            playlistImg = itemView.findViewById(R.id.playlist_img);
            playlistMenu = itemView.findViewById(R.id.playlist_menu);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playlistListener.onSelectPlaylist(getItem(getAdapterPosition()));
                }
            });
            playlistMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuListener.onSelectMenu(getItem(getAdapterPosition()));
                }
            });
        }
    }

    public interface onPlayMenuSelected {
        void onSelectMenu (Playlist playlist) ;
    }

    public interface onPlaylistSelected {
        void onSelectPlaylist (Playlist playlist) ;
    }


}

