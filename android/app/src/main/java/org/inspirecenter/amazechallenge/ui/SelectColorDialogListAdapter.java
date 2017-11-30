package org.inspirecenter.amazechallenge.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.model.AmazeColor;
import org.inspirecenter.amazechallenge.R;

/**
 * @author Nicos on 31-Oct-17.
 */

public class SelectColorDialogListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    public SelectColorDialogListAdapter(@NonNull Context context, PersonalizeActivity personalizeActivity) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return AmazeColor.values().length;
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

        view = inflater.inflate(R.layout.color_select_item, null);
        TextView colorName = view.findViewById(R.id.colorName);
        final AmazeColor amazeColor = AmazeColor.values()[i];
        colorName.setText(amazeColor.getName());

        final ImageView colorDisplay = view.findViewById(R.id.colorDisplay);
        colorDisplay.setBackgroundColor(Color.parseColor(amazeColor.getCode()));
        colorDisplay.invalidate();

        view.setBackgroundColor(Color.parseColor(amazeColor.getCode()));


        return view;
    }//end getView()

}//end class SelectColorDialogListAdapter
