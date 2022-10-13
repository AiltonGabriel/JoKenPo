package tsi.ailton.android.jokenpo.models.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tsi.ailton.android.jokenpo.models.RankingItem;

@Dao
public interface RankingItemDao {
    @Query("SELECT * FROM rankingitem")
    List<RankingItem> getAll();

    @Query("SELECT * FROM rankingItem where playerName = :pn")
    RankingItem findByPlayerName(String pn);

    @Insert
    void insert(RankingItem... rankingItems);

    @Update
    void update(RankingItem... rankingItems);

    @Query("DELETE FROM rankingitem")
    void clear();
}
