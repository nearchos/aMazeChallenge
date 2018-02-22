package org.inspirecenter.amazechallenge.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.Language;
import org.mozilla.javascript.tools.jsc.Main;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_PREF_LEARNED = "pref-learned";
    public static final String KEY_PREF_PERSONALIZED = "pref-personalized";
    public static final String KEY_PREF_EDITED_CODE = "pref-code-edited";
    public static final String KEY_PREF_LOCALLY_TESTED = "pref-local-tested";
    public static final String KEY_PREF_PLAYED_ONLINE = "pref-played-online";
    public static final String KEY_PREF_SOUND = "pref-sound";

    private Button buttonLearn;
    private Button buttonPersonalize;
    private Button buttonEditYourCode;
    private Button buttonTestLocally;
    private Button buttonPlayOnline;
    private Button buttonAbout;
    private Button buttonGenerator;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        buttonLearn = findViewById(R.id.button_learn);
        buttonPersonalize = findViewById(R.id.button_personalize);
        buttonEditYourCode = findViewById(R.id.edit_your_code);
        buttonTestLocally = findViewById(R.id.training);
        buttonPlayOnline = findViewById(R.id.play_online);
        buttonGenerator = findViewById(R.id.generator);
        buttonAbout = findViewById(R.id.about);
        fab = findViewById(R.id.activity_main_sound);
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
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
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

    public void mazeDesigner(final View view) {
        startActivity(new Intent(this, MazeDesignerActivity.class));
    }

    public void about(final View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void sound(View view) {
        boolean sound = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEY_PREF_SOUND, true);
        sound = !sound;
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(KEY_PREF_SOUND, sound).apply();
        if (sound) fab.setImageDrawable(getDrawable(R.drawable.ic_volume_up_black_24dp));
        else fab.setImageDrawable(getDrawable(R.drawable.ic_volume_off_black_24dp));
    }

    public void language(View view) {

        CharSequence[] langs = new CharSequence[Language.values().length];
        for (int i = 0; i < Language.values().length; i++) {
            langs[i] = Language.values()[i].toString();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_language);
        builder.setItems(langs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Locale myLocale = new Locale(Language.values()[which].getLocale());
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

}