package org.inspirecenter.amazechallenge.model;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.ui.PersonalizeActivity;

import static org.inspirecenter.amazechallenge.model.PlayerColor.PLAYER_COLORS;

/**
 * Created by Nicos on 31-Oct-17.
 */

public class SelectColorDialogListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    public SelectColorDialogListAdapter(@NonNull Context context, PersonalizeActivity personalizeActivity) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return PLAYER_COLORS.length;
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
        TextView colorName = (TextView) view.findViewById(R.id.colorName);
        colorName.setText(PLAYER_COLORS[i].getName());

        ImageView colorDisplay = (ImageView) view.findViewById(R.id.colorDisplay);
        colorDisplay.setBackgroundColor(Color.parseColor(PLAYER_COLORS[i].getHex()));
        colorDisplay.invalidate();

        view.setBackgroundColor(Color.parseColor(PLAYER_COLORS[i].getHex()));


        return view;
    }//end getView()

}//end class SelectColorDialogListAdapter
