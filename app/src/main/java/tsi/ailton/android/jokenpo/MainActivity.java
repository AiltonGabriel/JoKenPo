package tsi.ailton.android.jokenpo;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;

import javax.annotation.Nullable;

import tsi.ailton.android.jokenpo.controllers.VibratorController;
import tsi.ailton.android.jokenpo.databinding.ActivityMainBinding;
import tsi.ailton.android.jokenpo.models.AppState;
import tsi.ailton.android.jokenpo.controllers.MediaPlayerController;
import tsi.ailton.android.jokenpo.models.RankingItem;
import tsi.ailton.android.jokenpo.models.Scoreboard;
import tsi.ailton.android.jokenpo.models.dao.AppDatabase;
import tsi.ailton.android.jokenpo.models.dao.AppStateDao;
import tsi.ailton.android.jokenpo.notifications.AlarmReciever;

public class MainActivity extends AppCompatActivity {

    private enum PLAY_OPTION { ROCK, PAPER, SCISSORS }

    public enum GAME_MODE { RANDOM, ONLY_ROCK }

    private FirebaseFirestore firestore;
    private AppDatabase db;

    private AppStateDao appStateDao;
    private AppState appState;

    private GAME_MODE game_mode;
    private Scoreboard scoreboard;

    private MediaPlayerController mediaPlayerController;
    private VibratorController vibratorController;

    private boolean isNotificationListenerCreated;

    public WebView webView;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        super.onCreate(savedInstanceState);
//
//        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.appBarMain.toolbar);
//        DrawerLayout drawer = binding.drawerLayout;
//        NavigationView navigationView = binding.navView;
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_jo_ken_po, R.id.nav_ranking, R.id.nav_game_mode)
//                .setOpenableLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        firestore = FirebaseFirestore.getInstance();
        db = AppDatabase.getInstance(getApplicationContext());

        appStateDao = db.appStateDao();

        appState = appStateDao.get();

        if(appState != null){
            game_mode = appState.getGame_mode();
            scoreboard = appState.getScoreboard();
        } else {
            game_mode = GAME_MODE.RANDOM;
            scoreboard = new Scoreboard();
        }

        mediaPlayerController = new MediaPlayerController(getApplicationContext());
        vibratorController = new VibratorController(getApplicationContext());

        isNotificationListenerCreated = false;
        //createNotificationListener();

        // Configura a WebView
        webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
        webView.setWebChromeClient(new WebChromeClient());
        // Habilita o JS
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Garante que usará a WebView e não o navegador padrão
        webView.setWebViewClient(new WebViewClient(){
            // Callback que determina quando terminou de ser carregada a
            // WebView, para trocarmos a imagem de carregamento por ela
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ImageView imageView = (ImageView)
                        findViewById(R.id.imageView);
                imageView.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        // Associa a interface (a ser definida abaixo) e carrega o HTML
        webView.addJavascriptInterface(new WebAppInterface(this),"Android");
        webView.loadUrl("file:///android_asset/index.html");

    }

    // Interface para binding Javascript -> Java
    public class WebAppInterface {
        MainActivity mainActivity;

        public WebAppInterface(MainActivity activity) {
            this.mainActivity = activity;
        }
        @JavascriptInterface
        public void androidToast(String msg) {
            Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT).show();
            // Chama uma função do JavaScript
            //runJavaScript("oculta_botao();");
        }
        @JavascriptInterface
        public void playRock() {
            makePlay(MainActivity.PLAY_OPTION.ROCK);
        }

        @JavascriptInterface
        public void playPaper() {
            makePlay(PLAY_OPTION.PAPER);
        }

        @JavascriptInterface
        public void playScissors() {
            makePlay(PLAY_OPTION.SCISSORS);
        }

        @JavascriptInterface
        public void playAgain() {
            runJavaScript("setGameStatusLabel('Selecione sua jogada:')");
            runJavaScript("playAgain();");
        }

        @JavascriptInterface
        public void resetScoreboard() {
            scoreboard.reset();
            updateScoreboardGui();
            saveAppState();
            playAgain();
        }

        @JavascriptInterface
        public void updateScoreboardGui() {
            runJavaScript(String.format("setScoreboard(%d, %d);", scoreboard.getPlayerScore(), scoreboard.getComputerScore()));
        }

        private void makePlay(PLAY_OPTION choice) {
            PLAY_OPTION computerChoice = generateComputerPlay();
            runJavaScript(String.format("setComputerChoice(%d);", computerChoice.ordinal()));

            boolean isGameFinished = false;

            boolean draw = false;
            boolean won = false;

            switch (choice) {
                case ROCK:
                    switch (computerChoice) {
                        case ROCK:
                            mediaPlayerController.play(R.raw.rock_rock);
                            draw = true;
                            break;
                        case PAPER:
                            mediaPlayerController.play(R.raw.paper_rock);
                            break;
                        case SCISSORS:
                            mediaPlayerController.play(R.raw.rock_scissors);
                            won = true;
                            break;
                    }
                    break;
                case PAPER:
                    switch (computerChoice) {
                        case ROCK:
                            mediaPlayerController.play(R.raw.paper_rock);
                            won = true;
                            break;
                        case PAPER:
                            mediaPlayerController.play(R.raw.paper_paper);
                            draw = true;
                            break;
                        case SCISSORS:
                            mediaPlayerController.play(R.raw.scissors_paper);
                            break;
                    }
                    break;
                case SCISSORS:
                    switch (computerChoice) {
                        case ROCK:
                            mediaPlayerController.play(R.raw.rock_scissors);
                            break;
                        case PAPER:
                            mediaPlayerController.play(R.raw.scissors_paper);
                            won = true;
                            break;
                        case SCISSORS:
                            mediaPlayerController.play(R.raw.scissors_sicssors);
                            draw = true;
                            break;
                    }
                    break;
            }

            if (draw){
                runJavaScript("setGameStatusLabel('Empate!')");
            } else {
                if (won)
                    runJavaScript("setGameStatusLabel('Voce ganhou!')");
                else
                    runJavaScript("setGameStatusLabel('Voce perdeu!')");

                isGameFinished = (won) ? scoreboard.addPointPlayer() : scoreboard.addPointComputer();
            }

            if (isGameFinished) {
                if(scoreboard.isWinner()) {
                    vibratorController.vibrate(VibratorController.MORTAL_KOMBAT_THEME);
                    mediaPlayerController.play(R.raw.mk_theme);
                    //addPlayerToRankingDialog();
                }

                resetScoreboard();
            }

            updateScoreboardGui();
        }

    }

    // Possibilita o uso do botão voltar
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    // Executa um comando JavaScript
    public void runJavaScript(final String jsCode){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(jsCode, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveAppState();
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    private void saveAppState(){
        if(appState.getId() != null){
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

    public GAME_MODE getGame_mode() {
        return game_mode;
    }

    public void setGame_mode(GAME_MODE game_mode) {
        this.game_mode = game_mode;
    }

//    public void resetScoreboard(View view) {
//        scoreboard.reset();
//        updateScoreboardGui(view);
//        saveAppState();
//        playAgain(view);
//    }
//
//    public void updateScoreboardGui(View view) {
//        TextView playerScore = (TextView) findViewById(R.id.player_score_textView);
//        TextView computerScore = (TextView) findViewById(R.id.computer_score_textView);
//
//        playerScore.setText(String.valueOf(scoreboard.getPlayerScore()));
//        computerScore.setText(String.valueOf(scoreboard.getComputerScore()));
//    }
//
//    public void playAgain(View view) {
//
//        Button playAgainButton = (Button) findViewById(R.id.play_again_button);
//        playAgainButton.setVisibility(Button.GONE);
//
//        ImageView computerChoiceImageView = (ImageView) findViewById(R.id.computer_choice_imageView);
//        computerChoiceImageView.setImageResource(R.color.computer_choose_empty_background_color);
//
//        ImageButton[] imageButtons = getImageButtons();
//
//        setImageButtonsEnable(true, imageButtons);
//        for(ImageButton imageButton : imageButtons) {
//            imageButton.getBackground().clearColorFilter();
//        }
//
//        ((TextView) findViewById(R.id.player_choice_textView)).setText(R.string.player_choice_label_text);
//    }
//
//    public void playRock(View view) {
//        makePlay(view, (ImageButton) view, PLAY_OPTION.ROCK);
//    }
//
//    public void playPaper(View view){
//        makePlay(view, (ImageButton) view, PLAY_OPTION.PAPER);
//    }
//
//    public void playScissors(View view) {
//        makePlay(view, (ImageButton) view, PLAY_OPTION.SCISSORS);
//    }
//
//    private void makePlay(View view, ImageButton imageButton, PLAY_OPTION choice) {
//        PLAY_OPTION computerChoice = generateComputerPlay();
//        ImageButton[] imageButtons = getImageButtons();
//
//        setImageButtonsEnable(false, imageButtons);
//        imageButton.getBackground().setColorFilter(androidx.appcompat.R.attr.colorPrimary, PorterDuff.Mode.MULTIPLY);
//
//        ImageView computerChoiceImageView = (ImageView) findViewById(R.id.computer_choice_imageView);
//        computerChoiceImageView.setImageResource(computerChoice.getImageResource192());
//
//        TextView playerChoiceTextView = (TextView) findViewById(R.id.player_choice_textView);
//
//        boolean isGameFinished = false;
//
//        boolean draw = false;
//        boolean won = false;
//
//        switch (choice) {
//            case ROCK:
//                switch (computerChoice) {
//                    case ROCK:
//                        mediaPlayerController.play(R.raw.rock_rock);
//                        draw = true;
//                        break;
//                    case PAPER:
//                        mediaPlayerController.play(R.raw.paper_rock);
//                        break;
//                    case SCISSORS:
//                        mediaPlayerController.play(R.raw.rock_scissors);
//                        won = true;
//                        break;
//                }
//                break;
//            case PAPER:
//                switch (computerChoice) {
//                    case ROCK:
//                        mediaPlayerController.play(R.raw.paper_rock);
//                        won = true;
//                        break;
//                    case PAPER:
//                        mediaPlayerController.play(R.raw.paper_paper);
//                        draw = true;
//                        break;
//                    case SCISSORS:
//                        mediaPlayerController.play(R.raw.scissors_paper);
//                        break;
//                }
//                break;
//            case SCISSORS:
//                switch (computerChoice) {
//                    case ROCK:
//                        mediaPlayerController.play(R.raw.rock_scissors);
//                        break;
//                    case PAPER:
//                        mediaPlayerController.play(R.raw.scissors_paper);
//                        won = true;
//                        break;
//                    case SCISSORS:
//                        mediaPlayerController.play(R.raw.scissors_sicssors);
//                        draw = true;
//                        break;
//                }
//                break;
//        }
//
//        if (draw){
//            playerChoiceTextView.setText(R.string.draw_message);
//        } else {
//            playerChoiceTextView.setText((won) ? R.string.win_message : R.string.lose_message);
//
//            isGameFinished = (won) ? scoreboard.addPointPlayer() : scoreboard.addPointComputer();
//        }
//
//        Button playAgainButton = (Button) findViewById(R.id.play_again_button);
//        playAgainButton.setVisibility(Button.VISIBLE);
//
//        if (isGameFinished) {
//            if(scoreboard.isWinner()) {
//                vibratorController.vibrate(VibratorController.MORTAL_KOMBAT_THEME);
//                mediaPlayerController.play(R.raw.mk_theme);
//                addPlayerToRankingDialog();
//            }
//
//            resetScoreboard(view);
//        }
//
//        updateScoreboardGui(view);
//    }
//
//    private ImageButton[] getImageButtons(){
//        ImageButton[] imageButtons = new ImageButton[3];
//
//        imageButtons[0] = (ImageButton) findViewById(R.id.rock_imageButton);
//        imageButtons[1] = (ImageButton) findViewById(R.id.paper_imageButton);
//        imageButtons[2] = (ImageButton) findViewById(R.id.scissors_imageButton);
//
//        return imageButtons;
//    }
//
//    private void setImageButtonsEnable(Boolean enable, ImageButton[] imageButtons) {
//        for(ImageButton imageButton : imageButtons) {
//            imageButton.setEnabled(enable);
//        }
//    }
//
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
//
//    /*
//    *  Ranking
//    */
//    private void addPlayerToRankingDialog() {
//        RankingItem rankingItem = new RankingItem("", scoreboard.getComputerScore(), scoreboard.getPlayerScore(), scoreboard.getGameElapsedTime());
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.add_player_scoreboard_msg);
//
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
//        builder.setPositiveButton(R.string.add_player_scoreboard_ok_button_text, null);
//        builder.setNegativeButton(R.string.add_player_scoreboard_cancel_button_text, (dialog, which) -> dialog.cancel());
//
//        AlertDialog dialog = builder.create();
//
//        dialog.setOnShowListener(dialogInterface -> {
//            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
//
//            button.setOnClickListener(view -> {
//                String txt = input.getText().toString();
//
//                if(!txt.isEmpty()){
//                    rankingItem.setPlayerName(input.getText().toString());
//                    addPlayerToRanking(rankingItem);
//                    dialog.dismiss();
//                } else{
//                    Toast.makeText(getApplicationContext(), R.string.add_player_scoreboard_empty_input_msg, Toast.LENGTH_LONG).show();
//                }
//            });
//        });
//
//        dialog.show();
//    }
//
//    private void addPlayerToRanking (RankingItem rankingItem){
//        firestore.collection("ranking")
//                .whereEqualTo("playerName", rankingItem.getPlayerName())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        boolean isBestEntry = true;
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            if(rankingItem.compareTo(document.toObject(RankingItem.class)) <= 0) {
//                                Toast.makeText(MainActivity.this, R.string.better_score_already_registered_msg, Toast.LENGTH_LONG).show();
//                                isBestEntry = false;
//                            }
//                        }
//                        if (isBestEntry){
//                            firestore.collection("ranking").add(rankingItem);
//                            Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_ranking);
//                        }
//                    }
//                });
//    }
//
//    /*
//     * Notification
//     */
//    public void createChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String name = "notific";
//            String description = "test";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel("1", name, importance);
//            channel.setDescription(description);
//            channel.setShowBadge(true);
//            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//
//    private void createNotificationListener() {
//        firestore.collection("ranking")
//            .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                @Override
//                public void onEvent(@Nullable QuerySnapshot snapshots,
//                                    @Nullable FirebaseFirestoreException e) {
//                    if (e != null) {
//                        return;
//                    }
//
//                    if (!isNotificationListenerCreated) {
//                        isNotificationListenerCreated = true;
//                        return;
//                    }
//
//                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
//                        switch (dc.getType()) {
//                            case ADDED:
//                                Integer milliseconds = 0;
//                                Integer requestCode = 0;
//                                Intent intent = new Intent(MainActivity.this, AlarmReciever.class);
//                                PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                                        MainActivity.this.getApplicationContext(),
//                                        requestCode++, intent,
//                                        PendingIntent.FLAG_MUTABLE
//                                );
//                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                                        + milliseconds, pendingIntent);
//                                break;
//                        }
//                    }
//
//                }
//            });
//    }

}