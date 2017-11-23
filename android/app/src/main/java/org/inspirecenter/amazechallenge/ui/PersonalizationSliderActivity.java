package org.inspirecenter.amazechallenge.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.AmazeColor;

public class PersonalizationSliderActivity extends FragmentActivity {

    public static final String PERSONALIZATION_COLOR_FRAGMENT__COLOR_KEY = "color";

    private ViewPager iconPager;
    private PagerAdapter iconPagerAdapter;
    private ViewPager colorPager;
    private PagerAdapter colorPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalization_screen_slider);

        //Icon
        iconPager = (ViewPager) findViewById(R.id.iconPager);
        iconPagerAdapter = new ScreenSliderIconPagerAdapter(getSupportFragmentManager());
        iconPager.setAdapter(iconPagerAdapter);
        iconPager.setPageTransformer(true, new ZoomOutPageTransformer());
        //Color
        colorPager = (ViewPager) findViewById(R.id.colorPager);
        colorPagerAdapter = new ScreenSliderColorPagerAdapter(getSupportFragmentManager());
        colorPager.setAdapter(colorPagerAdapter);
        colorPager.setPageTransformer(true, new ZoomOutPageTransformer());

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
                Snackbar.make(view, "I'm useless for now :( ", Snackbar.LENGTH_SHORT).show();
            }
        });

    }//end onCreate()

    //TODO: Warn about unsaved changes on back press.
    /*@Override
    public void onBackPressed() {

    }//end onBackPressed()*/


    public class ScreenSliderIconPagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSliderIconPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            return new PersonalizationIconFragment();
            //TODO Get each icon from a list of icons.
        }

        @Override
        public int getCount() {
            return 0; //TODO Change this to the length of array containing the icons.
        }
    }//end class ScreenSliderIconPagerAdapter

    public class ScreenSliderColorPagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSliderColorPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            PersonalizationColorFragment colorFragment = new PersonalizationColorFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(PERSONALIZATION_COLOR_FRAGMENT__COLOR_KEY, AmazeColor.values()[position]);
            colorFragment.setArguments(bundle);
            return colorFragment;
        }//end getItem()

        @Override
        public int getCount() { return AmazeColor.values().length; }
    }//end class ScreenSliderColorPagerAdapter

}//end class PersonalizationSliderActivity
