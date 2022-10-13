package tsi.ailton.android.jokenpo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import tsi.ailton.android.jokenpo.databinding.ActivityMainBinding;
import tsi.ailton.android.jokenpo.models.AppState;
import tsi.ailton.android.jokenpo.models.RankingItem;
import tsi.ailton.android.jokenpo.models.Scoreboard;
import tsi.ailton.android.jokenpo.models.dao.AppDatabase;
import tsi.ailton.android.jokenpo.models.dao.AppStateDao;
import tsi.ailton.android.jokenpo.models.dao.RankingItemDao;

public class MainActivity extends AppCompatActivity {

    private enum PLAY_OPTION {
        ROCK(R.drawable.ic_rock_72, R.drawable.ic_rock_192),
        PAPER(R.drawable.ic_paper_72, R.drawable.ic_paper_192),
        SCISSORS(R.drawable.ic_scissors_72, R.drawable.ic_scissors_192);

        private final int imageResource72;
        private final int imageResource192;
        PLAY_OPTION(int imageResource96, int imageResource192){
            this.imageResource72 = imageResource96;
            this.imageResource192 = imageResource192;
        }

        public int getImageResource72() {
            return imageResource72;
        }

        public int getImageResource192() {
            return imageResource192;
        }
    }

    public enum GAME_MODE { RANDOM, ONLY_ROCK };

    private AppDatabase db;
    private RankingItemDao rankingItemDao;
    private AppStateDao appStateDao;

    private GAME_MODE game_mode;
    private Scoreboard scoreboard;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_jo_ken_po, R.id.nav_ranking, R.id.nav_game_mode)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        ranking.add(new Player("Ailton2", 3, 5, 10000L));
//        ranking.add(new Player("Ailton3", 2, 5, 250000L));
//        ranking.add(new Player("Ailton4", 3, 5, 8000L));

        db = AppDatabase.getInstance(getApplicationContext());
        rankingItemDao = db.rankingItemDao();
        appStateDao = db.appStateDao();

        AppState appState = appStateDao.get();

        if(appState != null){
            game_mode = appState.getGame_mode();
            scoreboard = appState.getScoreboard();
        } else {
            game_mode = GAME_MODE.RANDOM;
            scoreboard = new Scoreboard();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveAppState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void saveAppState(){
        AppState appState = appStateDao.get();
        if(appState != null){
            appState.setGame_mode(this.game_mode);
            appState.setScoreboard(this.scoreboard);
            appStateDao.update(appState);
        } else {
            appStateDao.insert(new AppState(this.game_mode, this.scoreboard));
        }
    }

    public boolean isGameFinished() {
        return scoreboard.isGameFinished();
    }

    public List<RankingItem> getRanking() {
        List<RankingItem> rankingItems = rankingItemDao.getAll();
        Collections.sort(rankingItems);
        return rankingItems;
    }

    public GAME_MODE getGame_mode() {
        return game_mode;
    }

    public void setGame_mode(GAME_MODE game_mode) {
        this.game_mode = game_mode;
    }

    public void resetScoreboard(View view) {
        scoreboard.reset();
        updateScoreboardGui(view);
        saveAppState();
        playAgain(view);
    }

    public void updateScoreboardGui(View view) {
        TextView playerScore = (TextView) findViewById(R.id.player_score_textView);
        TextView computerScore = (TextView) findViewById(R.id.computer_score_textView);

        playerScore.setText(String.valueOf(scoreboard.getPlayerScore()));
        computerScore.setText(String.valueOf(scoreboard.getComputerScore()));
    }

    public void playAgain(View view) {

        Button playAgainButton = (Button) findViewById(R.id.play_again_button);
        playAgainButton.setVisibility(Button.GONE);

        ImageView computerChoiceImageView = (ImageView) findViewById(R.id.computer_choice_imageView);
        computerChoiceImageView.setImageResource(R.color.computer_choose_empty_background_color);

        ImageButton imageButtons[] = getImageButtons();

        setImageButtonsEnable(true, imageButtons);
        for(ImageButton imageButton : imageButtons) {
            imageButton.getBackground().clearColorFilter();
        }

        ((TextView) findViewById(R.id.player_choice_textView)).setText(R.string.player_choice_label_text);
    }

    public void playRock(View view) {
        makePlay(view, (ImageButton) view, PLAY_OPTION.ROCK);
    }

    public void playPaper(View view){
        makePlay(view, (ImageButton) view, PLAY_OPTION.PAPER);
    }

    public void playScissors(View view) {
        makePlay(view, (ImageButton) view, PLAY_OPTION.SCISSORS);
    }

    private void makePlay(View view, ImageButton imageButton, PLAY_OPTION choice) {
        PLAY_OPTION computerChoice = generateComputerPlay();
        ImageButton imageButtons[] = getImageButtons();

        setImageButtonsEnable(false, imageButtons);
        imageButton.getBackground().setColorFilter(androidx.appcompat.R.attr.colorPrimary, PorterDuff.Mode.MULTIPLY);

        ImageView computerChoiceImageView = (ImageView) findViewById(R.id.computer_choice_imageView);
        computerChoiceImageView.setImageResource(computerChoice.getImageResource192());

        TextView playerChoiceTextView = (TextView) findViewById(R.id.player_choice_textView);

        boolean isGameFinished = false;
        if (choice == computerChoice){
            playerChoiceTextView.setText(R.string.draw_message);
        } else {

            boolean won = false;

            switch (choice) {
                case ROCK:
                    if (computerChoice == PLAY_OPTION.SCISSORS)
                        won = true;
                    break;
                case PAPER:
                    if (computerChoice == PLAY_OPTION.ROCK)
                        won = true;
                    break;
                case SCISSORS:
                    if (computerChoice == PLAY_OPTION.PAPER)
                        won = true;
                    break;
            }

            playerChoiceTextView.setText((won) ? R.string.win_message : R.string.lose_message);

            isGameFinished = (won) ? scoreboard.addPointPlayer() : scoreboard.addPointComputer();

        }

        Button playAgainButton = (Button) findViewById(R.id.play_again_button);
        playAgainButton.setVisibility(Button.VISIBLE);

        if (isGameFinished) {
            if(scoreboard.isWinner())
                addPlayerToRankingDialog();

            resetScoreboard(view);
        }

        updateScoreboardGui(view);
    }

    private ImageButton[] getImageButtons(){
        ImageButton imageButtons[] = new ImageButton[3];

        imageButtons[0] = (ImageButton) findViewById(R.id.rock_imageButton);
        imageButtons[1] = (ImageButton) findViewById(R.id.paper_imageButton);
        imageButtons[2] = (ImageButton) findViewById(R.id.scissors_imageButton);

        return imageButtons;
    }

    private void setImageButtonsEnable(Boolean enable, ImageButton imageButtons[]) {
        for(ImageButton imageButton : imageButtons) {
            imageButton.setEnabled(enable);
        }
    }

    public PLAY_OPTION generateComputerPlay(){
        if(this.game_mode == GAME_MODE.RANDOM){
            return PLAY_OPTION.values()[
                new SecureRandom().nextInt(
                        PLAY_OPTION.values().length
                )
            ];
        } //else if(this.game_mode == GAME_MODE.ONLY_ROCK){
            return PLAY_OPTION.ROCK;
        //}
    }

    /*
    *  Ranking
    * */
    private void addPlayerToRankingDialog() {
        RankingItem rankingItem = new RankingItem("", scoreboard.getComputerScore(), scoreboard.getPlayerScore(), scoreboard.getGameElapsedTime());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_player_scoreboard_msg);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.add_player_scoreboard_ok_button_text, null);
        builder.setNegativeButton(R.string.add_player_scoreboard_cancel_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String txt = input.getText().toString();

                        if(!txt.isEmpty()){
                            rankingItem.setPlayerName(input.getText().toString());
                            addPlayerToRanking(rankingItem);
                            dialog.dismiss();
                            Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_ranking);
                        } else{
                            Toast.makeText(getApplicationContext(), R.string.add_player_scoreboard_empty_input_msg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void addPlayerToRanking (RankingItem rankingItem){
        RankingItem rankingItemAux = rankingItemDao.findByPlayerName(rankingItem.getPlayerName());

        if(rankingItemAux == null){
            rankingItemDao.insert(rankingItem);
        } else {
            if(rankingItemAux.compareTo(rankingItem) > 0){
                rankingItem.setId(rankingItemAux.getId());
                rankingItemDao.update();
            } else {
                Toast.makeText(this, R.string.better_score_already_registered_msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void clearRanking(){
        rankingItemDao.clear();
    }

}