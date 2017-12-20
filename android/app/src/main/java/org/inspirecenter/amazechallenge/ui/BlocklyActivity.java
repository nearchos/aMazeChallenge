package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.model.BlocklySerializerException;
import com.google.blockly.model.DefaultBlocks;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.algorithms.InterpreterError;
import org.inspirecenter.amazechallenge.filemanager.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.inspirecenter.amazechallenge.algorithms.InterpreterError.InterpreterErrorType.ERROR;
import static org.inspirecenter.amazechallenge.algorithms.InterpreterError.InterpreterErrorType.WARNING;

public class BlocklyActivity extends AbstractBlocklyActivity {

    private static final String TAG = "MazeBlocklyActivity";
    private static final String DEFAULT_SAVE_FILENAME = "maze_workspace.xml";
    public static String SAVE_FILENAME = DEFAULT_SAVE_FILENAME;
    private static final String AUTOSAVE_FILENAME = "maze_workspace_temp.xml";
    public static final String KEY_ALGORITHM_ACTIVITY_CODE = "KEY_ALGORITHM_ACTIVITY_CODE";
    public static final String ASSETS_CODES_DIR = "defaultCodes";
    public static ArrayList<String> codeNamesList = new ArrayList<>();
    public static ArrayList<String> codeFilesList = new ArrayList<>();
    public static ArrayList<String> codeFilesLastModifiedList = new ArrayList<>();
    public static AlertDialog loadDialog;
    public static int snackbarDuration_MS = 5000;
    private final BlocklyActivity instance = this;

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
                    if(generatedCode != null && !generatedCode.isEmpty()) sharedPreferences.edit().putBoolean(MainActivity.KEY_PREF_EDITED_CODE, true).apply();
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

    public void done(final View view) {
        submitCode();
    }

    private void submitCode() {
        //Save the code first, but don't display message:
        try { mBlocklyActivityHelper.saveWorkspaceToAppDir(AUTOSAVE_FILENAME); }
        catch (FileNotFoundException | BlocklySerializerException e) { e.printStackTrace(); }

        //Check the code:
        ArrayList<InterpreterError> errorList = checkCode();

        int warnings = 0;
        int errors = 0;
        for (final InterpreterError e : errorList) {
            if (e.type == WARNING) warnings++;
            else if (e.type == ERROR) errors++;
        }//end for

        if (errorList.isEmpty()) {
            if (getController().getWorkspace().hasBlocks()) {
                Snackbar compilingSnackbar = Snackbar.make(findViewById(R.id.blocklyView), R.string.Compiling, Snackbar.LENGTH_LONG);
                View sbCView = compilingSnackbar.getView(); sbCView.setBackgroundColor(getColor(R.color.snackbarGreen));
                compilingSnackbar.show();
                onRunCode();
            }//end if has blocks and no error occured
            else {
                Intent intentBack = new Intent(BlocklyActivity.this, MainActivity.class);
                startActivity(intentBack);
            }//end if no blocks and no error
        }//end if no errors
        else {
            if (errors > 1) {
                AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this, R.style.ErrorDialogStyle);
                errorBuilder.setTitle(R.string.errors);
                errorBuilder.setMessage(getString(R.string.multiple_errors_found));
                final AlertDialog errorDialog = errorBuilder.create();
                final AlertDialog errorListDialog = createErrorListDialog(errorList);
                errorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.view), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        errorListDialog.show();
                        dialogInterface.dismiss();
                    }
                });
                errorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.back_to_menu), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intentBack = new Intent(BlocklyActivity.this, MainActivity.class);
                        startActivity(intentBack);
                    }
                });
                errorDialog.show();
            }//end if multiple errors occurred
            else if (errors > 0) {
                AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this, R.style.ErrorDialogStyle);
                errorBuilder.setTitle(getString(R.string.error));
                errorBuilder.setNegativeButton(R.string.back_to_menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intentBack = new Intent(BlocklyActivity.this, MainActivity.class);
                        startActivity(intentBack);
                    }
                });
                errorBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                InterpreterError e = getNextError(errorList);
                if (e == null) throw new RuntimeException("FATAL ERROR IN BLOCKLYACTIVITY: Could not find error in a presumably error-containing arraylist");
                else errorBuilder.setMessage(e.toString());
                errorBuilder.create().show();
            }//end if 1 error occurred
            else if (warnings > 1) {
                AlertDialog.Builder warningBuilder = new AlertDialog.Builder(this, R.style.WarningDialogStyle);
                warningBuilder.setTitle(R.string.warnings);
                warningBuilder.setMessage(getString(R.string.multiple_warnings_found));
                final AlertDialog warningDialog = warningBuilder.create();
                final AlertDialog errorListDialog = createWarningListDialog(errorList);
                warningDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.back_to_menu), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intentBack = new Intent(BlocklyActivity.this, MainActivity.class);
                        startActivity(intentBack);
                    }
                });
                warningDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ignore_all), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar compilingSnackbar = Snackbar.make(findViewById(R.id.blocklyView), R.string.Compiling, Snackbar.LENGTH_LONG);
                        View sbCView = compilingSnackbar.getView(); sbCView.setBackgroundColor(getColor(R.color.snackbarGreen));
                        compilingSnackbar.show();
                        onRunCode();
                    }
                });
                warningDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.view), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        errorListDialog.show();
                    }
                });
                warningDialog.show();
            }//end if multiple warnings occurred
            else if (warnings > 0) {
                AlertDialog.Builder warningBuilder = new AlertDialog.Builder(this, R.style.WarningDialogStyle);
                warningBuilder.setTitle(getString(R.string.warning));
                warningBuilder.setNeutralButton(R.string.back_to_menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intentBack = new Intent(BlocklyActivity.this, MainActivity.class);
                        startActivity(intentBack);
                    }
                });
                warningBuilder.setNegativeButton(R.string.ignore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar compilingSnackbar = Snackbar.make(findViewById(R.id.blocklyView), R.string.Compiling, Snackbar.LENGTH_LONG);
                        View sbCView = compilingSnackbar.getView(); sbCView.setBackgroundColor(getColor(R.color.snackbarGreen));
                        compilingSnackbar.show();
                        onRunCode();
                    }
                });
                warningBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                InterpreterError e = getNextWarning(errorList);
                if (e == null) throw new RuntimeException("FATAL ERROR IN BLOCKLYACTIVITY: Could not find warning in a presumably warning-containing arraylist");
                else warningBuilder.setMessage(e.toString());
                warningBuilder.create().show();
            }//end if 1 warning occurred
        }//end if errors
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
                                if (FileManager.internalFileExists(instance, input.getText().toString())) {
                                    AlertDialog.Builder confirmOverwriteDialog = new AlertDialog.Builder(BlocklyActivity.this);
                                    confirmOverwriteDialog.setTitle(R.string.code_overwrite)
                                            .setMessage(R.string.code_overwrite_message)
                                            .setPositiveButton(R.string.code_overwrite_yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String savedFileName = "playercode_" + input.getText().toString() + ".xml";
                                                    FileManager.setSaveFilename(savedFileName);
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
                                    FileManager.setSaveFilename(savedFileName);
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
                FileManager.updateInternalStorageCodes(instance);
                FileManager.getCodes(instance);
                final ListView list = new ListView(BlocklyActivity.this);
                loadDialog = createLoadDialog(list);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        FileManager.setSaveFilename(codeFilesList.get(i));
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

    /*************** DIALOG INTERFACES ***************/

    /**
     * Creates the Save Dialog.
     * @param input The edit text object (code name).
     * @return Returns an AlertDialog object.
     */
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
    }//end createSaveDialog()

    /**
     * Creates the Load Dialog.
     * @param list The list of items to choose from to load.
     * @return Returns an AlertDialog object.
     */
    private AlertDialog createLoadDialog(ListView list) {
        final AlertDialog.Builder loadDialog = new AlertDialog.Builder(this);
        LoadDialogListAdapter listAdapter = new LoadDialogListAdapter(this, codeNamesList, codeFilesList, codeFilesLastModifiedList,  this);
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

    /**
     * Creates the error list dialog.
     * @return Returns an AlertDialog object.
     */
    private AlertDialog createErrorListDialog(ArrayList<InterpreterError> errorList) {
        final ListView list = new ListView(BlocklyActivity.this);
        final AlertDialog.Builder errorListDialog = new AlertDialog.Builder(this);
        ErrorListDialogListAdapter listAdapter = new ErrorListDialogListAdapter(this, errorList, this);
        list.setAdapter(listAdapter);

        LinearLayout.LayoutParams lp_list = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        list.setLayoutParams(lp_list);
        errorListDialog.setView(list);
        errorListDialog.setTitle(R.string.errors);
        errorListDialog.setMessage(getString(R.string.errors_list_message));
        errorListDialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return errorListDialog.create();
    }//end createErrorListDialog()

    /**
     * Creates the warning list dialog.
     * @return Returns an AlertDialog object.
     */
    private AlertDialog createWarningListDialog(ArrayList<InterpreterError> errorList) {
        final ListView list = new ListView(BlocklyActivity.this);
        final AlertDialog.Builder warningListDialog = new AlertDialog.Builder(this);
        ErrorListDialogListAdapter listAdapter = new ErrorListDialogListAdapter(this, errorList, this);
        list.setAdapter(listAdapter);

        LinearLayout.LayoutParams lp_list = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        list.setLayoutParams(lp_list);
        warningListDialog.setView(list);
        warningListDialog.setTitle(R.string.warnings);
        warningListDialog.setMessage(getString(R.string.warnings_list_message));
        warningListDialog.setNegativeButton(R.string.ignore_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar compilingSnackbar = Snackbar.make(findViewById(R.id.blocklyView), R.string.Compiling, Snackbar.LENGTH_LONG);
                View sbCView = compilingSnackbar.getView(); sbCView.setBackgroundColor(getColor(R.color.snackbarGreen));
                compilingSnackbar.show();
                onRunCode();
            }
        });
        warningListDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return warningListDialog.create();
    }//end createErrorListDialog()

    /*************** ERROR HANDLING METHODS ***************/

    /**
     * Checks the code for obvious user mistakes such as missing or empty functions etc.
     * @return Returns an InterpreterError to describe the error that occured.
     */
    private ArrayList<InterpreterError> checkCode() {
        final String code = getTempWorkspaceContents();
        ArrayList<InterpreterError> errorList = new ArrayList<>();

        //For each type of error, run its error finder and return a list of errors found:
        for (InterpreterError e : InterpreterError.values()) {
            ArrayList<InterpreterError> currentErrorList = e.executeErrorFinder(code);
            if (!currentErrorList.isEmpty() && currentErrorList != null)
                errorList.addAll(currentErrorList);
        }//end foreach InterpreterError

        //Sort the list before returning it:
        ArrayList<InterpreterError> sortedErrorList = new ArrayList<>();
        for (final InterpreterError e : errorList) { if (e.type == ERROR) sortedErrorList.add(e); }
        for (final InterpreterError e : errorList) { if (e.type == WARNING) sortedErrorList.add(e); }
        return sortedErrorList;
    }//end checkCode()

    /**
     * Gets the contents of the temporary workspace as an XML file.
     * @return A string with the text contents of the temporary workspace XML format.
     */
    private String getTempWorkspaceContents() {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream in = new FileInputStream(new File(getFilesDir().getAbsolutePath() + "/" + AUTOSAVE_FILENAME));
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }//end while
        }//end try
        catch (IOException e) { e.printStackTrace(); }
        return sb.toString();
    }//end getTempWorkspaceContents()

    /**
     * Gets the next error found in an array or InterpreterErrors.
     * @param list The list of InterpreterErrors (ArrayList)
     * @return Returns null of no errors found, otherwise returns the first error found in the array.
     */
    private InterpreterError getNextError(ArrayList<InterpreterError> list) {
        for (final InterpreterError e : list) if (e.type == ERROR) return e;
        return null;
    }//end getNextError()

    /**
     * Gets the next warning found in an array or InterpreterErrors.
     * @param list The list of InterpreterErrors (ArrayList)
     * @return Returns null of no warnings found, otherwise returns the first error found in the array.
     */
    private InterpreterError getNextWarning(ArrayList<InterpreterError> list) {
        for (final InterpreterError e : list) if (e.type == WARNING) return e;
        return null;
    }//end getNextWarning()

}//end activity BlocklyActivity