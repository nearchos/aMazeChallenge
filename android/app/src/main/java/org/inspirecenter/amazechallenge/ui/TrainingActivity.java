package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Shape;
import org.inspirecenter.amazechallenge.model.ShapeColor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TrainingActivity extends AppCompatActivity implements ChallengeAdapter.OnChallengeSelectedListener {

    public static final String CHALLENGES_PATH = "challenges";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        final RecyclerView challengesRecyclerView = findViewById(R.id.activity_training_mazes_list_view);
        challengesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        challengesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        final ChallengeAdapter challengeAdapter = new ChallengeAdapter(this);
        try {
            final String [] allAssets = getAssets().list(CHALLENGES_PATH);
            for(final String asset : allAssets) {
                final InputStream inputStream = getAssets().open(CHALLENGES_PATH + "/" + asset);
                final String data = convertStreamToString(inputStream);
                inputStream.close();
                final Challenge challenge = Challenge.parseJSON(new JSONObject(data));
                challengeAdapter.add(challenge);
                inputStream.close();
            }
            challengesRecyclerView.setAdapter(challengeAdapter);
        } catch (IOException | JSONException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("challenges", "Error: " + e.getMessage());
            finish();
        }
    }

    @Override
    public void onChallengeSelected(final Challenge challenge) {
        final Grid selectedGrid = challenge.getGrid();
        final Game game = new Game(selectedGrid);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final Map<String,Serializable> parametersMap = new HashMap<>();
        final String code = sharedPreferences.getString(BlocklyActivity.KEY_ALGORITHM_ACTIVITY_CODE, "");
        parametersMap.put(InterpretedMazeSolver.PARAMETER_KEY_CODE, code);

        final String name = sharedPreferences.getString(PersonalizeActivity.PREFERENCE_KEY_NAME, "Guest");
        final ShapeColor shapeColor = ShapeColor.PLAYER_COLOR_RED; // todo enable user selection
        final Shape shape = Shape.TRIANGLE; // todo enable user selection
        final Player player = new Player(name, shapeColor, shape, InterpretedMazeSolver.class);
        game.addPlayer(player, parametersMap);

        final Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.SELECTED_GAME_KEY, game);
        startActivity(intent);
    }

    public static String convertStreamToString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

}