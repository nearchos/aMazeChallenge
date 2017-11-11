package org.inspirecenter.amazechallenge.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.InterpreterError;

import java.util.ArrayList;

/**
 * Created by Nicos on 29-Oct-17.
 */

public class ErrorListDialogListAdapter extends BaseAdapter {

    private ArrayList<InterpreterError> errors;
    private final BlocklyActivity activity;
    private LayoutInflater inflater;

    public ErrorListDialogListAdapter(@NonNull Context context, ArrayList<InterpreterError> errors, BlocklyActivity activity) {
        this.errors = errors;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return errors.size();
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
        view = inflater.inflate(R.layout.errorlist_dialog_item, null);
        TextView errorInfo = (TextView) view.findViewById(R.id.errorInfo);
        errorInfo.setText(errors.get(i).name);
        ImageView errorType = (ImageView) view.findViewById(R.id.errorType);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.errorList_item);
        switch (errors.get(i).type) {
            case ERROR:
                errorType.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_report_problem_black_24dp));
                errorType.setColorFilter(activity.getResources().getColor(R.color.snackbarRed));
                layout.setBackgroundColor(activity.getColor(R.color.lightRed));
                break;
            case WARNING:
                errorType.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_info_black_24dp));
                errorType.setColorFilter(activity.getResources().getColor(R.color.snackbarYellow));
                layout.setBackgroundColor(activity.getColor(R.color.lightYellow));
                break;
        }//end switch
        DrawableCompat.setTint(errorType.getDrawable(), Color.BLACK);
        return view;
    }//end getView()

}//end class ErrorListDialogListAdapter