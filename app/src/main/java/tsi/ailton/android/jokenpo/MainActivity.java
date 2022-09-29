package tsi.ailton.android.jokenpo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tsi.ailton.android.jokenpo.databinding.ActivityMainBinding;
import tsi.ailton.android.jokenpo.models.Player;
import tsi.ailton.android.jokenpo.models.Scoreboard;

public class MainActivity extends AppCompatActivity {

    private enum PLAY_OPTIONS {
        ROCK(R.drawable.ic_rock_72, R.drawable.ic_rock_192),
        PAPER(R.drawable.ic_paper_72, R.drawable.ic_paper_192),
        SCISSORS(R.drawable.ic_scissors_72, R.drawable.ic_scissors_192);

        private final int imageResource72;
        private final int imageResource192;
        PLAY_OPTIONS(int imageResource96, int imageResource192){
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

    Scoreboard scoreboard = new Scoreboard();
    private boolean isGameFinished = false;
    private Player player;
    private List<Player> ranking;

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
                R.id.nav_jo_ken_po, R.id.nav_ranking)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ranking = new ArrayList<>();
//        ranking.add(new Player("Ailton", 2, 5, 200000L));
//        ranking.add(new Player("Ailton2", 3, 5, 10000L));
//        ranking.add(new Player("Ailton3", 2, 5, 250000L));
//        ranking.add(new Player("Ailton4", 3, 5, 8000L));
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateJoKenPoGuiInfo() {

        TextView playerScore = (TextView) findViewById(R.id.player_score_textView);
        TextView computerScore = (TextView) findViewById(R.id.computer_score_textView);

        playerScore.setText(String.valueOf(scoreboard.getPlayerScore()));
        computerScore.setText(String.valueOf(scoreboard.getComputerScore()));

        this.player = null;
    }

    public void resetScoreboard(View view) {
        scoreboard.reset();
        isGameFinished = false;
        updateJoKenPoGuiInfo();
        playAgain(view);
    }

    public void playAgain(View view) {
        if(isGameFinished){
            resetScoreboard(view);
        } else {
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
    }

    public void playRock(View view) {
        makePlay((ImageButton) view, PLAY_OPTIONS.ROCK);
    }

    public void playPaper(View view){
        makePlay((ImageButton) view, PLAY_OPTIONS.PAPER);
    }

    public void playScissors(View view) {
        makePlay((ImageButton) view, PLAY_OPTIONS.SCISSORS);
    }

    private void makePlay(ImageButton imageButton, PLAY_OPTIONS choice) {
        PLAY_OPTIONS computerChoice = generateComputerPlay();

        ImageButton imageButtons[] = getImageButtons();

        setImageButtonsEnable(false, imageButtons);
        imageButton.getBackground().setColorFilter(androidx.appcompat.R.attr.colorPrimary, PorterDuff.Mode.MULTIPLY);

        ImageView computerChoiceImageView = (ImageView) findViewById(R.id.computer_choice_imageView);
        computerChoiceImageView.setImageResource(computerChoice.getImageResource192());

        TextView playerChoiceTextView = (TextView) findViewById(R.id.player_choice_textView);

        if (choice == computerChoice){
            playerChoiceTextView.setText(R.string.draw_message);
        } else {

            boolean won = false;

            switch (choice) {
                case ROCK:
                    if (computerChoice == PLAY_OPTIONS.SCISSORS)
                        won = true;
                    break;
                case PAPER:
                    if (computerChoice == PLAY_OPTIONS.ROCK)
                        won = true;
                    break;
                case SCISSORS:
                    if (computerChoice == PLAY_OPTIONS.PAPER)
                        won = true;
                    break;
            }

            playerChoiceTextView.setText((won) ? R.string.win_message : R.string.lose_message);

            isGameFinished = (won) ? scoreboard.addPointPlayer() : scoreboard.addPointComputer();

        }

        Button playAgainButton = (Button) findViewById(R.id.play_again_button);
        playAgainButton.setVisibility(Button.VISIBLE);

        updateJoKenPoGuiInfo();

        if (isGameFinished && scoreboard.isWinner()) {
//            Log.d("Aqui", "aqui");
//            NavigationView navigationView = (NavigationView) this.findViewById(R.id.nav_view);
//            navigationView.getMenu().getItem(1).setChecked(true);
            player = new Player("", scoreboard.getComputerScore(), scoreboard.getPlayerScore(), scoreboard.getGameElapsedTime());
            addPlayerScoreboardDialog();
        }
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

    private PLAY_OPTIONS generateComputerPlay(){
        return PLAY_OPTIONS.values()[
                new SecureRandom().nextInt(
                        PLAY_OPTIONS.values().length
                )
        ];
    }

    public void updateRanking(){
        List<String> itens = new ArrayList<>();
        for(Player player : ranking){
            itens.add(player.toString());
        }

        ListView rankingListView = (ListView) findViewById(R.id.ranking_list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, itens
        );

        rankingListView.setAdapter(adapter);
    }

    public Player getPlayer(){
        return this.player;
    }

    private void addPlayerScoreboardDialog() {
        if(player != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Digite seu nome para adicionar seu resultado ao ranking:");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    player.setNome(input.getText().toString());
                    addPlayerToRanking(player);
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    public void addPlayerToRanking (Player player){
        ranking.add(player);
        Collections.sort(ranking);
    }
}