package org.inspirecenter.amazechallenge.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.generator.MazeGenerator;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.Position;

import java.util.ArrayList;

public class MazeDesignerActivity extends AppCompatActivity {

    public static final String TAG = "amaze";
    private static final int MAX_ROWS = 30;
    private static final int MAX_COLUMNS = 30;

    private GameView gameView;

    private Button selectColorButton;
    private Button selectImageButton;
    private Spinner maze_size_Spinner;
    private Spinner startPos_Row_Spinner;
    private Spinner startPos_Column_Spinner;
    private Spinner targetPos_Row_Spinner;
    private Spinner targetPos_Column_Spinner;

    private int selectedWallColor = 0;
    private String selectedImageResourceName = "";
    private int size = 5;
    private int startPos_row = 0;
    private int startPos_column = 0;
    private int targetPos_row = 0;
    private int targetPos_column = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maze_designer);

        gameView = findViewById(R.id.activity_maze_designer_game_view);

        //Buttons:
        selectColorButton = findViewById(R.id.designer_wallcolor_button);
        selectColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedWallColorFromPicker();
            }
        });

        selectImageButton = findViewById(R.id.designer_backgroundImage_button);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedImageFromPicker();
            }
        });

        //Spinners:
        maze_size_Spinner = findViewById(R.id.designer_size_spinner);
        startPos_Row_Spinner = findViewById(R.id.designer_row_start_spinner);
        startPos_Column_Spinner = findViewById(R.id.designer_column_start_spinner);
        targetPos_Row_Spinner = findViewById(R.id.designer_row_target_spinner);
        targetPos_Column_Spinner = findViewById(R.id.designer_column_target_spinner);

        maze_size_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setSize(i + 5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





    }

//    public void generate(final View view) {
//        final int gridSize = 10;
//        final Position startingPosition = new Position(0, gridSize - 1);
//        final Position targetPosition = new Position(gridSize - 1, 0);
//        final String data = MazeGenerator.generate(gridSize, startingPosition, targetPosition);
//        final Grid grid = new Grid(gridSize, gridSize, data, startingPosition, targetPosition);
//        gameView.setBackgroundDrawable(MazeBackground.BACKGROUND_GRASS.getResourceID());
//        gameView.setGrid(grid);
//        gameView.invalidate();
//    }


    public void generate(final View view) {
        //TODO FIX
        final Position startingPosition = new Position(startPos_row, startPos_column);
        final Position targetPosition = new Position(targetPos_row, targetPos_column);
        final String data = MazeGenerator.generate(size, startingPosition, targetPosition);
        final Grid grid = new Grid(size, size, data, startingPosition, targetPosition);
        gameView.setBackgroundDrawable(MazeBackground.BACKGROUND_GRASS.getResourceID());
        //TODO Implement background selection
        gameView.setGrid(grid);
        gameView.invalidate();
    }

    private void getSelectedWallColorFromPicker() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle(getString(R.string.color_select_title))
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //Toast.makeText(MazeDesignerActivity.this, "Selected " + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton(getString(R.string.ok), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        setWallColor(selectedColor);
                    }
                })
                .setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void setWallColor(int color) {
        selectedWallColor = color;
        selectColorButton.setBackgroundColor(color);
        if (ColorFragment.isBrightColor(color)) selectColorButton.setTextColor(Color.BLACK);
        else selectColorButton.setTextColor(Color.WHITE);
        gameView.setLineColor("#"+Integer.toHexString(color));
    }

    public int getWallColor() {
        return selectedWallColor;
    }

    private void getSelectedImageFromPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_image)
                .setMessage("TODO");
        builder.create().show();
        //TODO Implement image selection from assets (repeatable textures only)
    }

    private void setImageResourceName(String imageResourceName) {
        selectedImageResourceName = imageResourceName;
    }

    public String getImageResourceName() {
        return selectedImageResourceName;
    }

    private void setSize(int size) {
        this.size = size;
        ArrayList<String> validItems = new ArrayList<>();
        for (int i = 0; i <= size; i++) validItems.add(String.valueOf(i));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, validItems);
        startPos_Row_Spinner.setAdapter(adapter);
        startPos_Column_Spinner.setAdapter(adapter);
        targetPos_Row_Spinner.setAdapter(adapter);
        targetPos_Column_Spinner.setAdapter(adapter);
    }


}
