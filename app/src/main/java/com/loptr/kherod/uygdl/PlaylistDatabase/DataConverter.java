package com.loptr.kherod.uygdl.PlaylistDatabase;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loptr.kherod.uygdl.model.PlaylistItem;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataConverter {
    @TypeConverter
    public String fromPlaylistItems(ArrayList<PlaylistItem> playlistItems) {
        if (playlistItems == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<PlaylistItem>>() {

        }.getType();
        return gson.toJson(playlistItems, type);
    }

    @TypeConverter
    public ArrayList<PlaylistItem> toPlaylistItems(String playlistItemsString) {
        if (playlistItemsString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<PlaylistItem>>() {}.getType();
        return gson.fromJson(playlistItemsString, type);
    }
}