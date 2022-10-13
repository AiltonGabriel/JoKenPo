package tsi.ailton.android.jokenpo.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import tsi.ailton.android.jokenpo.MainActivity;

@Entity
public class AppState {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public Long id;

    private MainActivity.GAME_MODE game_mode;
    @Embedded
    private Scoreboard scoreboard;

    public AppState() {
    }

    public AppState(MainActivity.GAME_MODE game_mode, Scoreboard scoreboard) {
        this.game_mode = game_mode;
        this.scoreboard = scoreboard;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MainActivity.GAME_MODE getGame_mode() {
        return game_mode;
    }

    public void setGame_mode(MainActivity.GAME_MODE game_mode) {
        this.game_mode = game_mode;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
}
