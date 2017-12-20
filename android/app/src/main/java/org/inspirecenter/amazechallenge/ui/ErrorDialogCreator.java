package org.inspirecenter.amazechallenge.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.algorithms.InterpreterError;

import java.util.ArrayList;

public class ErrorDialogCreator {

    /**
     * Creates the Save Dialog.
     * @param input The edit text object (code name).
     * @return Returns an AlertDialog object.
     */
    public static AlertDialog createSaveDialog(BlocklyActivity a, EditText input) {
        final AlertDialog.Builder saveDialogBuilder = new AlertDialog.Builder(a);
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
    public static AlertDialog createLoadDialog(BlocklyActivity a, ListView list) {
        final AlertDialog.Builder loadDialog = new AlertDialog.Builder(a);
        LoadDialogListAdapter listAdapter = new LoadDialogListAdapter(a, BlocklyActivity.codeNamesList, BlocklyActivity.codeFilesList, BlocklyActivity.codeFilesLastModifiedList,  a);
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
    public static AlertDialog createErrorListDialog(BlocklyActivity a, ArrayList<InterpreterError> errorList) {
        final ListView list = new ListView(a);
        final AlertDialog.Builder errorListDialog = new AlertDialog.Builder(a);
        ErrorListDialogListAdapter listAdapter = new ErrorListDialogListAdapter(a, errorList, a);
        list.setAdapter(listAdapter);

        LinearLayout.LayoutParams lp_list = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        list.setLayoutParams(lp_list);
        errorListDialog.setView(list);
        errorListDialog.setTitle(R.string.errors);
        errorListDialog.setMessage(a.getString(R.string.errors_list_message));
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
    public static AlertDialog createWarningListDialog(BlocklyActivity a, ArrayList<InterpreterError> errorList) {
        final ListView list = new ListView(a);
        final AlertDialog.Builder warningListDialog = new AlertDialog.Builder(a);
        ErrorListDialogListAdapter listAdapter = new ErrorListDialogListAdapter(a, errorList, a);
        list.setAdapter(listAdapter);

        LinearLayout.LayoutParams lp_list = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        list.setLayoutParams(lp_list);
        warningListDialog.setView(list);
        warningListDialog.setTitle(R.string.warnings);
        warningListDialog.setMessage(a.getString(R.string.warnings_list_message));
        warningListDialog.setNegativeButton(R.string.ignore_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar compilingSnackbar = Snackbar.make(a.findViewById(R.id.blocklyView), R.string.Compiling, Snackbar.LENGTH_LONG);
                View sbCView = compilingSnackbar.getView(); sbCView.setBackgroundColor(a.getColor(R.color.snackbarGreen));
                compilingSnackbar.show();
                a.runCode();
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

}//end class ErrorDialogCreator