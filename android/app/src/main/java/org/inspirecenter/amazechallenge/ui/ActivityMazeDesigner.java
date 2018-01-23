package org.inspirecenter.amazechallenge.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.generator.MazeGenerator;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.Position;

public class ActivityMazeDesigner extends AppCompatActivity {

    public static final String TAG = "amaze";

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maze_designer);

        this.gameView = findViewById(R.id.activity_maze_designer_game_view);
    }

    public void generate(final View view) {
        final int gridSize = 10;
        final Position startingPosition = new Position(0, gridSize - 1);
        final Position targetPosition = new Position(gridSize - 1, 0);
        final String data = MazeGenerator.generate(gridSize, startingPosition, targetPosition);
        final Grid grid = new Grid(gridSize, gridSize, data, startingPosition, targetPosition);
        gameView.setBackgroundDrawable(MazeBackground.BACKGROUND_GRASS.getResourceID());
        gameView.setGrid(grid);
        gameView.invalidate();
    }
}
