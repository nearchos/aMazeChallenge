package org.inspirecenter.amazechallenge.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.inspirecenter.amazechallenge.model.AmazeColor;

/**
 * @author Nearchos
 *         Created: 11-Dec-17
 */

public class ColorPagerAdapter extends FragmentStatePagerAdapter {

    ColorPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ColorFragment colorFragment = new ColorFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PersonalizeActivity.PREFERENCE_KEY_COLOR, AmazeColor.values()[position]);
        colorFragment.setArguments(bundle);
        return colorFragment;
    }//end getItem()

    @Override
    public int getCount() {
        return AmazeColor.values().length;
    }
}
