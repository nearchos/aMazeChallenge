package org.inspirecenter.amazechallenge.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.ShapeColor;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */

public class ColorSpinnerAdapter extends ArrayAdapter<ShapeColor> {

    public ColorSpinnerAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource, ShapeColor.getAll());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            final LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.item_player_color, null);
        }

        final ShapeColor shapeColor = getItem(position);
        if (shapeColor != null) {
            ((TextView) view).setText(shapeColor.getName());
            final int color = shapeColor.getCode();
            ((TextView) view).setTextColor(Color.parseColor("#" + Integer.toHexString(color)));
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}