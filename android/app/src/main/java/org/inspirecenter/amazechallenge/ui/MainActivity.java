package org.inspirecenter.amazechallenge.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import org.inspirecenter.amazechallenge.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_PREF_LEARNED = "pref-learned";
    public static final String KEY_PREF_PERSONALIZED = "pref-personalized";
    public static final String KEY_PREF_EDITED_CODE = "pref-code-edited";
    public static final String KEY_PREF_LOCALLY_TESTED = "pref-local-tested";
    public static final String KEY_PREF_PLAYED_ONLINE = "pref-played-online";

    private Button buttonLearn;
    private Button buttonPersonalize;
    private Button buttonEditYourCode;
    private Button buttonTestLocally;
    private Button buttonPlayOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

//        final Toolbar toolbar = findViewById(org.inspirecenter.amazechallenge.R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        final DrawerLayout drawer = findViewById(org.inspirecenter.amazechallenge.R.id.drawer_layout);
//        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, org.inspirecenter.amazechallenge.R.string.navigation_drawer_open, org.inspirecenter.amazechallenge.R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        final NavigationView navigationView = findViewById(org.inspirecenter.amazechallenge.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        buttonLearn = findViewById(R.id.button_learn);
        buttonPersonalize = findViewById(R.id.button_personalize);
        buttonEditYourCode = findViewById(R.id.edit_your_code);
        buttonTestLocally = findViewById(R.id.test_locally);
        buttonPlayOnline = findViewById(R.id.play_online);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final boolean hasLearned = sharedPreferences.getBoolean(KEY_PREF_LEARNED, false);
        final boolean hasPersonalized = sharedPreferences.getBoolean(KEY_PREF_PERSONALIZED, false);
        final boolean hasEditedCode = sharedPreferences.getBoolean(KEY_PREF_EDITED_CODE, false);
        final boolean hasLocallyTested = sharedPreferences.getBoolean(KEY_PREF_LOCALLY_TESTED, false);
        final boolean hasPlayedOnline = sharedPreferences.getBoolean(KEY_PREF_PLAYED_ONLINE, false);

        // edit buttons
        buttonLearn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_help_black_24dp, 0, hasLearned ? R.drawable.ic_check_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp, 0);
        buttonPersonalize.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_pin_black_24dp, 0, hasPersonalized ? R.drawable.ic_check_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp, 0);
        buttonEditYourCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_code_black_24dp, 0, hasEditedCode ? R.drawable.ic_check_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp, 0);
        buttonTestLocally.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_circle_filled_black_24dp, 0, hasLocallyTested ? R.drawable.ic_check_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp, 0);
        buttonPlayOnline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cloud_upload_black_24dp, 0, hasPlayedOnline ? R.drawable.ic_check_black_24dp : R.drawable.ic_check_box_outline_blank_black_24dp, 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(org.inspirecenter.amazechallenge.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_personalize) {
            personalize(null);
        } else if (id == R.id.nav_code_editor) {
            codeEditor(null);
        } else if (id == R.id.nav_testing) {
            training(null);
        } else if (id == R.id.nav_online_challenge) {
            onlineChallenge(null);
        } else if (id == R.id.nav_learn) {
            learn(null);
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        final DrawerLayout drawer = findViewById(org.inspirecenter.amazechallenge.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void learn(final View view) {
        startActivity(new Intent(this, HelpActivity.class));
    }

    public void personalize(final View view) {
        startActivity(new Intent(this, PersonalizeActivity.class));
    }

    public void codeEditor(final View view) {
        startActivity(new Intent(this, BlocklyActivity.class));
    }

    public void training(final View view) {
        startActivity(new Intent(this, TrainingActivity.class));
    }

    public void onlineChallenge(final View view) {
        startActivity(new Intent(this, OnlineChallengeActivity.class));
    }
}