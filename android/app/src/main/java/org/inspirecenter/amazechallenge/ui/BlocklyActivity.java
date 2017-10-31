/*
 *  Copyright 2016 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.model.DefaultBlocks;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.LoadDialogListAdapter;
import org.inspirecenter.amazechallenge.model.Maze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlocklyActivity extends AbstractBlocklyActivity {

    private static final String TAG = "MazeBlocklyActivity";
    private static final String DEFAULT_SAVE_FILENAME = "maze_workspace.xml";
    private static String SAVE_FILENAME = DEFAULT_SAVE_FILENAME;
    private static final String AUTOSAVE_FILENAME = "maze_workspace_temp.xml";
    public static final String KEY_ALGORITHM_ACTIVITY_CODE = "KEY_ALGORITHM_ACTIVITY_CODE";
    public static final String ASSETS_CODES_DIR = "defaultCodes";
    public static ArrayList<String> codeNamesList = new ArrayList<>();
    public static ArrayList<String> codeFilesList = new ArrayList<>();
    public static AlertDialog loadDialog;

    private static final List<String> BLOCK_DEFINITIONS = Arrays.asList(
            DefaultBlocks.COLOR_BLOCKS_PATH,
            DefaultBlocks.LOGIC_BLOCKS_PATH,
            DefaultBlocks.LOOP_BLOCKS_PATH,
            DefaultBlocks.MATH_BLOCKS_PATH,
            DefaultBlocks.TEXT_BLOCKS_PATH,
            DefaultBlocks.VARIABLE_BLOCKS_PATH,
            "blocks/maze_blocks.json"
    );//end List BLOCK_DEFINITIONS

    private static final List<String> JAVASCRIPT_GENERATORS = Collections.singletonList(
            "generators/maze_blocks.js"
    );//end List JAVASCRIPT_GENERATORS

    CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(final String generatedCode) {
                    Log.i(TAG, "generatedCode:\n" + generatedCode);
                    Toast.makeText(getApplicationContext(), R.string.Your_code_has_been_saved, Toast.LENGTH_LONG).show();
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BlocklyActivity.this);
                    sharedPreferences.edit().putString (KEY_ALGORITHM_ACTIVITY_CODE, generatedCode).apply();
                    finish();
                }
            };//end mCodeGeneratorCallback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }//end onCreate()

    @Override
    protected View onCreateContentView(int containerId) {
        return getLayoutInflater().inflate(R.layout.activity_blockly, null);
    }//end onCreateContentView()

    private void submitCode() {
        final Intent intent = getIntent();
        final Maze selectedMaze = (Maze) intent.getSerializableExtra(GameActivity.SELECTED_GAME_KEY);
        final Intent intentPlay = new Intent(BlocklyActivity.this, GameActivity.class);
        intentPlay.putExtra(GameActivity.SELECTED_GAME_KEY, selectedMaze);

        Snackbar.make(findViewById(R.id.blocklyView), R.string.Compiling, Snackbar.LENGTH_LONG).setAction("Action", null).show(); // todo

        if (getController().getWorkspace().hasBlocks()) onRunCode();
    }//end submitCode()

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        return BLOCK_DEFINITIONS;
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return "toolboxes/maze_toolbox.xml";
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return JAVASCRIPT_GENERATORS;
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    @Override
    @NonNull
    protected String getWorkspaceSavePath() {
        return SAVE_FILENAME;
    }

    @Override
    @NonNull
    protected String getWorkspaceAutosavePath() {
        return AUTOSAVE_FILENAME;
    }

    @Override
    protected int getActionBarMenuResId() {
        return R.menu.blockly_action_bar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                submitCode();
                return true;

            case R.id.action_save:
                final EditText input = new EditText(BlocklyActivity.this);
                final AlertDialog SAVE_DIALOG = createSaveDialog(input);
                SAVE_DIALOG.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = SAVE_DIALOG.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (internalFileExists(input.getText().toString())) {
                                    AlertDialog.Builder confirmOverwriteDialog = new AlertDialog.Builder(BlocklyActivity.this);
                                    confirmOverwriteDialog.setTitle(R.string.code_overwrite)
                                            .setMessage(R.string.code_overwrite_message)
                                            .setPositiveButton(R.string.code_overwrite_yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String savedFileName = "playercode_" + input.getText().toString() + ".xml";
                                                    setSaveFilename(savedFileName);
                                                    onSaveWorkspace();
                                                    dialogInterface.dismiss();
                                                    SAVE_DIALOG.dismiss();
                                                }
                                            })
                                            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).create().show();
                                }
                                else if (input.getText().toString().isEmpty())
                                    Snackbar.make(view, R.string.save_code_error_empty, Snackbar.LENGTH_SHORT).show();
                                else if (!input.getText().toString().matches("[a-zA-Z0-9]*"))
                                    Snackbar.make(view, R.string.save_code_error_invalid, Snackbar.LENGTH_SHORT).show();
                                else {
                                    String savedFileName = "playercode_" + input.getText().toString() + ".xml";
                                    setSaveFilename(savedFileName);
                                    onSaveWorkspace();
                                    SAVE_DIALOG.dismiss();
                                }//end else
                            }//end onClick()
                        });//end clicklistener
                    }//end onShow()
                });//end onShowListener
                SAVE_DIALOG.show();
                return true;


            case R.id.action_load:
                updateInternalStorageCodes();
                getCodes();
                final ListView list = new ListView(BlocklyActivity.this);
                loadDialog = createLoadDialog(list);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        setSaveFilename(codeFilesList.get(i));
                        onLoadWorkspace();
                        loadDialog.dismiss();
                    }
                });
                loadDialog.show();
                return true;


            case com.google.blockly.android.R.id.action_clear:
                // show confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.Clear_code)
                        .setMessage(R.string.Are_you_sure_you_want_to_erase_the_code)
                        .setPositiveButton(R.string.Clear, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                                onClearWorkspace();
                            }
                        })
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).create().show();
                return true;
        }//end switch
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected()

    @Override
    public void onBackPressed() {
        submitCode();
    }

    public static void setSaveFilename(String saveFilename) {
        SAVE_FILENAME = saveFilename;
    }

    /**
     * Searches for files within the internal storage (/data/user/0/[app-domain]/files/)
     * Populates the codeNames and codeFiles lists firstly with demo codes and then with user codes.
     * codeFilesList stores the raw filename e.g playercode_mySavedCode.xml
     * codeNamesList stores the filtered filename for display in the list e.g mySavedCode
     */
    private void getCodes() {
        //Clear the arrays first:
        codeNamesList.clear();
        codeFilesList.clear();

        //Get all files from the internal files directory:
        File mydir = this.getFilesDir();
        File listFile[] = mydir.listFiles();
        if (listFile != null && listFile.length > 0) {

            //Default codes:
            for (File aListFile : listFile) {
                if (aListFile.isFile() && aListFile.getName().startsWith("defaultcode_")) {
                    codeFilesList.add(aListFile.getName());
                    String filteredName = aListFile.getName().replace("defaultcode_", "");
                    filteredName = filteredName.replace(".xml", "");
                    codeNamesList.add(filteredName + " (DEMO)");
                }//end if
            }//end for (default codes)

            //Player codes:
            for (File aListFile : listFile) {
                if (aListFile.isFile() && aListFile.getName().startsWith("playercode_")) {
                    codeFilesList.add(aListFile.getName());
                    String filteredName = aListFile.getName().replace("playercode_", "");
                    filteredName = filteredName.replace(".xml", "");
                    codeNamesList.add(filteredName);
                }//end if
            }//end for (player codes)

        }//end if
    }//end getCodes()

    /**
     * Matches asset code files to internal storage code files.
     * If an internal file is missing, the asset code file is copied into the internal folder.
     * This is necessary as Blockly only loads XML files from internal app storage.
     */
    private void updateInternalStorageCodes() {

        //Get the asset files:
        ArrayList<String> assetFiles = new ArrayList<>();
        try { assetFiles = listAssetCodes(); }
        catch (IOException e) { e.printStackTrace(); }

        //Get the internal files:
        ArrayList<String> internalFilesList = new ArrayList<>();
        File mydir = this.getFilesDir();
        File listFile[] = mydir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File aListFile : listFile)
                if (aListFile.isFile() && aListFile.getName().startsWith("defaultcode_")) internalFilesList.add(aListFile.getName());
        }//end if

        //Compare files in assets with internal files:
        for (String filename : assetFiles) {
            boolean found = false;
            for (String internalFilename : internalFilesList) {
                if (internalFilename.equals(filename)) {
                    found = true;
                    break;
                }//end if
            }//end foreach internal filename

            //If file in assets does not exist in internals, read its content
            // and create a new file in internals with the same name:
            if (!found) {
                //Read the contents of the file in assets:
                String content = "";
                try { content = readFromAssets(this, ASSETS_CODES_DIR + "/" + filename); }
                catch (IOException e) { e.printStackTrace(); }
                //Create a new file in internals and copy content and name of the assets file:
                if (!content.isEmpty()) {
                    File file = new File(this.getFilesDir(), filename);
                    FileOutputStream outputStream;
                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(content.getBytes());
                        outputStream.close();
                    } catch (Exception e) { e.printStackTrace(); }
                }//end if content non empty
            }//end if file was not found
        }//end foreach file in assets
    }//end updateInternalStorageCodes()

    /**
     * Finds and returns a list of all the asset (default) codes.
     * @return Returns an ArrayList of Strings of the asset code files.
     * @throws IOException Throws exception if a file cannot be accessed.
     */
    private ArrayList<String> listAssetCodes() throws IOException {
        ArrayList<String> assetsFilenames = new ArrayList<>();
        Resources res = getResources();
        AssetManager am = res.getAssets();
        String fileList[] = am.list(ASSETS_CODES_DIR);
        if (fileList != null) {
            for (String file : fileList)
                if (file.startsWith("defaultcode_")) assetsFilenames.add(file);
        }//end if
        return assetsFilenames;
    }//end listAssets()

    /**
     * Reads a text file from assets.
     * @param context Current Context
     * @param filename The filename to read.
     * @return Returns a string of the file's contents.
     * @throws IOException Throws exception if the file cannot be accessed.
     */
    public static String readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine); // process line
            mLine = reader.readLine();
        }//end while
        reader.close();
        return sb.toString();
    }//end readFromAssets()

    /**
     * Delete a given internal file.
     * @param filename Internal file to delete.
     */
    public void deleteInternalFile(String filename) {
        File dir = getFilesDir();
        File file = new File(dir, filename);
        file.delete();
        updateInternalStorageCodes();
        getCodes();
    }//end deleteInternalFile()

    /**
     * Checks if an internal file with the given name exists.
     * @param filename The name of the file.
     * @return Returns true if file exists, false if not.
     */
    public boolean internalFileExists(String filename) {
        filename = "playercode_" + filename + ".xml";
        File file = getBaseContext().getFileStreamPath(filename);
        return file.exists();
    }//end internalFileExists()

    private AlertDialog createSaveDialog(EditText input) {
        final AlertDialog.Builder saveDialogBuilder = new AlertDialog.Builder(this);
        //Input EditText for name of the saved code:
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        saveDialogBuilder.setView(input);
        //Title, message and responses:
        saveDialogBuilder.setTitle(R.string.Save_code)
                .setMessage(R.string.Save_code_name)
                .setPositiveButton(R.string.Save, null)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        return saveDialogBuilder.create();
    }

    private AlertDialog createLoadDialog(ListView list) {
        final AlertDialog.Builder loadDialog = new AlertDialog.Builder(this);
        LoadDialogListAdapter listAdapter = new LoadDialogListAdapter(this, codeNamesList, codeFilesList, this);
        list.setAdapter(listAdapter);

        LinearLayout.LayoutParams lp_list = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        list.setLayoutParams(lp_list);
        loadDialog.setView(list);
        loadDialog.setTitle(R.string.load_code);
        loadDialog.setMessage(R.string.load_code_message);
        return loadDialog.create();
    }//end createLoadDialog()

}//end activity BlocklyActivity
