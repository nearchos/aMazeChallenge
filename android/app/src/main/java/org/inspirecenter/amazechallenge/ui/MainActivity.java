package org.inspirecenter.amazechallenge.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(org.inspirecenter.amazechallenge.R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(org.inspirecenter.amazechallenge.R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, org.inspirecenter.amazechallenge.R.string.navigation_drawer_open, org.inspirecenter.amazechallenge.R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(org.inspirecenter.amazechallenge.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        } else if (id == R.id.nav_help) {
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show(); // todo
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        DrawerLayout drawer = findViewById(org.inspirecenter.amazechallenge.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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