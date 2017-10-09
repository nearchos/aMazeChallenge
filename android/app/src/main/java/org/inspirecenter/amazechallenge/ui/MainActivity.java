package org.inspirecenter.amazechallenge.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Maze;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String GRIDS_PATH = "grids";

    private RecyclerView playersRecyclerView;

    private PlayerAdapter playerAdapter;

    private Maze selectedMaze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(org.inspirecenter.amazechallenge.R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(org.inspirecenter.amazechallenge.R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, org.inspirecenter.amazechallenge.R.string.navigation_drawer_open, org.inspirecenter.amazechallenge.R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(org.inspirecenter.amazechallenge.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Spinner mazesSpinner = findViewById(org.inspirecenter.amazechallenge.R.id.activity_main_mazes_spinner);
        playersRecyclerView = findViewById(org.inspirecenter.amazechallenge.R.id.activity_main_players_list_view);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playersRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        playerAdapter = new PlayerAdapter();
        playersRecyclerView.setAdapter(playerAdapter);

        final Button addPlayerButton = findViewById(org.inspirecenter.amazechallenge.R.id.activity_main_add_player_button);
        final Button startGameButton= findViewById(org.inspirecenter.amazechallenge.R.id.activity_main_start_game_button);

        try {
            final String [] allAssets = getAssets().list(GRIDS_PATH);
            final Vector<Maze> mazes = new Vector<>();
            for(final String asset : allAssets) {
                final InputStream inputStream = getAssets().open(GRIDS_PATH + "/" + asset);
                mazes.add(new Maze(inputStream));
                inputStream.close();
            }
            mazesSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mazes));
            mazesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedMaze = mazes.get(position);
                }

                @Override public void onNothingSelected(AdapterView<?> parent) { /* nothing */ }
            });
        } catch (IOException ioe) {
            Toast.makeText(this, "Error: " + ioe.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("mazes", "Error: " + ioe.getMessage());
            finish();
        }

        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, AddPlayerActivity.class);
                intent.putExtra("nextName", findNextName());
                startActivityForResult(intent, 1);
            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if(playerAdapter.isEmpty()) {
                    Snackbar.make(view, "You must add at least a player before starting the game", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                    return;
                }
                final Game game = new Game(selectedMaze);
                final Vector<Player> players = playerAdapter.getPlayers();
                for(final Player player : players) {
                    if(player.getMazeSolverClass().equals(InterpretedMazeSolver.class)) { // todo generalize
                        final Map<String,Serializable> parametersMap = new HashMap<>();
                        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        final String code = sharedPreferences.getString(BlocklyActivity.KEY_ALGORITHM_ACTIVITY_CODE, "");
                        parametersMap.put(InterpretedMazeSolver.PARAMETER_KEY_CODE, code);
                        game.addPlayer(player, parametersMap);
                        continue;
                    }
                    game.addPlayer(player);
                }
                final Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra(GameActivity.SELECTED_GAME_KEY, game);
                startActivity(intent);
            }
        });
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == org.inspirecenter.amazechallenge.R.id.nav_game) {
            // Handle the camera action
        } else if (id == org.inspirecenter.amazechallenge.R.id.nav_players) {

        } else if (id == org.inspirecenter.amazechallenge.R.id.nav_code_designer) {
            //startActivity(new Intent(this, AlgorithmActivity.class));
            startActivity(new Intent(this, BlocklyActivity.class));
        } else if (id == org.inspirecenter.amazechallenge.R.id.nav_info) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        DrawerLayout drawer = findViewById(org.inspirecenter.amazechallenge.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                final Player player = (Player) data.getSerializableExtra("player");
                playerAdapter.add(player);
                playerAdapter.notifyItemInserted(playerAdapter.getSize() - 1);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
                Toast.makeText(this, org.inspirecenter.amazechallenge.R.string.Cancelled, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String findNextName() {
        int i = 0;
        while(true) {
            boolean found = true;
            final String nextName = "p" + i++;
            final Vector<Player> players = playerAdapter.getPlayers();
            for(final Player player : players) {
                if(nextName.equals(player.getName())) {
                    found = false;
                    break;
                }
            }
            if(found) return nextName;
        }
    }
}