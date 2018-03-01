package org.inspirecenter.amazechallenge.ui;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.AmazeColor;
import org.inspirecenter.amazechallenge.model.AmazeIcon;

import java.util.regex.Pattern;

import static org.inspirecenter.amazechallenge.ui.BlocklyActivity.INTENT_KEY_NEXT_ACTIVITY;
import static org.inspirecenter.amazechallenge.ui.ColorFragment.isBrightColor;
import static org.inspirecenter.amazechallenge.ui.MainActivity.setLanguage;

public class PersonalizeActivity extends AppCompatActivity {

    public static final String PREFERENCE_KEY_NAME = "pref-name";
    public static final String PREFERENCE_KEY_EMAIL = "pref-email";
    public static final String PREFERENCE_KEY_COLOR = "pref-color";
    public static final String PREFERENCE_SHAPE_CODE = "pref-shape";
    public static final String PREFERENCE_KEY_ICON = "pref-icon";

    private EditText nameEditText;
    private EditText emailEditText;
    private GIFView gifView;
    private Button selectColorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setLanguage(this);
        setContentView(R.layout.activity_personalize);

        setTitle(R.string.Personalize);
        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        nameEditText = findViewById(R.id.activity_personalize_name);
        emailEditText = findViewById(R.id.activity_personalize_email);
        gifView = findViewById(R.id.activity_personalize_icon);
        selectColorButton = findViewById(R.id.activity_personalize_button_select_color);
        selectColorButton.setOnClickListener(view -> startActivity(new Intent(this, PersonalizationSliderActivity.class)));
    }

    public static final int PERMISSIONS_REQUEST_GET_ACCOUNT = 42;

    @Override
    protected void onResume() {
        super.onResume();

        // load personalization preferences
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // update user color
        {
            final String userColorName = sharedPreferences.getString(PREFERENCE_KEY_COLOR, AmazeColor.BLACK.name());
            final AmazeColor userAmazeColor = AmazeColor.valueOf(userColorName);
            final int userColor = Color.parseColor(userAmazeColor.getHexCode());
            selectColorButton.setBackgroundColor(userColor);
            selectColorButton.setTextColor(isBrightColor(Color.parseColor(userAmazeColor.getHexCode())) ? Color.BLACK : Color.WHITE);
        }
        // update user icon/avatar
        {
            final String userIconName = sharedPreferences.getString(PREFERENCE_KEY_ICON, AmazeIcon.ICON_1.getName());
            final AmazeIcon selectedAmazeIcon = AmazeIcon.getByName(userIconName);
            gifView.setImageResource(getDrawableResourceId(selectedAmazeIcon));
        }
        // update user email and name
        {
            final String email = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_EMAIL, "");
            final String name = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_NAME, getString(R.string.Guest));
            if(email.isEmpty()) {
                if(checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] { Manifest.permission.GET_ACCOUNTS }, PERMISSIONS_REQUEST_GET_ACCOUNT);
                } else {
                    selectAccount();
                }
            } else {
                emailEditText.setText(email);
                nameEditText.setText(name);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        save();
        final String name = nameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().toLowerCase().trim();

        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(PREFERENCE_KEY_EMAIL, email).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(PREFERENCE_KEY_NAME, name).apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String [] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_GET_ACCOUNT) {
            selectAccount();
        }
        // do nothing if denied
    }

    private void selectAccount() {
        final Pattern emailPattern = Patterns.EMAIL_ADDRESS;

        final AccountManager accountManager = AccountManager.get(this);
        final Account [] accounts = accountManager.getAccounts();

        String email = getString(R.string.Guest_email);
        String name = getString(R.string.Guest);

        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                email = account.name;
                name = email.indexOf('@') > -1 ? email.substring(0, email.indexOf('@')) : email;
                break;
            }
        }

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(PREFERENCE_KEY_EMAIL, email).apply();
        sharedPreferences.edit().putString(PREFERENCE_KEY_NAME, name).apply();

        emailEditText.setText(email);
        nameEditText.setText(name);
    }

    private void save() {
        // verify name is non-empty and email is valid
        final String name = nameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().toLowerCase().trim();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(name.isEmpty()) {
            final String defaultName = sharedPreferences.getString(PREFERENCE_KEY_NAME, getString(R.string.Guest));
            sharedPreferences.edit().putString(PREFERENCE_KEY_NAME, defaultName).apply();
        }
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            final String defaultEmail = sharedPreferences.getString(PREFERENCE_KEY_EMAIL, getString(R.string.Guest_email));
            sharedPreferences.edit().putString(PREFERENCE_KEY_EMAIL, defaultEmail).apply();
        }

        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(MainActivity.KEY_PREF_PERSONALIZED, true).apply();
    }

    public void done(final View view) {
        save();
        final Intent intent = new Intent(this, BlocklyActivity.class);
        intent.putExtra(INTENT_KEY_NEXT_ACTIVITY, TrainingActivity.class.getCanonicalName());
        startActivity(intent);
    }

    int getDrawableResourceId(final AmazeIcon amazeIcon) {
        return getResources().getIdentifier(amazeIcon.getResourceName(), "drawable", this.getPackageName());
    }
}