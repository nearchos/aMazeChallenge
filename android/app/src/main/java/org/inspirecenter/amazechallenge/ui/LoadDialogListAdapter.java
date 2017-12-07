package org.inspirecenter.amazechallenge.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.ui.BlocklyActivity;

import java.util.ArrayList;

/**
 * Created by Nicos on 29-Oct-17.
 */

public class LoadDialogListAdapter extends BaseAdapter {

    private ArrayList<String> fileNames;
    private ArrayList<String> codeNames;
    private ArrayList<String> dateModifiedList;
    private final BlocklyActivity activity;
    private LayoutInflater inflater;

    public LoadDialogListAdapter(@NonNull Context context, ArrayList<String> codeNames, ArrayList<String> fileNames, ArrayList<String> dateModifiedList, BlocklyActivity activity) {
        this.fileNames = fileNames;
        this.codeNames = codeNames;
        this.dateModifiedList = dateModifiedList;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return codeNames.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.loaddialog_listitem, null);
        TextView codeName = (TextView) view.findViewById(R.id.codeName);
        TextView dateModified = view.findViewById(R.id.dateModified);
        codeName.setText(codeNames.get(i));
        dateModified.setText(dateModifiedList.get(i));
        ImageView delete = view.findViewById(R.id.deleteCode);
        if (fileNames.get(i).startsWith("playercode_")) delete.setVisibility(View.VISIBLE);
        else delete.setVisibility(View.INVISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(view.getContext());
                deleteDialog.setTitle(R.string.delete_code)
                        .setMessage(R.string.delete_code_message)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i2) {
                                activity.deleteInternalFile(fileNames.get(i));
                                Snackbar.make(activity.findViewById(R.id.blocklyView), R.string.code_deleted, Snackbar.LENGTH_LONG).show();
                                dialogInterface.dismiss();
                                BlocklyActivity.loadDialog.dismiss();
                            }//end onClick()
                        })
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                dialogInterface.cancel();
                            }
                        }).create().show();
            }
        });
        return view;
    }
}
