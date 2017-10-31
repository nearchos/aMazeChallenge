package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.LoadDialogListAdapter;
import org.inspirecenter.amazechallenge.model.SelectColorDialogListAdapter;

import java.util.ArrayList;

import static org.inspirecenter.amazechallenge.model.PlayerColor.PLAYER_COLORS;

public class PersonalizeActivity extends AppCompatActivity {

    public static final String PREFERENCE_KEY_NAME = "pref-name";
    public static final String PREFERENCE_KEY_COLOR_ID = "pref-color-id";

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
        int userColorID = PreferenceManager.getDefaultSharedPreferences(this).getInt(PREFERENCE_KEY_COLOR_ID, 0);
        userImageView.setColorFilter(Color.parseColor(PLAYER_COLORS[userColorID].getHex()));
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
        AlertDialog.Builder colorSelectBuilder = new AlertDialog.Builder(this);
        ListView colorsList = new ListView(PersonalizeActivity.this);

        SelectColorDialogListAdapter listAdapter = new SelectColorDialogListAdapter(this, this);
        colorsList.setAdapter(listAdapter);

        LinearLayout.LayoutParams lp_list = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        colorsList.setLayoutParams(lp_list);
        colorSelectBuilder.setView(colorsList);
        colorSelectBuilder.setTitle(R.string.color_select_title);
        colorSelectBuilder.setMessage(" ");
        final AlertDialog colorDialog = colorSelectBuilder.create();
        colorDialog.show();

        colorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PreferenceManager.getDefaultSharedPreferences(PersonalizeActivity.this).edit().putInt(PREFERENCE_KEY_COLOR_ID, i).apply();
                userImageView.setColorFilter(Color.parseColor(PLAYER_COLORS[i].getHex()));
                colorDialog.dismiss();
            }
        });

    }

    public void done(final View view) {
        finish();
    }
}