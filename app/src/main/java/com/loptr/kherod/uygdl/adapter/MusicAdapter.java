package com.loptr.kherod.uygdl.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.loptr.kherod.uygdl.R;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.loptr.kherod.uygdl.model.MusicModel;

import java.util.ArrayList;
import java.util.List;


public class MusicAdapter extends ListAdapter<MusicModel,RecyclerView.ViewHolder> implements Filterable {

    private final Context context;
    private final List<MusicModel> musics = new ArrayList<>();
    private final onMusicSelected listener ;
    private boolean isCheckable = false ;
    private onMusicClickCheckBox checkBoxListener ;
    private onMusicMenuClicked menuClickedListener ;
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List <MusicModel> MusicFilters = new ArrayList<>() ;
            if (constraint == null || constraint.length()==0) {
                MusicFilters.clear();
                // Search Result Not Found // user exit the search view
                SearchTxt = "" ;
                MusicFilters.addAll(musics) ;
            }
            else {
                MusicFilters.clear();
                SearchTxt  = constraint.toString().toLowerCase().trim() ;
                for (MusicModel music : musics) {
                    if (music.getTitle().toLowerCase().trim().contains(SearchTxt)
                    || music.getArtist().toLowerCase().trim().contains(SearchTxt) ) {
                        MusicFilters.add(music) ;
                    }
                }
            }
            FilterResults filterResults = new FilterResults() ;
            filterResults.values = MusicFilters ;
            return filterResults ;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Full_data = false ;
            if (results.values != null) {
                Log.i("ab_do", "size " + ((List<MusicModel>) results.values).size());
                submitList((List<MusicModel>) results.values);
            }
        }
    };

    @Override
    public void submitList(List<MusicModel> values) {
        if (values!=null) {
            super.submitList(values);
            if (Full_data) {
                musics.clear();
                musics.addAll(values);
            }
            notifyDataSetChanged();
        }
    }

    private String SearchTxt = "";
    public boolean Full_data = false;

    private static final DiffUtil.ItemCallback<MusicModel> diffUtil = new DiffUtil.ItemCallback<MusicModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull MusicModel oldItem, @NonNull MusicModel newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MusicModel oldItem, @NonNull MusicModel newItem) {
            if (oldItem.getTitle() == null || oldItem.getArtist() == null || oldItem.getUrl() == null) {
                return false ;
            }
            return  oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getArtist().equals(newItem.getArtist()) &&
                    oldItem.getUrl().equals(newItem.getUrl());
        }
    };


    public MusicAdapter(Context requireContext , boolean  isCheckable ) {
         super(diffUtil);
         this.context = requireContext ;
         listener = (onMusicSelected) requireContext;
         checkBoxListener = (onMusicClickCheckBox) requireContext;
         this.isCheckable = isCheckable ;
         this.menuClickedListener = (onMusicMenuClicked) requireContext;
    }

    @NonNull
    @Override
    public MusicsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View music_view = LayoutInflater.from(context).inflate(R.layout.layout_music_item , parent , false);
        return new MusicsHolder(music_view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MusicModel musicModel = getItem(position);
        MusicsHolder musicsHolder = (MusicsHolder) holder;
        if (isCheckable) {
            Log.i("ab_do" , "Checkable");
            musicsHolder.checkbox.setVisibility(View.VISIBLE);
            musicsHolder.checkbox.setChecked(musicModel.isChecked());
            musicsHolder.menu.setVisibility(View.GONE);
        }
        else {
            musicsHolder.checkbox.setVisibility(View.GONE);
            musicsHolder.menu.setVisibility(View.VISIBLE);
        }
        if ( SearchTxt.trim().length()!= 0){
            int StartIndex , EndIndex;
            String title = musicModel.getTitle().trim().toLowerCase() ;
            String artist = musicModel.getArtist().trim().toLowerCase() ;
            SpannableStringBuilder spannableString;

            if (title.contains(SearchTxt)) {
                StartIndex = title.indexOf(SearchTxt) ;  // word -- > world in --> title HelloWorld
                // s = 6 , e = 11 the 11 th character will not be colored
                EndIndex = StartIndex + SearchTxt.length() ;
                spannableString = new SpannableStringBuilder(musicModel.getTitle()) ;
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context , R.color.lightBlue)), StartIndex , EndIndex , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) ;
                musicsHolder.title.setText(spannableString);
            }
            else musicsHolder.title.setText(musicModel.getTitle());
            if (artist.contains(SearchTxt)) {
                StartIndex = artist.indexOf(SearchTxt) ;  // word -- > world in --> title HelloWorld
                // s = 6 , e = 11 the 11 th character will not be colored
                EndIndex = StartIndex + SearchTxt.length() ;
                spannableString = new SpannableStringBuilder(musicModel.getArtist()) ;
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context , R.color.lightBlue)), StartIndex , EndIndex , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) ;
                musicsHolder.artist.setText(spannableString);
            }
            else musicsHolder.artist.setText(musicModel.getArtist());
        }
        else {
            musicsHolder.title.setText(musicModel.getTitle());
            musicsHolder.artist.setText(musicModel.getArtist());
        }
    }



    public void refreshList(List<MusicModel> musics , SwipeRefreshLayout refreshLayout) {
        if (SearchTxt.trim().length() == 0) {
            // user is not searching so ok refresh :)
            submitList(musics);
        }
        if (refreshLayout!=null)
        refreshLayout.setRefreshing(false);
    }

    @Override
    public Filter getFilter() {
        return filter ;
    }

    public class MusicsHolder extends RecyclerView.ViewHolder{
         CheckBox checkbox;
         TextView title ;
         TextView artist ;
         ImageView menu ;
        public MusicsHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.music_title);
            artist = itemView.findViewById(R.id.music_artist);
            checkbox = itemView.findViewById(R.id.checkbox) ;
            menu = itemView.findViewById(R.id.menu);

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       menuClickedListener.onClickMusicMenu(getItem(getAdapterPosition()));
                }
            });

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBoxListener.onCheckBoxClicked(getItem(getAdapterPosition()) , checkbox.isChecked());
                    getItem(getAdapterPosition()).setChecked(checkbox.isChecked());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelectMusic(musics.get(getAdapterPosition()).getUrl());
                }
            });
        }
    }

    public interface onMusicSelected {
        void onSelectMusic (String url) ;
    }
    public interface onMusicClickCheckBox {
        void onCheckBoxClicked (MusicModel musicModel , boolean isChecked) ;
    }

    public interface onMusicMenuClicked {
        void onClickMusicMenu (MusicModel musicModel) ;
    }
}
