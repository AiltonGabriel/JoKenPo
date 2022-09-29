package tsi.ailton.android.jokenpo.models;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Player implements Comparable<Player> {
    private String nome;
    private int computerScore;
    private int playerScore;
    private Long gameTime;

    public Player() {
    }

    public Player(String nome, int computerScore, int playerScore, Long durationTime) {
        this.nome = nome;
        this.computerScore = computerScore;
        this.playerScore = playerScore;
        this.gameTime = durationTime;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
    public int compareTo(Player player) {
        return (Math.abs(this.computerScore - this.playerScore) != Math.abs(player.computerScore - player.playerScore))
                ? Integer.compare(player.playerScore - player.computerScore, this.playerScore - this.computerScore)
                : Long.compare(this.gameTime, player.gameTime);
    }

    @Override
    public String toString() {
        return String.format("Jogador: %s \t Placar: %d x %d \t Tempo: %02d:%02d",
                nome, playerScore, computerScore,
                TimeUnit.MILLISECONDS.toMinutes(gameTime), TimeUnit.MILLISECONDS.toSeconds(gameTime));
    }

}
