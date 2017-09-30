package org.inspirecenter.amazechallenge.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.inspirecenter.amazechallenge.algorithms.LeftWallFollowerMazeSolver;
import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.algorithms.RandomWalkMazeSolver;
import org.inspirecenter.amazechallenge.algorithms.RightWallFollowerMazeSolver;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.ShapeColor;
import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.algorithms.RandomWallFollowerMazeSolver;

import static org.inspirecenter.amazechallenge.model.Shape.TRIANGLE;

public class AddPlayerActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Spinner algorithmSpinner;
    private Spinner colorSpinner;

    static final Class<MazeSolver> [] mazeSolvers = new Class [] {
            LeftWallFollowerMazeSolver.class,
            RightWallFollowerMazeSolver.class,
            RandomWallFollowerMazeSolver.class,
            RandomWalkMazeSolver.class,
            InterpretedMazeSolver.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.inspirecenter.amazechallenge.R.layout.activity_add_player);

        final Intent intent = getIntent();
        String nextName = "";
        if(intent != null && intent.hasExtra("nextName")) {
            nextName = intent.getStringExtra("nextName");
        }
        nameEditText = findViewById(org.inspirecenter.amazechallenge.R.id.activity_add_player_name_edit_text);
        nameEditText.setText(nextName);

        algorithmSpinner = findViewById(org.inspirecenter.amazechallenge.R.id.activity_add_player_algorithm_spinner);
        final String [] mazeSolverNames = new String[mazeSolvers.length];
        for(int i = 0; i < mazeSolvers.length; i++) {
            mazeSolverNames[i] = mazeSolvers[i].getSimpleName();
        }
        final ArrayAdapter<String> mazeSolverArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mazeSolverNames);
        algorithmSpinner.setAdapter(mazeSolverArrayAdapter);

        colorSpinner = findViewById(org.inspirecenter.amazechallenge.R.id.activity_add_player_color_spinner);
        final ColorSpinnerAdapter colorSpinnerAdapter = new ColorSpinnerAdapter(this, org.inspirecenter.amazechallenge.R.layout.item_player_color);
        colorSpinnerAdapter.setDropDownViewResource(org.inspirecenter.amazechallenge.R.layout.item_player_color);
        colorSpinner.setAdapter(colorSpinnerAdapter);

        findViewById(org.inspirecenter.amazechallenge.R.id.activity_add_player_save_button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final String name = nameEditText.getText().toString();
                Log.d("mazes", "name: " + name);
                final ShapeColor color = ((ShapeColor) colorSpinner.getSelectedItem());
                final Class<MazeSolver> algorithm = mazeSolvers[algorithmSpinner.getSelectedItemPosition()];
                // return player
                final Intent returnIntent = new Intent();
                returnIntent.putExtra("player", new Player(name, color, TRIANGLE, algorithm));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        findViewById(org.inspirecenter.amazechallenge.R.id.activity_add_player_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // cancel
                final Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }
}
