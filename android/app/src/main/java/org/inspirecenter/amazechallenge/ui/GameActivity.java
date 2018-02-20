package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.blockly.model.Block;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.controller.AudioEventListener;
import org.inspirecenter.amazechallenge.controller.RuntimeController;
import org.inspirecenter.amazechallenge.model.Audio;
import org.inspirecenter.amazechallenge.model.BackgroundImage;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Pickable;
import org.inspirecenter.amazechallenge.model.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class GameActivity extends AppCompatActivity implements AudioEventListener {

    public static final String TAG = "aMaze";

    public static final String SELECTED_CHALLENGE_KEY = "selected_challenge";
    public static final String SELECTED_PLAYER_KEY = "selected_player";

    public static final int DEFAULT_PERIOD_INDEX = 3;
    public static final long [] PERIOD_OPTIONS = new long [] { 100L, 200L, 500L, 1000L, 2000L, 5000L };

    private static final float DEFAULT_EVENTS_VOLUME = 1f;
    private static final float DEFAULT_AMBIENT_VOLUME = 0.7f;

    private boolean isRunning = false;

    private Challenge challenge;
    private Game game;

    private GameView gameView;
    private Spinner delaySeekBar;

    private Switch autoPlayButton;
    private Button nextButton;
    private ToggleButton playPauseToggleButton;
    private Button resetButton;
    private TextView healthTextView;
    private TextView pointsTextView;

    private TextView movesDataTextView;
    private Button movesDetailsButton;

    final GameActivity instance = this;

    private HashMap<String, MediaPlayer> audioEventsMap = new HashMap<>();
    private MediaPlayer backgroundAudio;
    private boolean sound = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        playPauseToggleButton = findViewById(R.id.activity_game_play);
        playPauseToggleButton.setChecked(isRunning);
        playPauseToggleButton.setText(R.string.Play);
        playPauseToggleButton.setTextOff(getString(R.string.Play));
        playPauseToggleButton.setTextOn(getString(R.string.Pause));
        playPauseToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isRunning = b;
                nextButton.setEnabled(!b);
                resetButton.setEnabled(!b);
            }
        });

        resetButton = findViewById(R.id.activity_game_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(instance);
                builder.setTitle(R.string.Are_you_sure_you_want_to_reset)
                        .setPositiveButton(R.string.Reset, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetGame();
                            }
                        })
                        .setNegativeButton(R.string.Cancel, null);
                builder.create().show();
            }
        });

        healthTextView = findViewById(R.id.activity_game_health);
        updateHealthTextView();

        pointsTextView = findViewById(R.id.activity_game_points);
        updatePointsTextView();


        this.gameView = findViewById(org.inspirecenter.amazechallenge.R.id.activity_game_game_view);
        this.delaySeekBar = findViewById(org.inspirecenter.amazechallenge.R.id.activity_game_delay_spinner);
        final Vector<String> periodOptions = new Vector<>();
        for(final long period : PERIOD_OPTIONS) {
            if(period < 1000) {
                periodOptions.add(String.format(Locale.US, "%d ms", period));
            } else {
                periodOptions.add(String.format(Locale.US, "%d s", period / 1000));
            }
        }
        this.delaySeekBar.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, periodOptions));
        this.delaySeekBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                period = PERIOD_OPTIONS[position];
            }

            @Override public void onNothingSelected(AdapterView<?> parent) { /* nothing */ }
        });

        this.autoPlayButton = findViewById(org.inspirecenter.amazechallenge.R.id.activity_game_auto_play_switch);
        this.nextButton = findViewById(org.inspirecenter.amazechallenge.R.id.activity_game_button_next);
        this.nextButton.setOnClickListener(v -> makeNextMove());

        movesDataTextView = findViewById(R.id.activity_game_moves_data);
        movesDetailsButton = findViewById(R.id.activity_game_moves_details);
        movesDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo show a dialog displaying the details of all players' moves
            }
        });

        //Create audio map:
        for (final Audio audio : Audio.values()) {
            if (audio.getAudioType() == Audio.AudioType.EVENT) {
                if (audio.getAudioFormat() != Audio.AudioFormat.UNDEFINED_FORMAT && !audio.getSoundResourceName().equals(Audio.AUDIO_NONE.getSoundResourceName())) {
                    final int identifier = getResources().getIdentifier(audio.getSoundResourceName(), "raw", getPackageName());
                    final MediaPlayer mediaPlayer = MediaPlayer.create(this, identifier);
                    mediaPlayer.setVolume(DEFAULT_EVENTS_VOLUME, DEFAULT_EVENTS_VOLUME);
                    audioEventsMap.put(audio.toString(), mediaPlayer);
                }
            }
        }

        //Sound prefs:
        sound = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(MainActivity.KEY_PREF_SOUND, true);
    }

    private Handler handler;
    private Timer timer = new Timer();
    private int periodIndex = DEFAULT_PERIOD_INDEX;
    private long period = PERIOD_OPTIONS[periodIndex];

    private Map<String,MazeSolver> playerEmailToMazeSolvers = new HashMap<>();

    @Override
    protected void onResume() {
        super.onResume();

        final Intent intent = getIntent();
        this.challenge = (Challenge) intent.getSerializableExtra(SELECTED_CHALLENGE_KEY);
        this.gameView.setGrid(challenge.getGrid());
        this.gameView.setLineColor(challenge.getLineColor());
        BackgroundImage backgroundImage = challenge.getBackgroundImage();
        this.gameView.setBackgroundDrawable(backgroundImage);
        this.game = new Game();
        game.setOnAudioEventListener(this);
        final Player player = (Player) intent.getSerializableExtra(SELECTED_PLAYER_KEY);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String code = sharedPreferences.getString(BlocklyActivity.KEY_ALGORITHM_ACTIVITY_CODE, "");
        if (code.isEmpty()) {
            AlertDialog.Builder noCodeDialog = new AlertDialog.Builder(this, R.style.ErrorDialogStyle);
            noCodeDialog.setTitle(R.string.no_code_title);
            noCodeDialog.setMessage(R.string.no_code_message);
            noCodeDialog.setPositiveButton(R.string.no_code_action, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startService(new Intent(GameActivity.this, BlocklyActivity.class));
                    finish();
                }
            });
            noCodeDialog.setCancelable(false);
            noCodeDialog.create().show();
        }
        playerEmailToMazeSolvers.put(player.getEmail(), new InterpretedMazeSolver(challenge, game, player.getEmail(), code));
        game.addPlayer(player);
        game.queuePlayer(player.getEmail());
        game.activateNextPlayer(challenge.getGrid());

        handler = new Handler();

        gameView.update(game);

        timer.schedule(new MazeRunner(), 0L, PERIOD_OPTIONS[0]);

        //Play background audio:
        final Audio audioResource = challenge.getBackgroundAudio();
        final Audio.AudioFormat audioFormat = audioResource.getAudioFormat();
        System.out.println("Sound is: " + sound);
        if (audioFormat != Audio.AudioFormat.UNDEFINED_FORMAT && !audioResource.getName().equals(Audio.AUDIO_NONE.getSoundResourceName())) {
            backgroundAudio = MediaPlayer.create(this, getResources().getIdentifier(audioResource.getName(), "raw", getPackageName()));
            if (backgroundAudio != null && sound) {
                backgroundAudio.setLooping(true);
                backgroundAudio.setVolume(DEFAULT_AMBIENT_VOLUME, DEFAULT_AMBIENT_VOLUME);
                backgroundAudio.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundAudio != null) backgroundAudio.stop();
        timer.cancel();
    }

    private void makeNextMove() {
        if (!game.getActivePlayers().isEmpty()) {
            RuntimeController.makeMove(challenge, game, playerEmailToMazeSolvers);
            gameView.update(game);
            gameView.invalidate();
            updateHealthTextView();
            updatePointsTextView();
            // update movesDataTextView
//        movesDataTextView.setText(game.getStatisticsDescription()); // todo
            if (RuntimeController.hasSomeoneReachedTheTargetPosition(game, challenge.getGrid())) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(MainActivity.KEY_PREF_LOCALLY_TESTED, true).apply();
                Toast.makeText(this, getString(R.string.maze_completed) + " " + challenge.getName() + "!", Toast.LENGTH_LONG).show();
                if (backgroundAudio != null) backgroundAudio.stop();

                if (challenge.getHasQuestionnaire())
                    startActivity(new Intent(this, QuestionnaireActivity.class));
                else
                    finish();
            }

            if (RuntimeController.allPlayersHaveLost(game)) {
                RuntimeController.resetTurnEffects();
                Snackbar.make(gameView, getString(R.string.maze_lost), Snackbar.LENGTH_INDEFINITE).setAction(R.string.maze_play_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetGame();
                    }
                }).setActionTextColor(Color.GREEN).show();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (v != null) v.vibrate(500);
            }
        }

    }

    private class MazeRunner extends TimerTask {
        private long elapsed = 0;

        @Override
        public void run() {
            elapsed += PERIOD_OPTIONS[0];
            if(elapsed >= period) {
                elapsed = 0;
                if(/*autoPlayButton.isChecked()*/isRunning) {
                    handler.post(() -> makeNextMove());
                }
            }
        }
    }

    private void resetGame() {
        //autoPlayButton.setChecked(false);
        isRunning = false;
        playPauseToggleButton.setChecked(isRunning);
        final Intent intent = getIntent();
        final Player player = (Player) intent.getSerializableExtra(SELECTED_PLAYER_KEY);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String code = sharedPreferences.getString(BlocklyActivity.KEY_ALGORITHM_ACTIVITY_CODE, "");
        final String playerEmail = player.getEmail();
        playerEmailToMazeSolvers.put(player.getEmail(), new InterpretedMazeSolver(challenge, game, playerEmail, code));
        game.addPlayer(player);
        game.queuePlayer(playerEmail);
        game.activateNextPlayer(challenge.getGrid());
        updateHealthTextView();
        updatePointsTextView();
        player.setActive();
        game.resetPickables();
        gameView.update(game);
        gameView.invalidate();
    }

    private void updateHealthTextView() {
        final Intent intent = getIntent();
        final Player player = (Player) intent.getSerializableExtra(SELECTED_PLAYER_KEY);
        healthTextView.setText("Player health: " + player.getHealth().get() );
    }

    private void updatePointsTextView() {
        final Intent intent = getIntent();
        final Player player = (Player) intent.getSerializableExtra(SELECTED_PLAYER_KEY);
        pointsTextView.setText("Player points: " + player.getPoints() );
    }

    @Override
    public void onAudioEvent(Pickable pickable) {

        switch (pickable.getPickableType()) {
            case APPLE:
            case BANANA:
            case STRAWBERRY:
            case PEACH:
            case WATERMELON:
            case GRAPES:
            case ORANGE:
                if (sound) audioEventsMap.get(Audio.EVENT_FOOD.toString()).start();
                break;
            case COIN_5:
                if (sound) audioEventsMap.get(Audio.EVENT_COIN5.toString()).start();
                break;
            case COIN_10:
                if (sound) audioEventsMap.get(Audio.EVENT_COIN10.toString()).start();
                break;
            case COIN_20:
                if (sound) audioEventsMap.get(Audio.EVENT_COIN20.toString()).start();
                break;
            case GIFTBOX:
                if (sound) audioEventsMap.get(Audio.EVENT_GIFTBOX.toString()).start();
                break;
            case BOMB:
                if (pickable.getState() == 1  || pickable.getState() == 2)
                    if (sound) audioEventsMap.get(Audio.EVENT_BOMB.toString()).start();
                break;
            case SPEEDHACK:
                if (sound) audioEventsMap.get(Audio.EVENT_SPEEDHACK.toString()).start();
                break;
            case TRAP:
                if (sound) audioEventsMap.get(Audio.EVENT_TRAP.toString()).start();
                break;
        }
    }

    @Override
    public void onGameEndAudioEvent(boolean win) {
        if (win) {
            MediaPlayer winAudio = MediaPlayer.create(this, getResources().getIdentifier(Audio.EVENT_WIN.getSoundResourceName(), "raw", getPackageName()));
            winAudio.start();
            winAudio.release();
        }
        else {
            MediaPlayer loseAudio = MediaPlayer.create(this, getResources().getIdentifier(Audio.EVENT_LOSE.getSoundResourceName(), "raw", getPackageName()));
            loseAudio.start();
            loseAudio.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Map.Entry<String, MediaPlayer> entry : audioEventsMap.entrySet()) {
            audioEventsMap.get(entry.getKey()).release();
        }
    }
}