package tsi.ailton.android.jokenpo.models.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import tsi.ailton.android.jokenpo.models.AppState;
import tsi.ailton.android.jokenpo.models.RankingItem;

@Database(entities = {RankingItem.class,AppState.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RankingItemDao rankingItemDao();
    public abstract AppStateDao appStateDao();

    private static AppDatabase instance;
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "jokenpo")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
