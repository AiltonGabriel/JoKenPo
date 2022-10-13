package tsi.ailton.android.jokenpo.models.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import tsi.ailton.android.jokenpo.models.AppState;
import tsi.ailton.android.jokenpo.models.RankingItem;

@Dao
public interface AppStateDao {
    @Query("SELECT * FROM appstate")
    AppState get();

    @Insert
    void insert(AppState... appStates);

    @Update
    void update(AppState... appStates);
}
