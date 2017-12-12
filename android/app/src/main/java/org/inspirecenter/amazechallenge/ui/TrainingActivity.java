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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.AmazeIcon;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Shape;
import org.inspirecenter.amazechallenge.model.AmazeColor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_COLOR;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_EMAIL;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_ICON;
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

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final String userColorName = sharedPreferences.getString(PREFERENCE_KEY_COLOR, AmazeColor.getDefault().getName());
        final AmazeColor userAmazeColor = AmazeColor.getByName(userColorName);

        final String userIconName = sharedPreferences.getString(PersonalizeActivity.PREFERENCE_KEY_ICON, AmazeIcon.ICON_1.getName());
        final AmazeIcon userAmazeIcon = AmazeIcon.getByName(userIconName);

        final GIFView gifView = findViewById(R.id.activity_training_icon);
        gifView.setImageResource(getResources().getIdentifier(userAmazeIcon.getResourceName(), "drawable", this.getPackageName()));
        gifView.play();

        final String name = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_NAME, getString(R.string.Guest));
        final TextView nameTextView = findViewById(R.id.activity_training_user_name);
        nameTextView.setText(name);
        nameTextView.setTextColor(Color.parseColor(userAmazeColor.getCode()));
        final String email = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_EMAIL, getString(R.string.Guest_email));
        final TextView emailTextView = findViewById(R.id.activity_training_user_email);
        emailTextView.setText(email);
        emailTextView.setTextColor(Color.parseColor(userAmazeColor.getCode()));

        final RecyclerView challengesRecyclerView = findViewById(R.id.activity_training_challenges_list_view);
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
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final String email = sharedPreferences.getString(PREFERENCE_KEY_EMAIL, "guest@example.com");
        final String name = sharedPreferences.getString(PREFERENCE_KEY_NAME, "Guest");
        final String userColorName = sharedPreferences.getString(PREFERENCE_KEY_COLOR, AmazeColor.getDefault().getName());
        final AmazeColor userAmazeColor = AmazeColor.getByName(userColorName);
        final String userIconName = sharedPreferences.getString(PREFERENCE_KEY_ICON, AmazeIcon.ICON_1.getName());
        final AmazeIcon userAmazeIcon = AmazeIcon.getByName(userIconName);
        final Shape shape = Shape.TRIANGLE; // todo enable user selection
        final Player player = new Player(email, name, userAmazeColor, userAmazeIcon, shape);

        final Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.SELECTED_CHALLENGE_KEY, challenge);
        intent.putExtra(GameActivity.SELECTED_PLAYER_KEY, player);
        startActivity(intent);
    }

    public static String convertStreamToString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}