package com.loptr.kherod.uygdl.PlaylistDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@androidx.room.Dao
public interface Dao {

@Insert
void insertPlaylist (Playlist playlist) ;
@Update
void updatePlaylist (Playlist playlist) ;
@Delete
void deletePlaylist (Playlist playlist) ;

@Query("Select * from Playlist where id = :id")
Playlist getPlaylistById(int id);

@Query("Select * from Playlist where id = 0")
Playlist getFavouritePlaylist();

@Query("Delete from Playlist where favourite = 0")
void deleteAllPlaylists();

@Query("select * from Playlist")
LiveData<List<Playlist>> getAllPlayLists();


    @Query("Delete from FavouriteItem")
    void deleteAllFavouriteItems();
    @Insert
    void insertFavouriteItem (FavouriteItem favouriteItem) ;
    @Update
    void updateFavouriteItem (FavouriteItem favouriteItem) ;
    @Delete
    void deleteFavouriteItem (FavouriteItem favouriteItem) ;
    @Query("select * from FavouriteItem")
    List<FavouriteItem> getAllFavouriteItems();
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllFavouriteItems(List<FavouriteItem> FavouriteItems) ;
}
