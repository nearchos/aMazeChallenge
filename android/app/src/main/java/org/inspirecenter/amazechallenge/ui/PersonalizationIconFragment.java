package org.inspirecenter.amazechallenge.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.inspirecenter.amazechallenge.R;

public class PersonalizationIconFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.personalization_icon_fragment, container, false);

        return rootView;
    }//end onCreateView()

}//end class PersonalizationIconFragment