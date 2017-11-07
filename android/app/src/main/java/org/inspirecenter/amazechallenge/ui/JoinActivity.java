package org.inspirecenter.amazechallenge.ui;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.api.ChallengesReply;
import org.inspirecenter.amazechallenge.api.JsonParser;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_EMAIL;

public class JoinActivity extends AppCompatActivity {

    public static final String TAG = "aMazeChallenge";

    private ProgressBar progressBar;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.activity_join_progress_bar);
        resultTextView = findViewById(R.id.activity_join_result);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final String email = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_EMAIL, "");
        final Challenge challenge = (Challenge) getIntent().getSerializableExtra("challenge");

        // start online request
        new JoinChallengeAsyncTask(email).execute(challenge.getId());
    }

    private class JoinChallengeAsyncTask extends AsyncTask<Long, Void, String> {

        private final String email;

        JoinChallengeAsyncTask(final String email) {
            this.email = email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            resultTextView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(final Long... challengeId) {
            try {
                final String apiUrlBase = getString(R.string.api_url);
                final String magic = getString(R.string.magic);
                final URL apiURL = new URL(apiUrlBase + "/join?magic=" + magic + "&email=" + email + "&id=" + challengeId[0]);
                final InputStream inputStream = apiURL.openStream();
                return convertStreamToString(inputStream);
            } catch (IOException e) {
                Log.e("challenges", "Error: " + Arrays.toString(e.getStackTrace()));
                return "Error: " + Arrays.toString(e.getStackTrace());
            }
        }

        @Override
        protected void onPostExecute(final String reply) {
            super.onPostExecute(reply);
            progressBar.setVisibility(View.GONE);
            resultTextView.setVisibility(View.VISIBLE);
            resultTextView.setText(reply);
        }
    }

    public static String convertStreamToString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}