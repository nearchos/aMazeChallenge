package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.inspirecenter.amazechallenge.Installation;
import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.api.ChallengesReply;
import org.inspirecenter.amazechallenge.api.Reply;
import org.inspirecenter.amazechallenge.api.ReplyWithErrors;
import org.inspirecenter.amazechallenge.model.AmazeColor;
import org.inspirecenter.amazechallenge.model.AmazeIcon;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.Vector;

import static org.inspirecenter.amazechallenge.ui.MainActivity.setLanguage;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_COLOR;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_EMAIL;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_ICON;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_NAME;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_SHAPE_CODE;

public class OnlineChallengeActivity extends AppCompatActivity implements ChallengeAdapter.OnChallengeSelectedListener, ChallengeAdapter.OnChallengeLongSelectionListener {

    public static final String TAG = "aMazeChallenge";

    public static final String PREFERENCE_KEY_CHALLENGE = "pref-challenge";

    private ProgressBar progressBar;
    private RecyclerView challengesRecyclerView;
    private ChallengeAdapter challengeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setLanguage(this);
        setContentView(R.layout.activity_online_challenge);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.activity_online_challenge_progress_bar);

        challengesRecyclerView = findViewById(R.id.activity_online_challenge_list_view);
        challengesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        challengesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        challengeAdapter = new ChallengeAdapter(this, this);
        challengesRecyclerView.setAdapter(challengeAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // todo if no code was submitted yet, advise the player

        // start online request
        new FetchChallengesAsyncTask().execute();
    }

    @Override
    public void onChallengeSelected(final Challenge challenge) {
        Snackbar.make(findViewById(R.id.activity_online_challenge), "Joining " + challenge.getName() + " ...", Snackbar.LENGTH_SHORT).show();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(OnlineChallengeActivity.this);
        final String email = sharedPreferences.getString(PREFERENCE_KEY_EMAIL, getString(R.string.Guest_email));
        final String name = sharedPreferences.getString(PREFERENCE_KEY_NAME, getString(R.string.Guest));
        final String colorName = sharedPreferences.getString(PREFERENCE_KEY_COLOR, AmazeColor.getDefault().name());
        final String iconName = sharedPreferences.getString(PREFERENCE_KEY_ICON, AmazeIcon.getDefault().getName());
        final String shapeCode = sharedPreferences.getString(PREFERENCE_SHAPE_CODE, "triangle");
        new JoinChallengeAsyncTask(email, name, colorName, iconName, shapeCode, challenge).execute();
    }

    @Override
    public void onChallengeLongSelect(Challenge challenge) {
        //Do nothing
    }

    private class FetchChallengesAsyncTask extends AsyncTask<Void, Void, Reply> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            challengesRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Reply doInBackground(Void... v) {
            final Vector<Challenge> challenges = new Vector<>();
            try {
                final String apiUrlBase = getString(R.string.api_url);
                final URL apiURL = new URL(apiUrlBase + "/challenges");
                Log.d(TAG, "Opening URL: " + apiURL.toString());
                final InputStream inputStream = apiURL.openStream();
                final String json = convertStreamToString(inputStream);
                Log.v(TAG, "Read JSON: " + json);
                final Gson gson = new Gson();
                final ChallengesReply challengesReply = gson.fromJson(json, ChallengesReply.class);
                return challengesReply;
            } catch (IOException e) {
                // show message in snackbar
                Snackbar.make(findViewById(R.id.activity_online_challenge), "Error while accessing list of challenges: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                // log error
                Log.e(TAG, "Error: " + e.getMessage());
                return new ReplyWithErrors(e.getMessage());
            }
       }

        @Override
        protected void onPostExecute(final Reply reply) {
            super.onPostExecute(reply);
            progressBar.setVisibility(View.GONE);
            // check if reply is not null first
            if(reply != null && reply instanceof ChallengesReply) {
                final Collection<Challenge> challenges = ((ChallengesReply) reply).getChallenges();
                challengeAdapter.clear();
                challengeAdapter.addAll(challenges);
                challengesRecyclerView.setVisibility(View.VISIBLE);
            } else {
                // show message in snack-bar
                final Vector<String> messages = new Vector<>();
                if(reply == null) messages.add("Server is down or connection is cancelled");
                else messages.addAll(((ReplyWithErrors) reply).getErrors());
                Snackbar.make(findViewById(R.id.activity_online_challenge), "Error(s): " + messages, Snackbar.LENGTH_SHORT).show();
                // also log warning
                Log.w(TAG, "Error messages: " + messages);
            }
        }
    }

    private class JoinChallengeAsyncTask extends AsyncTask<Void, Void, String> {

        private final String email;
        private final String name;
        private final String colorName;
        private final String iconName;
        private final String shapeCode;
        private final Challenge challenge;

        JoinChallengeAsyncTask(final String email, final String name, final String colorName, final String iconName, final String shapeCode, final Challenge challenge) {
            this.email = email;
            this.name = name;
            this.colorName = colorName;
            this.iconName = iconName;
            this.shapeCode = shapeCode;
            this.challenge = challenge;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... ignore) {
            try {
                final String apiUrlBase = getString(R.string.api_url);
                final String magic = getString(R.string.magic);
                final URL apiURL = new URL(apiUrlBase + "/join?magic=" + magic + "&name=" + name
                        + "&email=" + email + "&color=" + colorName + "&icon=" + iconName
                        + "&shape=" + shapeCode + "&challenge=" + challenge.getId()
                        + "&id=" + Installation.id(OnlineChallengeActivity.this));
                Log.d(TAG, "apiURL: " + apiURL.toString());
                final InputStream inputStream = apiURL.openStream();
                return convertStreamToString(inputStream);
            } catch (IOException e) {
                // show message in snackbar
                Snackbar.make(findViewById(R.id.activity_online_challenge), "Error while joining challenge: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                // log error
                Log.e(TAG, "Error: " + Arrays.toString(e.getStackTrace()));
                return "Error: " + Arrays.toString(e.getStackTrace());
            }
        }

        @Override
        protected void onPostExecute(final String reply) {
            super.onPostExecute(reply);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(OnlineChallengeActivity.this, "Joined \n" + reply, Toast.LENGTH_SHORT).show();
            // parse and check reply
            try {
                final JSONObject replyJsonObject = new JSONObject(reply);
                if("ok".equalsIgnoreCase(replyJsonObject.getString("status"))) {
                    // start online game activity
                    final Intent intent = new Intent(OnlineChallengeActivity.this, OnlineGameActivity.class);
                    intent.putExtra(PREFERENCE_KEY_CHALLENGE, challenge);
                    startActivity(intent);
                } else {
                    Snackbar.make(findViewById(R.id.activity_online_challenge), "Error while joining challenge: " + challenge.getName(), Snackbar.LENGTH_SHORT).show();
                    Log.w(TAG, reply);
                }
            } catch (JSONException jsone) {
                Snackbar.make(findViewById(R.id.activity_online_challenge), "Error while parsing reply: " + jsone.getMessage(), Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, reply);
            }

        }
    }

    public static String convertStreamToString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}