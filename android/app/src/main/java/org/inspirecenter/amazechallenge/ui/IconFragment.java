package org.inspirecenter.amazechallenge.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.AmazeIcon;

import java.util.ArrayList;

public class IconFragment extends Fragment {

    public static ArrayList<GIFView> gifViews;

    private GIFView gifView = null;

    public IconFragment() {
        gifViews = new ArrayList<>();
    }//end IconFragment()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.personalization_icon_fragment, container, false);

        gifView = (GIFView) rootView.findViewById(R.id.gifView);
        gifViews.add(gifView);

        Bundle bundle = getArguments();
        AmazeIcon icon = null;
        if (bundle != null) icon = (AmazeIcon) bundle.getSerializable(PersonalizeActivity.PREFERENCE_KEY_ICON);
        if (icon != null) gifView.setImageResource(getDrawableResourceId(icon));
        else gifView.setBackgroundColor(Color.BLACK);

        return rootView;
    }//end onCreateView()

    public void playGIF() {
        stopAllGIFs();
        if (gifView != null) gifView.play();
    }//end playGIF()

    public void stopGIF() {
        gifView.stop();
    }

    public void stopAllGIFs() {
        for (GIFView g : gifViews) if (g != null) g.stop();
    }//end stopAllGIFs()

    int getDrawableResourceId(final AmazeIcon amazeIcon) {
        return getResources().getIdentifier(amazeIcon.getResourceName(), "drawable", getActivity().getPackageName());
    }
}//end class IconFragment