package org.inspirecenter.amazechallenge.ui;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.AmazeColor;

public class ColorFragment extends Fragment {

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.personalization_color_fragment, container, false);

        ScrollView colorView = rootView.findViewById(R.id.colorSelectScrollView);
        TextView colorName = rootView.findViewById(R.id.colorSelectTextView);

        Bundle bundle = getArguments();
        AmazeColor color = null;
        if (bundle != null) color = (AmazeColor) bundle.getSerializable(PersonalizeActivity.PREFERENCE_KEY_COLOR);
        if (color != null) {
            colorView.setBackgroundColor(Color.parseColor(color.getHexCode()));
            final int resourceId = getResources().getIdentifier(color.getResourceIdAsString(), "string", getContext().getPackageName());
            System.out.println("RESID:" + resourceId);
            colorName.setText(getString(resourceId));
            if (isBrightColor(Color.parseColor(color.getHexCode()))) colorName.setTextColor(Color.BLACK);
        }//end if nonnull
        else {
            colorView.setBackgroundColor(Color.parseColor(AmazeColor.getDefault().getHexCode()));
            colorName.setText("error");
        }//end if null

        return rootView;
    }//end onCreateView()

    public static boolean isBrightColor(int color) {
        if (android.R.color.transparent == color) return true;
        boolean rtnValue = false;
        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1] * rgb[1] * .691 + rgb[2] * rgb[2] * .068);
        if (brightness >= 200) rtnValue = true;
        return rtnValue;
    }//end isBrightColor()

}//end class ColorFragment