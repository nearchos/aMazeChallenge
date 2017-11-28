package org.inspirecenter.amazechallenge.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.AmazeColor;
import org.inspirecenter.amazechallenge.model.AmazeIcon;

public class PersonalizationSliderActivity extends FragmentActivity {

    private ViewPager iconPager;
    private IconPagerAdapter iconPagerAdapter;
    private ViewPager colorPager;
    private ColorPagerAdapter colorPagerAdapter;
    private int oldColorIndex = 0;
    private int oldIconIndex = 0;
    private int newColorIndex = 0;
    private int newIconIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalization_screen_slider);

        //Get the old values:
        oldColorIndex = PreferenceManager.getDefaultSharedPreferences(this).getInt(PersonalizeActivity.PREFERENCE_KEY_COLOR, 0);
        oldIconIndex = PreferenceManager.getDefaultSharedPreferences(this).getInt(PersonalizeActivity.PREFERENCE_KEY_ICON, 0);

        //Icon
        iconPager = (ViewPager) findViewById(R.id.iconPager);
        iconPagerAdapter = new IconPagerAdapter(getSupportFragmentManager());
        iconPager.setAdapter(iconPagerAdapter);
        iconPager.setPageTransformer(true, new ZoomOutPageTransformer());
        iconPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                newIconIndex = position;
                //IconFragment currentFragment = iconPagerAdapter.getRegisteredFragment(iconPager.getCurrentItem());
                //currentFragment.playGIF();
            }//end onPageSelected()

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        //Color
        colorPager = (ViewPager) findViewById(R.id.colorPager);
        colorPagerAdapter = new ColorPagerAdapter(getSupportFragmentManager());
        colorPager.setAdapter(colorPagerAdapter);
        colorPager.setPageTransformer(true, new ZoomOutPageTransformer());
        colorPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) { newColorIndex = position; }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        //Set the defaults:
        colorPager.setCurrentItem(oldColorIndex);
        iconPager.setCurrentItem(oldIconIndex);

        Button cancelButton = findViewById(R.id.personalization_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button saveButton = findViewById(R.id.personalization_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putInt(PersonalizeActivity.PREFERENCE_KEY_COLOR, newColorIndex).apply();
                PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putInt(PersonalizeActivity.PREFERENCE_KEY_ICON, newIconIndex).apply();
                PersonalizationSliderActivity.super.onBackPressed();
            }//end onClick()
        });

    }//end onCreate()

    @Override
    public void onBackPressed() {
        if ((newIconIndex != oldIconIndex) || (newColorIndex != oldColorIndex)) {
            AlertDialog askForSaveDialog = createSaveDialog();
            askForSaveDialog.show();
        }//end if icons or colors differ from older ones
        else super.onBackPressed();
    }//end onBackPressed()

    public class IconPagerAdapter extends FragmentStatePagerAdapter {

        SparseArray<IconFragment> registeredFragments = new SparseArray<>();

        public IconPagerAdapter(FragmentManager fm) { super(fm); }

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
            IconFragment fragment = (IconFragment) super.instantiateItem(container, position);
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
        public int getCount() { return AmazeIcon.values().length; }

    }//end class ScreenSliderIconPagerAdapter

    public class ColorPagerAdapter extends FragmentStatePagerAdapter {

        public ColorPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            ColorFragment colorFragment = new ColorFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(PersonalizeActivity.PREFERENCE_KEY_COLOR, AmazeColor.values()[position]);
            colorFragment.setArguments(bundle);
            return colorFragment;
        }//end getItem()

        @Override
        public int getCount() { return AmazeColor.values().length; }

    }//end class ScreenSliderColorPagerAdapter

    private AlertDialog createSaveDialog() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.warning));
        builder.setMessage(getString(R.string.changes_not_saved));
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PersonalizationSliderActivity.super.onBackPressed();
            }//end onClick()
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               preferences.edit().putInt(PersonalizeActivity.PREFERENCE_KEY_COLOR, newColorIndex).apply();
               preferences.edit().putInt(PersonalizeActivity.PREFERENCE_KEY_ICON, newIconIndex).apply();
               dialogInterface.dismiss();
               PersonalizationSliderActivity.super.onBackPressed();
            }//end onClick()
        });
        return builder.create();
    }//end createSaveDialog()

}//end class PersonalizationSliderActivity
