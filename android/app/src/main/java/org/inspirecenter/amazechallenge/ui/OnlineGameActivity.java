package org.inspirecenter.amazechallenge.ui;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.Challenge;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_EMAIL;

public class OnlineGameActivity extends AppCompatActivity {

    public static final String TAG = "aMazeChallenge";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game);

        progressBar = findViewById(R.id.activity_online_game_progress_bar);
    }

    private Challenge challenge;

    @Override
    protected void onResume() {
        super.onResume();

        // get challenge from intent
        challenge = (Challenge) getIntent().getSerializableExtra(OnlineChallengeActivity.PREFERENCE_KEY_CHALLENGE);
        if(challenge == null) {
            Log.e(TAG, "Invalid null argument 'challenge'in Intent");
            finish();
        }
    }

    public void editCode(final View view) {
        // todo
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String email = sharedPreferences.getString(PREFERENCE_KEY_EMAIL, getString(R.string.Guest_email));
        Log.d(TAG, "getting game-state for challenge: " + challenge.getName());
        new GetGameStateAsyncTask(email).execute();
    }

    public void submitCode(final View view) {
        // todo use dialog to ask user to confirm
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String email = sharedPreferences.getString(PREFERENCE_KEY_EMAIL, getString(R.string.Guest_email));
        final String code = sharedPreferences.getString(BlocklyActivity.KEY_ALGORITHM_ACTIVITY_CODE, "");

        new SubmitCodeAsyncTask(email, code, challenge).execute();
    }

    private class SubmitCodeAsyncTask extends AsyncTask<Void, Void, String> {

        private final String email;
        private final String code;
        private final Challenge challenge;

        SubmitCodeAsyncTask(final String email, final String code, final Challenge challenge) {
            this.email = email;
            this.code = code;
            this.challenge = challenge;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(final Void... ignore) {
            try {
                final String apiUrlBase = getString(R.string.api_url);
                final String magic = getString(R.string.magic);
                final URL apiURL = new URL(apiUrlBase + "/submit-code?magic=" + magic + "&email=" + email + "&id=" + challenge.getId());
                Log.d(TAG, "apiURL: " + apiURL.toString());
                final HttpURLConnection httpURLConnection = (HttpURLConnection) apiURL.openConnection();
                httpURLConnection.setDoInput(true); // Allow Inputs
                httpURLConnection.setDoOutput(true); // Allow Outputs
                httpURLConnection.setUseCaches(false); // Don't use a Cached Copy
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                final DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.write(code.getBytes());
                dataOutputStream.close();

                final InputStream inputStream = httpURLConnection.getInputStream();
                return convertStreamToString(inputStream);
            } catch (IOException e) {
                // show message in snackbar
                Snackbar.make(findViewById(R.id.activity_online_game), "Error while submitting code: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                // log error
                Log.e(TAG, "Error: " + Arrays.toString(e.getStackTrace()));
                return "Error: " + Arrays.toString(e.getStackTrace());
            }
        }

        @Override
        protected void onPostExecute(final String reply) {
            super.onPostExecute(reply);
            progressBar.setVisibility(View.GONE);
            Snackbar.make(findViewById(R.id.activity_online_game), "Code uploaded \n" + reply, Snackbar.LENGTH_SHORT).show();
            Log.d(TAG, "reply: " + reply);
        }
    }

    private class GetGameStateAsyncTask extends AsyncTask<Void, Void, String> {

        private final String email;

        GetGameStateAsyncTask(final String email) {
            this.email = email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(final Void... ignore) {
            InputStream inputStream = null;
            try {
                final String apiUrlBase = getString(R.string.api_url);
                final String magic = getString(R.string.magic);
                final URL apiURL = new URL(apiUrlBase + "/game-state?magic=" + magic + "&email=" + email + "&id=" + challenge.getId());
                Log.d(TAG, "apiURL: " + apiURL.toString());
                final HttpURLConnection httpURLConnection = (HttpURLConnection) apiURL.openConnection();
                httpURLConnection.setDoInput(true); // Allow Inputs
                httpURLConnection.setUseCaches(false); // Don't use a Cached Copy
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                inputStream = httpURLConnection.getInputStream();
                return convertStreamToString(inputStream);
            } catch (IOException e) {
                // show message in snackbar
                Snackbar.make(findViewById(R.id.activity_online_game), "Error while getting game-state: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                // log error
                Log.e(TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                return "Error: " + Arrays.toString(e.getStackTrace());
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (IOException ioe) {
                    Log.e(TAG, "Error: " + ioe.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(final String reply) {
            super.onPostExecute(reply);
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "reply: " + reply);
        }
    }

    public static String convertStreamToString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
