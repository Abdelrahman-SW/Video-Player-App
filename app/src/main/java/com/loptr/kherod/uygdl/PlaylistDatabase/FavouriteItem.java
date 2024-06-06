package com.loptr.kherod.uygdl.PlaylistDatabase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FavouriteItem implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id ;
    private String path ;
    private String name ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FavouriteItem() {
    }

    protected FavouriteItem(Parcel in) {
        path = in.readString();
        name = in.readString();
    }

    public static final Creator<FavouriteItem> CREATOR = new Creator<FavouriteItem>() {
        @Override
        public FavouriteItem createFromParcel(Parcel in) {
            return new FavouriteItem(in);
        }

        @Override
        public FavouriteItem[] newArray(int size) {
            return new FavouriteItem[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FavouriteItem(String path, String name) {
        this.path = path;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(name);
    }
}
