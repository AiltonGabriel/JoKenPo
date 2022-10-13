package tsi.ailton.android.jokenpo.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.Duration;
import java.util.Calendar;

import tsi.ailton.android.jokenpo.models.dao.converters.CalendarConverter;

@Entity
@TypeConverters({CalendarConverter.class})
public class Scoreboard {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    private static final int POINTS_TO_WIN = 5;

    private int computerScore;
    private int playerScore;

    private Calendar start;
    private Calendar end;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getComputerScore() {
        return computerScore;
    }

    public void setComputerScore(int computerScore) {
        this.computerScore = computerScore;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public boolean addPointComputer() {
        if (start == null )
            start = Calendar.getInstance();
        computerScore++;
        return isGameFinished();
    }

    public boolean addPointPlayer() {
        if (start == null )
            start = Calendar.getInstance();
        playerScore++;
        return isGameFinished();
    }

    public void reset(){
        start = null;
        this.computerScore = 0;
        this.playerScore = 0;
    }

    public boolean isGameFinished () {
        return ((computerScore >= POINTS_TO_WIN) || (playerScore >= POINTS_TO_WIN));
    }

    public boolean isWinner() {
        if (playerScore >= POINTS_TO_WIN && playerScore > computerScore)
            return true;
        return false;
    }

    public long getGameElapsedTime() {
        if(start == null)
            return 0;

        end = Calendar.getInstance();
        return (end.getTimeInMillis() - start.getTimeInMillis());
    }
}
