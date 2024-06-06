package com.loptr.kherod.uygdl.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.jiajunhui.xapp.medialoader.bean.VideoItem;

public class VideoModel implements Parcelable {
    private VideoItem videoItem ;
    private Bitmap thumbnail;
    private boolean isChecked = false;
    private boolean isFavourite = false ;


    protected VideoModel(Parcel in) {
        thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
        isChecked = in.readByte() != 0;
        isFavourite = in.readByte() != 0;
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public VideoModel(VideoItem videoItem, Bitmap thumbnail) {
        this.videoItem = videoItem;
        this.thumbnail = thumbnail;
    }

    public VideoModel(VideoItem videoItem, Bitmap thumbnail , boolean isFavourite) {
        this.videoItem = videoItem;
        this.thumbnail = thumbnail;
        this.isFavourite = isFavourite ;
    }


    public VideoItem getVideoItem() {
        return videoItem;
    }

    public void setVideoItem(VideoItem videoItem) {
        this.videoItem = videoItem;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean TheSameContentOf(VideoModel oldItem) {
        return oldItem.getVideoItem().getId() == this.getVideoItem().getId()
                && oldItem.getVideoItem().getDisplayName().equals(this.getVideoItem().getDisplayName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(thumbnail, i);
        parcel.writeByte((byte) (isChecked ? 1 : 0));
        parcel.writeByte((byte) (isFavourite ? 1 : 0));
    }
}
