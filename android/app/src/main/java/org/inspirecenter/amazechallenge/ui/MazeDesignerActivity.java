package org.inspirecenter.amazechallenge.ui;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.generator.MazeGenerator;
import org.inspirecenter.amazechallenge.model.Algorithm;
import org.inspirecenter.amazechallenge.model.Audio;
import org.inspirecenter.amazechallenge.model.BackgroundImage;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.ChallengeDifficulty;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.PickableIntensity;
import org.inspirecenter.amazechallenge.model.Position;
import org.inspirecenter.amazechallenge.utils.FileManager;

import java.util.ArrayList;

public class MazeDesignerActivity extends AppCompatActivity {

    public static final String DESIGNER_MODE_KEY = "DESIGNER_MODE_KEY";
    public static final String DESIGNER_DATA_KEY = "DESIGNER_DATA_KEY";

    public enum DesignerMode {
        EDIT,
        CREATE
    }

    public static final String TAG = "amaze";
    private static final int MAX_ROWS = 30;
    private static final int MAX_COLUMNS = 30;
    private final BackgroundImage DEFAULT_BACKGROUND_IMAGE = BackgroundImage.TEXTURE_GRASS;

    DesignerMode mode = DesignerMode.CREATE;

    private GameView gameView;

    private Button selectColorButton;
    private Button selectImageButton;
    private Button addToTrainingButton;
    private Spinner maze_size_Spinner;
    private Spinner startPos_Row_Spinner;
    private Spinner startPos_Column_Spinner;
    private Spinner targetPos_Row_Spinner;
    private Spinner targetPos_Column_Spinner;
    private Spinner penaltiesSpinner;
    private Spinner rewardsSpinner;
    private Spinner backgroundAudioSpinner;
    private Spinner algorithmSpinner;
    private EditText mazeNameEditText;
    private EditText mazeDescriptionEditText;

    private int selectedWallColor = Color.BLACK;
    private BackgroundImage backgroundImage;
    private int size = 5; //Default setting
    private int startPos_row = 0;
    private int startPos_column = 0;
    private int targetPos_row = 0;
    private int targetPos_column = 0;
    private int penalties = size / 5; //Default low setting formula
    private int rewards = size / 5; //Default low setting formula
    private PickableIntensity rewardsIntensity = PickableIntensity.LOW;
    private PickableIntensity penaltiesIntensity = PickableIntensity.LOW;
    private Audio backgroundAudio;
    private Algorithm selectedAlgorithm = Algorithm.SINGLE_SOLUTION;
    private String oldMazeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maze_designer);

        backgroundImage = DEFAULT_BACKGROUND_IMAGE;

        //GameView:
        gameView = findViewById(R.id.activity_maze_designer_game_view);

        //EditTexts:
        mazeNameEditText = findViewById(R.id.designer_name_txt);
        mazeDescriptionEditText = findViewById(R.id.designer_description_txt);

        //Buttons:
        selectColorButton = findViewById(R.id.designer_wallcolor_button);
        selectImageButton = findViewById(R.id.designer_backgroundImage_button);
        addToTrainingButton = findViewById(R.id.addToTrainingButton);

        selectColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedWallColorFromPicker();
            }
        });

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
        rewardsSpinner = findViewById(R.id.designer_rewards_spinner);
        penaltiesSpinner = findViewById(R.id.designer_penalties_spinner);
        backgroundAudioSpinner = findViewById(R.id.designer_audio_spinner);
        algorithmSpinner = findViewById(R.id.designer_algorithm_spinner);

        //Maze Size Selection
        maze_size_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final int size = i + 5;
                setSize(size, new Position(0, size-1), new Position(size-1, 0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Start Row Selection
        startPos_Row_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setStartingPositionRow(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Start Column Selection
        startPos_Column_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setStartingPositionColumn(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Target Row Selection
        targetPos_Row_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTargetPositionRow(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Target Column Selection
        targetPos_Column_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTargetPositionColumn(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Rewards Selection
        rewardsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setRewards(PickableIntensity.getOptionFromID(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Penalties Selection
        penaltiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setPenalties(PickableIntensity.getOptionFromID(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Audio Spinner Options:
        final ArrayAdapter<String> audioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ArrayList<Audio> audioList = new ArrayList<>();
        for (Audio a : Audio.values()) {
            if (a.getAudioType() == Audio.AudioType.AMBIENT || a.getAudioType() == Audio.AudioType.NONE) {
                audioList.add(a);
                audioAdapter.add(a.toString());
            }
        }
        backgroundAudioSpinner.setAdapter(audioAdapter);

        //Audio Spinner Selection
        backgroundAudioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                backgroundAudio = audioList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Algorithm Spinner Options:
        final ArrayAdapter<String> algorithmAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ArrayList<Algorithm> algorithmList = new ArrayList<>();
        for (Algorithm algorithm : Algorithm.values()) {
            algorithmList.add(algorithm);
            algorithmAdapter.add(algorithm.toString());
        }
        algorithmSpinner.setAdapter(algorithmAdapter);

        //Algorithm Spinner Selection:
        algorithmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAlgorithm = algorithmList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //GET DATA FOR EXISTING MAZE:

        mode = (DesignerMode) getIntent().getSerializableExtra(DESIGNER_MODE_KEY);
        if (mode == DesignerMode.EDIT) {
            Challenge challenge = (Challenge) getIntent().getSerializableExtra(DESIGNER_DATA_KEY);
            selectedWallColor = Color.parseColor(challenge.getLineColor());
            backgroundImage = challenge.getBackgroundImage();
            backgroundAudio = challenge.getBackgroundAudio();
            size = challenge.getGrid().getHeight();
            startPos_row = challenge.getGrid().getStartingPosition().getRow();
            startPos_column = challenge.getGrid().getStartingPosition().getCol();
            targetPos_row = challenge.getGrid().getTargetPosition().getRow();
            targetPos_column = challenge.getGrid().getTargetPosition().getCol();
            rewards = challenge.getMaxRewards();
            rewardsIntensity = challenge.getRewardsIntensity();
            penalties = challenge.getMaxPenalties();
            penaltiesIntensity = challenge.getPenaltiesIntensity();
            selectedAlgorithm = challenge.getAlgorithm();
            oldMazeName = challenge.getName();

            mazeNameEditText.setText(oldMazeName);
            mazeDescriptionEditText.setText(challenge.getDescription());
            maze_size_Spinner.setSelection(challenge.getGrid().getHeight() - 5);
            setSize(challenge.getGrid().getHeight(), new Position(startPos_row, startPos_column), new Position(targetPos_row, targetPos_column));
            backgroundAudioSpinner.setSelection(Audio.getIdFromString(backgroundAudio.getSoundResourceName()));
            selectColorButton.setBackgroundColor(selectedWallColor);
            if (ColorFragment.isBrightColor(selectedWallColor)) selectColorButton.setTextColor(Color.BLACK);
            else selectColorButton.setTextColor(Color.WHITE);
            gameView.setBackgroundDrawable(backgroundImage);
            gameView.setLineColor(String.format("#%06X", (0xFFFFFF & selectedWallColor)));
            gameView.setGrid(challenge.getGrid());
            gameView.invalidate();

            //Disabled fields (non-changeable):
            algorithmSpinner.setEnabled(false);
            algorithmSpinner.setSelection(Algorithm.getPosition(selectedAlgorithm));
            penaltiesSpinner.setSelection(penaltiesIntensity.getID());
            rewardsSpinner.setSelection(rewardsIntensity.getID());

            //Changed fields:
            Button addToTrainingButton = findViewById(R.id.addToTrainingButton);
            Button generateButton = findViewById(R.id.generateButton);
            addToTrainingButton.setText(getString(R.string.Save));
            generateButton.setText(getString(R.string.Discard));
            addToTrainingButton.setBackgroundColor(getColor(R.color.materialBlue));
            addToTrainingButton.setEnabled(true);
            generateButton.setBackgroundColor(getColor(R.color.materialRed));

            addToTrainingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveMaze();
                    Toast.makeText(MazeDesignerActivity.this, getString(R.string.maze_saved), Toast.LENGTH_LONG).show();
                    finish();
                }
            });

            generateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });


        }


    }


    public void generate(final View view) {
        final Position startingPosition = new Position(startPos_row, startPos_column);
        final Position targetPosition = new Position(targetPos_row, targetPos_column);

        if (startingPosition.equals(targetPosition)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.invalid_positions_title));
            dialog.setMessage(getString(R.string.invalid_positions_message));
            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
        else if (mazeNameEditText.getText().toString().isEmpty()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.invalid_maze_name_title));
            dialog.setMessage(getString(R.string.invalid_maze_name_message));
            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
        else {
            final String data = MazeGenerator.generate(selectedAlgorithm, size, startingPosition, targetPosition);
            final Grid grid = new Grid(size, size, data, startingPosition, targetPosition);
            gameView.setBackgroundDrawable(backgroundImage);
            gameView.setGrid(grid);
            gameView.invalidate();
            addToTrainingButton.setEnabled(true);
        }
    }

    public void saveMaze() {
        String oldFileName = FileManager.SAVED_MAZES_FILENAME_PREFIX + oldMazeName + ".json";
        String newFileName = FileManager.SAVED_MAZES_FILENAME_PREFIX + mazeNameEditText.getText().toString() + ".json";
        if (FileManager.fileExists(this, oldFileName)) {
            FileManager.deleteFile(this, oldFileName);
            FileManager.writeToFile(this, newFileName, toJSON());
        }
    }

    public void addToTraining(View view) {
        String combinedFileName = FileManager.SAVED_MAZES_FILENAME_PREFIX + mazeNameEditText.getText().toString() + ".json";
        if (mazeNameEditText.getText().toString().isEmpty()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.invalid_maze_name_title));
            dialog.setMessage(getString(R.string.invalid_maze_name_message));
            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
        else if (FileManager.fileExists(this, combinedFileName)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.challenge_exists_title));
            dialog.setMessage(getString(R.string.challenge_exists_message));
            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
        else if (!mazeNameEditText.getText().toString().matches(FileManager.FILENAME_REGEX)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.challenge_name_title));
            dialog.setMessage(getString(R.string.challenge_name_message));
            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
        else {
            String json = toJSON();
            FileManager.writeToFile(this, combinedFileName, json);
            Toast.makeText(this, mazeNameEditText.getText().toString() + " " + getString(R.string.challenge_added_training), Toast.LENGTH_LONG).show();
            finish();
        }
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
        gameView.invalidate();
    }

    public int getWallColor() {
        return selectedWallColor;
    }

    private void getSelectedImageFromPicker() {
        buildImageSelectionDialog(this).show();
    }

    public void setBackgroundImage(BackgroundImage image) {
        this.backgroundImage = image;
        gameView.setBackgroundDrawable(getResources().getIdentifier(image.getResourceName(), "drawable", getPackageName()));
        gameView.invalidate();
    }

    public BackgroundImage getBackgroundImage() {
        return backgroundImage;
    }

    private void setSize(int size, Position startPosition, Position targetPosition) {
        this.size = size;
        ArrayList<String> validItems = new ArrayList<>();
        for (int i = 0; i < size; i++) validItems.add(String.valueOf(i));

        //Set range of possible values:
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, validItems);
        startPos_Row_Spinner.setAdapter(adapter);
        startPos_Column_Spinner.setAdapter(adapter);
        targetPos_Row_Spinner.setAdapter(adapter);
        targetPos_Column_Spinner.setAdapter(adapter);

        startPos_Row_Spinner.setSelection(startPosition.getRow());
        startPos_Column_Spinner.setSelection(startPosition.getCol());
        targetPos_Row_Spinner.setSelection(targetPosition.getRow());
        targetPos_Column_Spinner.setSelection(targetPosition.getCol());
    }

    private void setStartingPositionRow(int row) {
        startPos_row = row;
    }

    private void setStartingPositionColumn(int column) {
        startPos_column = column;
    }

    private void setTargetPositionRow(int row) {
        targetPos_row = row;
    }

    private void setTargetPositionColumn(int column) {
        targetPos_column = column;
    }

    private void setRewards(PickableIntensity option) {
        rewardsIntensity = option;
    }

    private void setPenalties(PickableIntensity option) {
        penaltiesIntensity = option;
    }

    private ChallengeDifficulty calculateDifficulty(PickableIntensity rewardsIntensity, PickableIntensity penaltiesIntensity) {
        if (rewardsIntensity == penaltiesIntensity) return ChallengeDifficulty.MEDIUM;
        else {
            if (rewardsIntensity == PickableIntensity.LOW && penaltiesIntensity == PickableIntensity.HIGH) return ChallengeDifficulty.VERY_HARD;
            else if (rewardsIntensity == PickableIntensity.HIGH && penaltiesIntensity == PickableIntensity.LOW) return ChallengeDifficulty.VERY_EASY;
            else if (rewardsIntensity == PickableIntensity.MEDIUM && penaltiesIntensity == PickableIntensity.LOW) return ChallengeDifficulty.EASY;
            else if (rewardsIntensity == PickableIntensity.LOW && penaltiesIntensity == PickableIntensity.MEDIUM) return ChallengeDifficulty.HARD;
            else if (rewardsIntensity == PickableIntensity.MEDIUM && penaltiesIntensity == PickableIntensity.HIGH) return ChallengeDifficulty.HARD;
            else /*if (rewardsIntensity == PickableIntensity.HIGH && penaltiesIntensity == PickableIntensity.MEDIUM)*/ return ChallengeDifficulty.EASY;
        }
    }

    private String toJSON() {
        final Position startingPosition = new Position(startPos_row, startPos_column);
        final Position targetPosition = new Position(targetPos_row, targetPos_column);
//        final String data = MazeGenerator.generate(size, startingPosition, targetPosition);
        // todo choose algorithm
        final String data = MazeGenerator.generate(Algorithm.MANY_SOLUTIONS, size, startingPosition, targetPosition);

        return  "{\n" +
                "    \"id\": 0,\n" +
                "    \"apiVersion\": 1,\n" +
                "    \"name\": \"" + mazeNameEditText.getText().toString() + "\",\n" +
                "    \"description\": \"" + mazeDescriptionEditText.getText().toString() + "\",\n" +
                "    \"difficulty\": \"" + calculateDifficulty(rewardsIntensity, penaltiesIntensity).toString() + "\",\n" +
                "    \"createdOn\":" + System.currentTimeMillis() + ",\n" +
                "    \"createdBy\": \"player\"," +
                "    \"canRepeat\": true,\n" +
                "    \"canJoinAfterStart\": true,\n" +      //Default for training
                "    \"canStepOnEachOther\": true,\n" +     //Default for training
                "    \"minActivePlayers\": 1,\n" +          //Default for training
                "    \"maxActivePlayers\": 10,\n" +         //Default for training
                "    \"startTimestamp\": 0,\n" +            //Default for training
                "    \"endTimestamp\": 0,\n" +              //Default for training
                "    \"hasQuestionnaire\": true,\n" +       //Default for training
                "    \"rewards\": " + rewardsIntensity.getTextID() + ",\n" +
                "    \"penalties\": " + penaltiesIntensity.getTextID() + ",\n" +
                "    \"selectedAlgorithm\": \"" + selectedAlgorithm + "\",\n" +
                "    \"grid\": {\n" +
                "        \"width\": " + size + ",\n" +
                "        \"height\": " + size + ",\n" +
                "        \"data\": \"" + data + "\",\n" +
                "        \"startingPosition\": {\n" +
                "            \"row\": " + startPos_row + ",\n" +
                "            \"col\": " + startPos_column + "\n" +
                "        },\n" +
                "        \"targetPosition\": {\n" +
                "            \"row\": " + targetPos_row + ",\n" +
                "            \"col\": " + targetPos_column + "\n" +
                "        }\n" +
                "    },\n" +
                "    \"lineColor\": \"#" + Integer.toHexString(selectedWallColor) + "\",\n" +
                "    \"backgroundImageName\": \"" + backgroundImage.getResourceName() + "\",\n" +
                "    \"backgroundImageType\": \"" + backgroundImage.getType().toString() + "\",\n" +
                "    \"backgroundAudioName\": \"" + backgroundAudio.getSoundResourceName() + "\",\n" +
                "    \"backgroundAudioFormat\": \"" + backgroundAudio.getAudioFormat() + "\"" +
                "}";
    }

    private AlertDialog buildImageSelectionDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.ic_menu_gallery);
        builder.setTitle(R.string.select_image);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item);
        for (BackgroundImage i : BackgroundImage.values()) adapter.add(i.getName());

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setBackgroundImage(BackgroundImage.values()[i]);
            }
        });

        return builder.create();
    }
}