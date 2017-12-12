package org.inspirecenter.amazechallenge.ui;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.AmazeColor;
import org.inspirecenter.amazechallenge.model.AmazeIcon;

public class PersonalizeActivity extends AppCompatActivity {

    public static final String PREFERENCE_KEY_NAME = "pref-name";
    public static final String PREFERENCE_KEY_EMAIL = "pref-email";
    public static final String PREFERENCE_KEY_COLOR = "pref-color";
    public static final String PREFERENCE_SHAPE_CODE = "pref-shape";
    public static final String PREFERENCE_KEY_ICON = "pref-icon";

    private EditText nameEditText;
    private EditText emailEditText;
    private GIFView gifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize);

        setTitle(R.string.Personalize);
        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        nameEditText = findViewById(R.id.activity_personalize_name);
        emailEditText = findViewById(R.id.activity_personalize_email);
        gifView = findViewById(R.id.activity_personalize_icon);
        updatePersonalization();
        gifView.play();
    }

    public static final int PERMISSIONS_REQUEST_GET_ACCOUNT = 42;

    @Override
    protected void onResume() {
        super.onResume();
        final String name = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_NAME, getString(R.string.Guest));
        nameEditText.setText(name);

        updatePersonalization();

        final String email = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_EMAIL, getString(R.string.Guest_email));
        if(email.isEmpty()) {
            if(checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.GET_ACCOUNTS }, PERMISSIONS_REQUEST_GET_ACCOUNT);
            } else {
                selectAccount();
            }
        } else {
            emailEditText.setText(email);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_GET_ACCOUNT) {
            selectAccount();
        }
        // do nothing if denied
    }

    @SuppressLint("SetTextI18n")
    private void selectAccount() {
        final AccountManager accountManager = AccountManager.get(this);
        final Account[] accounts = accountManager.getAccountsByType("com.google");

        if(accounts.length == 0) {
            // ignore
            emailEditText.setText("guest@example.com");
        } else { // accounts.length >= 1
            // select the first email
            emailEditText.setText(accounts[0].name);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        final String name = nameEditText.getText().toString().trim();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(PREFERENCE_KEY_NAME, name).apply();
        final String email = emailEditText.getText().toString().toLowerCase().trim();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(PREFERENCE_KEY_EMAIL, email).apply();
    }

    public void selectColor(final View view) {
        startActivity(new Intent(this, PersonalizationSliderActivity.class));
    }//end selectColor()

    public void done(final View view) {
        // todo verify name is non-empty and email is valid
        finish();
    }

    private void updatePersonalization() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        {
            final String userColorName = sharedPreferences.getString(PREFERENCE_KEY_COLOR, AmazeColor.COLOR_BLACK.getName());
            final AmazeColor userAmazeColor = AmazeColor.getByName(userColorName);
            // todo use selected color?
        }
        {
            final String userIconName = sharedPreferences.getString(PREFERENCE_KEY_ICON, AmazeIcon.ICON_1.getName());
            final AmazeIcon selectedAmazeIcon = AmazeIcon.getByName(userIconName);
            gifView.setImageResource(getDrawableResourceId(selectedAmazeIcon));
            //gifView.setBackgroundColor(userAmazeColor.getCode());
        }
    }

    int getDrawableResourceId(final AmazeIcon amazeIcon) {
        return getResources().getIdentifier(amazeIcon.getResourceName(), "drawable", this.getPackageName());
    }
}