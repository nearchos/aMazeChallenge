package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

import static org.inspirecenter.amazechallenge.ui.MainActivity.KEY_PREF_SOUND;
import static org.inspirecenter.amazechallenge.ui.MainActivity.KEY_PREF_VIBRATION;
import static org.inspirecenter.amazechallenge.ui.MainActivity.setLanguage;

public class GameActivity extends AppCompatActivity implements AudioEventListener {

    public static final String SELECTED_CHALLENGE_KEY = "selected_challenge";
    public static final String SELECTED_PLAYER_KEY = "selected_player";

    public static final int DEFAULT_PERIOD_INDEX = 3;
    public static final long [] PERIOD_OPTIONS = new long [] { 100L, 200L, 500L, 1000L, 2000L, 5000L };

    private static final float DEFAULT_EVENTS_VOLUME = 1f;
    private static final float DEFAULT_AMBIENT_VOLUME = 0.7f;

    private boolean isRunning = false;
    private boolean hasCreatedQuestionnaire = false;

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
    private ProgressBar healthProgress;

    private Button movesDetailsButton;

    final GameActivity instance = this;

    private HashMap<String, MediaPlayer> audioEventsMap = new HashMap<>();
    private MediaPlayer backgroundAudio;
    private boolean sound = true;
    private boolean vibration = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setLanguage(this);
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

        healthProgress = findViewById(R.id.playerHealthProgress);

        healthTextView = findViewById(R.id.activity_game_health);
        System.out.println("AA" + healthTextView.getText());
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

        movesDetailsButton = findViewById(R.id.activity_game_moves_details);
        movesDetailsButton.setOnClickListener(view -> {
            // todo show a dialog displaying the details of all players' moves
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
        sound = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEY_PREF_SOUND, true);
        vibration = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEY_PREF_VIBRATION, true);
    }

    private Handler handler;
    private Timer timer = new Timer();
    private int periodIndex = DEFAULT_PERIOD_INDEX;
    private long period = PERIOD_OPTIONS[periodIndex];

    private Map<String,MazeSolver> playerIdToMazeSolvers = new HashMap<>();

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
                    startActivity(new Intent(GameActivity.this, BlocklyActivity.class));
                    finish();
                }
            });
            noCodeDialog.setCancelable(false);
            noCodeDialog.create().show();
        }
        playerIdToMazeSolvers.put(player.getId(), new InterpretedMazeSolver(challenge, game, player.getId(), code));
        game.addPlayer(player);
        game.queuePlayerById(player.getId());
        game.activateNextPlayer(challenge.getGrid());

        handler = new Handler();

        gameView.update(game);

        timer.schedule(new MazeRunner(), 0L, PERIOD_OPTIONS[0]);

        //Play background audio:
        final Audio audioResource = challenge.getBackgroundAudio();
        System.out.println("Sound is: " + sound);
        if (audioResource.getAudioFormat() != Audio.AudioFormat.UNDEFINED_FORMAT && !audioResource.getName().equals(Audio.AUDIO_NONE.getSoundResourceName())) {
            backgroundAudio = MediaPlayer.create(this, getResources().getIdentifier(audioResource.getSoundResourceName(), "raw", getPackageName()));
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
        if (!game.getActivePlayerIDs().isEmpty()) {
            RuntimeController.makeMove(challenge, game, playerIdToMazeSolvers);
            gameView.update(game);
            gameView.invalidate();
            updateHealthTextView();
            updatePointsTextView();
            if (RuntimeController.hasSomeoneReachedTheTargetPosition(game, challenge.getGrid())) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(MainActivity.KEY_PREF_LOCALLY_TESTED, true).apply();
                Toast.makeText(this, getString(R.string.maze_completed) + " " + challenge.getName() + "!", Toast.LENGTH_LONG).show();
                if (backgroundAudio != null) backgroundAudio.stop();

                if (challenge.getHasQuestionnaire() && !hasCreatedQuestionnaire) {
                    Intent intent = new Intent(this, QuestionnaireActivity.class);
                    intent.putExtra(QuestionnaireActivity.CHALLENGE_KEY, challenge.getId());
                    hasCreatedQuestionnaire = true;
                    startActivity(intent);
                }
                else finish();
            }

            if (RuntimeController.allPlayersHaveLost(game)) {
                RuntimeController.resetTurnEffects();

                if (vibration) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (v != null) v.vibrate(500);
                }

                AlertDialog.Builder lostDialog = new AlertDialog.Builder(this, R.style.ErrorDialogStyle);
                lostDialog.setTitle(R.string.maze_lost);
                lostDialog.setMessage(R.string.maze_play_again);
                lostDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                lostDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetGame();
                    }
                });
                lostDialog.setCancelable(false);
                lostDialog.create().show();
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
        playPauseToggleButton.setChecked(false);
        final Intent intent = getIntent();
        final Player player = (Player) intent.getSerializableExtra(SELECTED_PLAYER_KEY);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String code = sharedPreferences.getString(BlocklyActivity.KEY_ALGORITHM_ACTIVITY_CODE, "");
        final String playerId = player.getId();
        playerIdToMazeSolvers.put(player.getId(), new InterpretedMazeSolver(challenge, game, playerId, code));
        game.addPlayer(player);
        game.queuePlayerById(playerId);
        game.activateNextPlayer(challenge.getGrid());
        updateHealthTextView();
        updatePointsTextView();
        game.resetPickables();
        gameView.update(game);
        gameView.invalidate();
    }

    private void updateHealthTextView() {
        final Intent intent = getIntent();
        int oldHealth = Integer.parseInt(healthTextView.getText().toString());
        final Player player = (Player) intent.getSerializableExtra(SELECTED_PLAYER_KEY);
        int health = player.getHealth().get();
        if (health != oldHealth) {
            healthTextView.setText(Integer.toString(health));
            healthProgress.setProgress(health);
            Drawable drawable = healthProgress.getProgressDrawable();
            if (health <= 30) {
                healthTextView.setTextColor(getColor(R.color.materialRed));
                drawable.setColorFilter(new LightingColorFilter(0xFF000000, getColor(R.color.materialRed)));
            } else if (health <= 50) {
                healthTextView.setTextColor(getColor(R.color.materialYellow));
                drawable.setColorFilter(new LightingColorFilter(0xFF000000, getColor(R.color.materialYellow)));
            } else if (health > 100) {
                healthTextView.setTextColor(getColor(R.color.materialBlue));
                drawable.setColorFilter(new LightingColorFilter(0xFF000000, getColor(R.color.materialBlue)));
            } else {
                healthTextView.setTextColor(getColor(R.color.materialGreen));
                drawable.setColorFilter(new LightingColorFilter(0xFF000000, getColor(R.color.materialGreen)));
            }
        }
    }

    private void updatePointsTextView() {
        final Intent intent = getIntent();
        final Player player = (Player) intent.getSerializableExtra(SELECTED_PLAYER_KEY);
        pointsTextView.setText(Integer.toString(player.getPoints()));
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
                if (pickable.getState() == 1  || pickable.getState() == 2) {
                    if (sound) audioEventsMap.get(Audio.EVENT_BOMB.toString()).start();
                    if (vibration) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) v.vibrate(250);
                    }
                }
                break;
            case SPEEDHACK:
                if (sound) audioEventsMap.get(Audio.EVENT_SPEEDHACK.toString()).start();
                if (vibration) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {100, 100, 100};
                    if (v != null) v.vibrate(pattern, -1);
                }
                break;
            case TRAP:
                if (sound) audioEventsMap.get(Audio.EVENT_TRAP.toString()).start();
                if (vibration) {
                    Vibrator vTrap = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vTrap != null) vTrap.vibrate(250);
                }
                break;
        }
    }

    @Override
    public void onGameEndAudioEvent(boolean win) {
        if (win) {
            MediaPlayer winAudio = MediaPlayer.create(this, getResources().getIdentifier(Audio.EVENT_WIN.getSoundResourceName(), "raw", getPackageName()));
            winAudio.start();
            winAudio.release();
        } else {
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