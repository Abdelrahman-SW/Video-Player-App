package com.loptr.kherod.uygdl.PlaylistDatabase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.loptr.kherod.uygdl.model.PlaylistItem;

import java.util.ArrayList;

@Entity
public class Playlist implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name ;
    private String thumbnail;
    private int videosSize ;
    private int musicsSize ;
    private boolean favourite  = false ;
    private ArrayList<PlaylistItem> playlistItems = new ArrayList<>() ;


    protected Playlist(Parcel in) {
        id = in.readLong();
        name = in.readString();
        thumbnail = in.readString();
        videosSize = in.readInt();
        musicsSize = in.readInt();
        favourite = in.readByte() != 0;
        playlistItems = in.createTypedArrayList(PlaylistItem.CREATOR);
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Ignore
    public Playlist(String name, String thumbnail, ArrayList<PlaylistItem> playlistItems) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.playlistItems = playlistItems;
    }

    @Ignore
    public Playlist() {
    }


    public Playlist(long id, String name, String thumbnail, int videosSize, int musicsSize, ArrayList<PlaylistItem> playlistItems) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.videosSize = videosSize;
        this.musicsSize = musicsSize;
        this.playlistItems = playlistItems;
        this.favourite = false ;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getVideosSize() {
        if (getPlaylistItems()==null) {
            setPlaylistItems(new ArrayList<>());
        }
        int  i = 0 ;
        for (PlaylistItem playlistItem : getPlaylistItems()) {
            if (!playlistItem.isMusic()) i++;
        }
        return i ;
    }

    public void setVideosSize(int videosSize) {
        this.videosSize = videosSize;
    }

    public int getMusicsSize() {
        if (getPlaylistItems()==null) {
            setPlaylistItems(new ArrayList<>());
        }
        int  i = 0 ;
        for (PlaylistItem playlistItem : getPlaylistItems()) {
            if (playlistItem.isMusic()) i++;
        }
        return i ;
    }

    public void setMusicsSize(int musicsSize) {
        this.musicsSize = musicsSize;
    }

    public ArrayList<PlaylistItem> getPlaylistItems() {
        if (playlistItems==null) {
            playlistItems = new ArrayList<>();
        }
        return playlistItems;
    }

    public void setPlaylistItems(ArrayList<PlaylistItem> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public boolean isSameAs(Playlist newItem) {
        return false ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(thumbnail);
        parcel.writeInt(videosSize);
        parcel.writeInt(musicsSize);
        parcel.writeByte((byte) (favourite ? 1 : 0));
        parcel.writeTypedList(playlistItems);
    }
}
