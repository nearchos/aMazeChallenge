package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Shape;
import org.inspirecenter.amazechallenge.model.AmazeColor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_COLOR;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_EMAIL;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_NAME;

public class TrainingActivity extends AppCompatActivity implements ChallengeAdapter.OnChallengeSelectedListener {

    public static final String TAG = "aMaze";

    public static final String CHALLENGES_PATH = "challenges";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        final int userColorIndex = PreferenceManager.getDefaultSharedPreferences(this).getInt(PREFERENCE_KEY_COLOR, 0);
        final AmazeColor userAmazeColor = AmazeColor.values()[userColorIndex];
        // todo set selected image/avatar
        final ImageView userImageView = findViewById(R.id.activity_training_user_image);
        userImageView.setColorFilter(Color.parseColor(userAmazeColor.getCode()));

        final String name = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_NAME, getString(R.string.Guest));
        final TextView nameTextView = findViewById(R.id.activity_training_user_name);
        nameTextView.setText(name);
        nameTextView.setTextColor(Color.parseColor(userAmazeColor.getCode()));
        final String email = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_EMAIL, getString(R.string.Guest_email));
        final TextView emailTextView = findViewById(R.id.activity_training_user_email);
        emailTextView.setText(email);
        emailTextView.setTextColor(Color.parseColor(userAmazeColor.getCode()));

        final RecyclerView challengesRecyclerView = findViewById(R.id.activity_training_mazes_list_view);
        challengesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        challengesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        final Gson gson = new Gson();
        final ChallengeAdapter challengeAdapter = new ChallengeAdapter(this);
        try {
            final String [] allAssets = getAssets().list(CHALLENGES_PATH);
            for(final String asset : allAssets) {
                final InputStream inputStream = getAssets().open(CHALLENGES_PATH + "/" + asset);
                final String json = convertStreamToString(inputStream);
                inputStream.close();
                final Challenge challenge = gson.fromJson(json, Challenge.class);
                challengeAdapter.add(challenge);
                inputStream.close();
            }
            challengesRecyclerView.setAdapter(challengeAdapter);
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("challenges", "Error: " + e.getMessage());
            finish();
        }
    }

    @Override
    public void onChallengeSelected(final Challenge challenge) {
        final Grid selectedGrid = challenge.getGrid();
        Log.d(TAG, "selectedGrid: " + selectedGrid );
        final Game game = new Game(challenge.getId(), selectedGrid);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        final Map<String,Serializable> parametersMap = new HashMap<>();
        final String code = sharedPreferences.getString(BlocklyActivity.KEY_ALGORITHM_ACTIVITY_CODE, "");
//        parametersMap.put(InterpretedMazeSolver.PARAMETER_KEY_CODE, code);

        final String email = sharedPreferences.getString(PREFERENCE_KEY_EMAIL, "guest@example.com");
        final String name = sharedPreferences.getString(PREFERENCE_KEY_NAME, "Guest");
        final int userColorIndex = PreferenceManager.getDefaultSharedPreferences(this).getInt(PREFERENCE_KEY_COLOR, 0);
        final AmazeColor userAmazeColor = AmazeColor.values()[userColorIndex];
        final Shape shape = Shape.TRIANGLE; // todo enable user selection
        final Player player = new Player(email, name, userAmazeColor, shape);
//        game.addPlayer(player, InterpretedMazeSolver.class, parametersMap);
        game.addPlayer(player, code);

        final Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.SELECTED_GAME_KEY, game);
        startActivity(intent);
    }

    public static String convertStreamToString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}