package com.loptr.kherod.uygdl.model;

import android.os.Parcel;
import android.os.Parcelable;


public class PlaylistItem implements Parcelable {

    private int Playlist_id ;
    private String name ;
    private String summary ;
    private String path ;
    private boolean isMusic ;

    public PlaylistItem() {

    }

    public PlaylistItem(int playlist_id, String name, String summary, String path, boolean isMusic) {
        Playlist_id = playlist_id;
        this.name = name;
        this.summary = summary;
        this.path = path;
        this.isMusic = isMusic;
    }


    protected PlaylistItem(Parcel in) {
        Playlist_id = in.readInt();
        name = in.readString();
        summary = in.readString();
        path = in.readString();
        isMusic = in.readByte() != 0;
    }

    public static final Creator<PlaylistItem> CREATOR = new Creator<PlaylistItem>() {
        @Override
        public PlaylistItem createFromParcel(Parcel in) {
            return new PlaylistItem(in);
        }

        @Override
        public PlaylistItem[] newArray(int size) {
            return new PlaylistItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setMusic(boolean music) {
        isMusic = music;
    }

    public int getPlaylist_id() {
        return Playlist_id;
    }

    public void setPlaylist_id(int playlist_id) {
        Playlist_id = playlist_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Playlist_id);
        dest.writeString(name);
        dest.writeString(summary);
        dest.writeString(path);
        dest.writeByte((byte) (isMusic ? 1 : 0));
    }
}

