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
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.jiajunhui.xapp.medialoader.bean.VideoFolder;
import com.jiajunhui.xapp.medialoader.bean.VideoItem;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.databinding.LayoutFolderItemBinding;
import java.util.ArrayList;
import java.util.List;

public class FoldersAdapter extends ListAdapter<VideoFolder ,RecyclerView.ViewHolder> implements Filterable {

    private final String TAG = FoldersAdapter.class.getSimpleName();
    private final Context mContext;
    private List<VideoFolder> folderList = new ArrayList<>() ;
    onFolderSelected listener ;
    LayoutFolderItemBinding binding;
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.i("ab_do" , "" + constraint.toString().trim().length());
            List <VideoFolder> FilterFolders = new ArrayList<>() ;
            if (constraint.length() == 0) {
                FilterFolders.clear();
                // Search Result Not Found // user exit the search view
                SearchTxt = "" ;
                FilterFolders.addAll(folderList) ;
            }
            else {
                FilterFolders.clear();
                SearchTxt  = constraint.toString().toLowerCase().trim() ;
                for (VideoFolder folder : folderList) {
                    if (folder.getName().toLowerCase().trim().contains(SearchTxt)) {
                        Log.i("ab_do" , "yes");
                        FilterFolders.add(folder) ;
                    }
                }
            }
            FilterResults filterResults = new FilterResults() ;
            filterResults.values = FilterFolders ;
            return filterResults ;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Full_data = false ;
            submitList((List<VideoFolder>) results.values);
        }
    };



    @Override
    public void submitList(List<VideoFolder> values) {
        if (values!=null) {
            super.submitList(values);
            if (Full_data) {
                folderList.clear();
                folderList.addAll(values);
            }
            notifyDataSetChanged();
        }
    }

    private String SearchTxt = "";
    public boolean Full_data = false;
    private static final DiffUtil.ItemCallback<VideoFolder> diffUtil = new DiffUtil.ItemCallback<VideoFolder>() {
        @Override
        public boolean areItemsTheSame(@NonNull VideoFolder oldItem, @NonNull VideoFolder newItem) {
            if (oldItem == null || newItem == null) return false ;
            if (oldItem.getId() == null || newItem.getId() == null) return false ;
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull VideoFolder oldItem, @NonNull VideoFolder newItem) {
            if (oldItem == null || newItem == null) return false ;
            if (oldItem.getName() == null || newItem.getName() == null) return false ;
            return oldItem.getName().equals(newItem.getName());
        }
    };

    public FoldersAdapter(Context mContext) {
        super(diffUtil);
        this.mContext = mContext;
        this.listener = (onFolderSelected) mContext;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.layout_folder_item, parent, false);

        return new FolderHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        FolderHolder folderHolder = (FolderHolder) holder;
        VideoFolder folder = getItem(position);
        folderHolder.mBinding.folderItemCount.setText(String.valueOf(folder.getItems().size()));
        if (SearchTxt.trim().length()!=0){
            int StartIndex , EndIndex;
            String name = folder.getName().trim().toLowerCase() ;
            SpannableStringBuilder spannableString;
            if (name.contains(SearchTxt)) {
                StartIndex = name.indexOf(SearchTxt) ;  // word -- > world in --> title HelloWorld
                // s = 6 , e = 11 the 11 th character will not be colored
                EndIndex = StartIndex + SearchTxt.length() ;
                spannableString = new SpannableStringBuilder(folder.getName()) ;
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext , R.color.lightBlue)), StartIndex , EndIndex , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) ;
                folderHolder.mBinding.folderName.setText(spannableString);
            }
            else folderHolder.mBinding.folderName.setText(folder.getName());
        }
        else {
            folderHolder.mBinding.folderName.setText(folder.getName());
        }

    }


    @Override
    public Filter getFilter() {
        return filter ;
    }

    public void refreshList(List<VideoFolder> videoFolders , SwipeRefreshLayout refreshLayout) {
        if (SearchTxt.trim().length() == 0) {
            // user is not searching so ok refresh :)
            submitList(videoFolders);
        }
        if (refreshLayout!=null)
        refreshLayout.setRefreshing(false);
    }

    public class FolderHolder extends RecyclerView.ViewHolder{

        LayoutFolderItemBinding mBinding;
        public FolderHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            itemView.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ab_do" , "Click");
                    listener.onSelectFolder(folderList.get(getAdapterPosition()).getItems());
                }
            });
        }
    }

    public void RefreshFolders (List<VideoFolder> videoFolders , SwipeRefreshLayout refreshLayout) {
        this.folderList = videoFolders ;
        notifyDataSetChanged();
        if (refreshLayout != null)
        refreshLayout.setRefreshing(false);
    }

    public interface onFolderSelected {
        void onSelectFolder (List<VideoItem> videoItems) ;
    }
}
