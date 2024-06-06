package com.loptr.kherod.uygdl.model;

import android.os.Parcel;
import android.os.Parcelable;


public class MusicModel implements Parcelable {
    private String title ;
    private String artist ;
    private String url ;
    private boolean isChecked = false;
    private boolean isFavourite;

    protected MusicModel(Parcel in) {
        title = in.readString();
        artist = in.readString();
        url = in.readString();
        isChecked = in.readByte() != 0;
        isFavourite = in.readByte() != 0;
    }

    public static final Creator<MusicModel> CREATOR = new Creator<MusicModel>() {
        @Override
        public MusicModel createFromParcel(Parcel in) {
            return new MusicModel(in);
        }

        @Override
        public MusicModel[] newArray(int size) {
            return new MusicModel[size];
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

    public MusicModel(String title, String artist, String url , boolean isFavourite) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.isFavourite = isFavourite ;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }


    public String getUrl() {
        return url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(url);
        parcel.writeByte((byte) (isChecked ? 1 : 0));
        parcel.writeByte((byte) (isFavourite ? 1 : 0));
    }
}
