package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.R;

public class PersonalizeActivity extends AppCompatActivity {

    public static final String PREFERENCE_KEY_NAME = "pref-name";
    public static final String PREFERENCE_KEY_COLOR = "pref-color";

    private EditText nameEditText;
    private ImageView userImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize);

        setTitle(R.string.Personalize);
        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        nameEditText = findViewById(R.id.activity_personalize_name);
        userImageView = findViewById(R.id.activity_personalize_user_image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String name = PreferenceManager.getDefaultSharedPreferences(this).getString(PREFERENCE_KEY_NAME, getString(R.string.Guest));
        nameEditText.setText(name);
    }

    @Override
    protected void onPause() {
        super.onPause();
        final String name = nameEditText.getText().toString();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(PREFERENCE_KEY_NAME, name).apply();
    }

    public void selectColor(final View view) {
        Toast.makeText(this, "Select color...", Toast.LENGTH_SHORT).show(); // todo
    }

    public void done(final View view) {
        finish();
    }
}