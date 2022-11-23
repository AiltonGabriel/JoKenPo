package tsi.ailton.android.jokenpo.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.concurrent.TimeUnit;

@Entity
public class RankingItem implements Comparable<RankingItem> {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    private String playerName;
    private int computerScore;
    private int playerScore;
    private Long gameTime;

    public RankingItem() {
    }

    public RankingItem(String playerName, int computerScore, int playerScore, Long durationTime) {
        this.playerName = playerName;
        this.computerScore = computerScore;
        this.playerScore = playerScore;
        this.gameTime = durationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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

    public Long getGameTime() {
        return gameTime;
    }

    public void setGameTime(Long gameTime) {
        this.gameTime = gameTime;
    }

    public int getScoreDifference(){
        return Math.abs(computerScore - playerScore);
    }

    @Override
    public int compareTo(RankingItem rankingItem) {
        return (Math.abs(this.computerScore - this.playerScore) != Math.abs(rankingItem.computerScore - rankingItem.playerScore))
                ? Integer.compare(rankingItem.playerScore - rankingItem.computerScore, this.playerScore - this.computerScore)
                : Long.compare(this.gameTime, rankingItem.gameTime);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("Jogador: %s \t Placar: %d x %d \t Tempo: %02d:%02d",
                playerName, playerScore, computerScore,
                TimeUnit.MILLISECONDS.toMinutes(gameTime), TimeUnit.MILLISECONDS.toSeconds(gameTime));
    }

}
