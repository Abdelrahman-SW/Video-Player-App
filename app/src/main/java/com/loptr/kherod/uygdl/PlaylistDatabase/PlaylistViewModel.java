package com.loptr.kherod.uygdl.PlaylistDatabase;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {
    Database database ;
    Dao dao ;
    private final LiveData<List<Playlist>> _playlists ;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        database = Database.getInstance(application);
        dao = database.Dao();
        _playlists = dao.getAllPlayLists();
    }

    public Playlist getPlaylistById(int id) {
        return dao.getPlaylistById(id);
    }

    public Playlist getFavouritePlaylist() {
        return dao.getFavouritePlaylist();
    }

    public LiveData<List<Playlist>> get_playlists() {
        return _playlists;
    }

    public void insertPlaylist(Playlist playlist) {
        dao.insertPlaylist(playlist);
    }

    public void updatePlaylist(Playlist playlist) {
        Log.i("Ab_do" , "updatePlaylist");
        dao.updatePlaylist(playlist);
    }

    public void deletePlaylist(Playlist playlist) {
        dao.deletePlaylist(playlist);
    }

    public List<FavouriteItem>  getAllFavouriteItems() {
        return dao.getAllFavouriteItems();
    }

    public void insertFavouriteItem(FavouriteItem favouriteItem) {
        dao.insertFavouriteItem(favouriteItem);
    }

    public void insertAllFavouriteItems(List<FavouriteItem> favouriteItems) {
        dao.insertAllFavouriteItems(favouriteItems);
    }

    public void deleteAllFavouriteItems() {
        dao.deleteAllFavouriteItems();
    }

    public void updateFavouriteItem(FavouriteItem favouriteItem) {
        dao.updateFavouriteItem(favouriteItem);
    }

    public void deleteFavouriteItem(FavouriteItem favouriteItem) {
        dao.deleteFavouriteItem(favouriteItem);
    }

    public void deleteAllPlaylists() {
        dao.deleteAllPlaylists();
    }
}
