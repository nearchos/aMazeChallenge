package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.controller.RuntimeController;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class GameActivity extends AppCompatActivity {

    public static final String TAG = "aMaze";

    public static final String SELECTED_CHALLENGE_KEY = "selected_challenge";
    public static final String SELECTED_PLAYER_KEY = "selected_player";

    public static final int DEFAULT_PERIOD_INDEX = 3;
    public static final long [] PERIOD_OPTIONS = new long [] { 100L, 200L, 500L, 1000L, 2000L, 5000L };

    private Challenge challenge;
    private Game game;

    private GameView gameView;
    private Spinner delaySeekBar;

    private Switch autoPlayButton;
    private Button nextButton;

    private TextView movesDataTextView;
    private Button movesDetailsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        this.gameView = findViewById(org.inspirecenter.amazechallenge.R.id.activity_grid_grid_view);
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
        String backgroundName = challenge.getBackgroundImage();
        System.out.println("BACKGROUND NAME: " + backgroundName);
        this.gameView.setBackgroundDrawable(MazeBackground.getByName(backgroundName).getResourceID());
        this.game = new Game();
        final Player player = (Player) intent.getSerializableExtra(SELECTED_PLAYER_KEY);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String code = sharedPreferences.getString(BlocklyActivity.KEY_ALGORITHM_ACTIVITY_CODE, "");
        final String playerEmail = player.getEmail();
        playerEmailToMazeSolvers.put(player.getEmail(), new InterpretedMazeSolver(challenge, game, playerEmail, code));
        game.addPlayer(player);
        game.queuePlayer(playerEmail);
        game.activateNextPlayer(challenge.getGrid());

        handler = new Handler();

        gameView.update(game);

        timer.schedule(new MazeRunner(), 0L, PERIOD_OPTIONS[0]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    private void makeNextMove() {
        RuntimeController.makeMove(challenge.getGrid(), game, playerEmailToMazeSolvers);
        gameView.update(game);
        gameView.invalidate();
        // update movesDataTextView
//        movesDataTextView.setText(game.getStatisticsDescription()); // todo
        if(RuntimeController.hasSomeoneReachedTheTargetPosition(game, challenge.getGrid())) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(MainActivity.KEY_PREF_LOCALLY_TESTED, true).apply();
            finish();
        }
    }

    private class MazeRunner extends TimerTask {
        private long elapsed = 0;

        @Override
        public void run() {
            elapsed += PERIOD_OPTIONS[0];
            if(elapsed >= period) {
                elapsed = 0;
                if(autoPlayButton.isChecked()) {
                    handler.post(() -> makeNextMove());
                }
            }
        }
    }
}