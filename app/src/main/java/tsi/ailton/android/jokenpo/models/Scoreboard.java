package tsi.ailton.android.jokenpo.models;

import java.time.Duration;
import java.util.Calendar;

public class Scoreboard {
    private static final int POINTS_TO_WIN = 5;

    private int computerScore;
    private int playerScore;

    private Calendar start;
    private Calendar end;

    public int getComputerScore() {
        return computerScore;
    }

    public int getPlayerScore() {
        return playerScore;
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
