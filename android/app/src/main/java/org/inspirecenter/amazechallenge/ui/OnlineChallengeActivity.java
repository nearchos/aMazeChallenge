package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.inspirecenter.amazechallenge.R;

public class OnlineChallengeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_challenge);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
