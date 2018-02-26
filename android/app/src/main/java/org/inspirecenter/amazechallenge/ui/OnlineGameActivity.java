package org.inspirecenter.amazechallenge.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.blockly.model.Block;
import com.google.gson.Gson;

import org.inspirecenter.amazechallenge.Installation;
import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.api.ReplyWithGameFullState;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.GameFullState;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import static org.inspirecenter.amazechallenge.ui.MainActivity.setLanguage;
import static org.inspirecenter.amazechallenge.ui.PersonalizeActivity.PREFERENCE_KEY_EMAIL;

public class OnlineGameActivity extends AppCompatActivity {

    public static final String TAG = "aMazeChallenge";

    public static final long ONE_SECOND = 1000L;

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setLanguage(this);
        setContentView(R.layout.activity_online_game);

        gameView = findViewById(R.id.activity_online_game_game_view);
    }

    private Challenge challenge;
    private String email = null;

    private Handler handler;
    private Timer timer = new Timer();

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(OnlineGameActivity.this);
        email = sharedPreferences.getString(PREFERENCE_KEY_EMAIL, getString(R.string.Guest_email));

        // get challenge from intent
        challenge = (Challenge) getIntent().getSerializableExtra(OnlineChallengeActivity.PREFERENCE_KEY_CHALLENGE);
        if(challenge == null) {
            Log.e(TAG, "Invalid null argument 'challenge'in Intent");
            finish();
        }

        handler = new Handler();

        timer.schedule(new OnlineMazeRunner(), 0L, ONE_SECOND / 2); // todo
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();

        // todo ask user to confirm and withdraw
    }

    public void editCode(final View view) {
        startActivity(new Intent(OnlineGameActivity.this, BlocklyActivity.class).putExtra(BlocklyActivity.ONLINE_CALLING_ACTIVITY_KEY, true));
    }

    public void submitCode(final View view) {
        // todo use dialog to ask user to confirm
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String email = sharedPreferences.getString(PREFERENCE_KEY_EMAIL, getString(R.string.Guest_email));
        final String code = sharedPreferences.getString(BlocklyActivity.KEY_ALGORITHM_ACTIVITY_CODE, "");

        new SubmitCodeAsyncTask(email, code, challenge, getString(R.string.api_url), getString(R.string.magic)).execute();
    }

    private class SubmitCodeAsyncTask extends AsyncTask<Void, Void, String> {

        private final String email;
        private final String code;
        private final Challenge challenge;
        private final String apiUrlBase;
        private final String magic;

        SubmitCodeAsyncTask(final String email, final String code, final Challenge challenge, final String apiUrlBase, final String magic) {
            this.email = email;
            this.code = code;
            this.challenge = challenge;
            this.apiUrlBase = apiUrlBase;
            this.magic = magic;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(final Void... ignore) {
            try {
                final URL apiURL = new URL(apiUrlBase + "/submit-code?magic=" + magic
                        + "&challenge=" + challenge.getId()
                        + "&id=" + Installation.id(OnlineGameActivity.this));
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
            Snackbar.make(findViewById(R.id.activity_online_game), "Code uploaded \n" + reply, Snackbar.LENGTH_SHORT).show();
            Log.d(TAG, "reply: " + reply);
        }
    }

    private class GetGameStateAsyncTask extends AsyncTask<Void, Void, String> {

        private final String email;
        private final long challengeId;

        GetGameStateAsyncTask(final String email, final long challengeId) {
            this.email = email;
            this.challengeId = challengeId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(final Void... ignore) {
            InputStream inputStream = null;
            try {
                final String apiUrlBase = getString(R.string.api_url);
                final String magic = getString(R.string.magic);
                final URL apiURL = new URL(apiUrlBase + "/game-state?magic=" + magic + "&email=" + email + "&challenge=" + challengeId);
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
            final ReplyWithGameFullState replyWithFullGameState = new Gson().fromJson(reply, ReplyWithGameFullState.class);
            final GameFullState gameFullState = replyWithFullGameState.getGameFullState();
            if(gameFullState != null) {
                gameView.update(gameFullState);
                final boolean active = gameFullState.getActivePlayerIDs().contains(Installation.id(OnlineGameActivity.this));
                final boolean queued = gameFullState.getQueuedPlayerIDs().contains(Installation.id(OnlineGameActivity.this));
                // todo save to preferences and check if sounds needs to be played
            }
        }
    }

    public static String convertStreamToString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private class OnlineMazeRunner extends TimerTask {
        @Override
        public void run() {
            handler.post(() -> {
//                Log.d(TAG, "getting game-state for challenge: " + challenge.getName());
                new GetGameStateAsyncTask(email, challenge.getId()).execute();
            });
        }
    }
}