package org.inspirecenter.amazechallenge.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import org.inspirecenter.amazechallenge.model.AmazeIcon;

/**
 * @author Nearchos
 *         Created: 11-Dec-17
 */

public class IconPagerAdapter extends FragmentStatePagerAdapter {

    private SparseArray<IconFragment> registeredFragments = new SparseArray<>();

    IconPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public IconFragment getItem(int position) {
        IconFragment iconFragment = new IconFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PersonalizeActivity.PREFERENCE_KEY_ICON, AmazeIcon.values()[position]);
        iconFragment.setArguments(bundle);
        return iconFragment;
    }//end getItem()

    @Override
    public IconFragment instantiateItem(ViewGroup container, int position) {
        final IconFragment fragment = (IconFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }//end instantiateItem()

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }//end destroyItem()

    public IconFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }//end getRegisteredFragment()

    @Override
    public int getCount() {
        return AmazeIcon.values().length;
    }
}