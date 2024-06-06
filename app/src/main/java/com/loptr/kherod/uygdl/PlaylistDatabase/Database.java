package com.loptr.kherod.uygdl.PlaylistDatabase;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.loptr.kherod.uygdl.R;


@androidx.room.Database(entities = {Playlist.class , FavouriteItem.class} , version = 1 , exportSchema = false)
@TypeConverters({DataConverter.class})
public abstract class Database extends RoomDatabase {

    private static Database database ; //singleton
    public abstract Dao Dao() ; // required

    public static synchronized Database getInstance (Context context) {
        if (database == null) {
            Callback callback = new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    ContentValues contentValue = new ContentValues();
                    contentValue.put("name" ,context.getString(R.string.Favourite_playlist));
                    contentValue.put("videosSize" ,0);
                    contentValue.put("musicsSize" ,0);
                    contentValue.put("favourite" , 1);
                    contentValue.put("id" , 0);
                    db.insert("Playlist" , OnConflictStrategy.REPLACE , contentValue);
                }

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                }
            };
            database = Room.databaseBuilder(context , Database.class , "PlaylistsDatabase")
                    .fallbackToDestructiveMigration() // re-create the database When the database version on the device does not match the latest schema version
                    // and the Migrations that would migrate old database schemas to the latest schema version are not found.
                    .addCallback(callback)
                    .allowMainThreadQueries()
                    // for testing only
                    // As we should run any actions on the database on background thread to avoid anr
                    .build();
        }
        return database ;
    }

}
