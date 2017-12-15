package org.inspirecenter.amazechallenge.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.AmazeColor;
import org.inspirecenter.amazechallenge.model.AmazeIcon;

public class PersonalizationSliderActivity extends FragmentActivity {

    private ViewPager iconPager;
    private IconPagerAdapter iconPagerAdapter;
    private ViewPager colorPager;
    private ColorPagerAdapter colorPagerAdapter;

    private AmazeColor oldColor;
    private AmazeColor newColor;
    private AmazeIcon oldIcon;
    private AmazeIcon newIcon;

    private ViewPager.OnPageChangeListener iconOnPageChangeListener;
    private ViewPager.OnPageChangeListener colorOnPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalization_screen_slider);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Get the old values:
        final String oldColorName = sharedPreferences.getString(PersonalizeActivity.PREFERENCE_KEY_COLOR, AmazeColor.COLOR_BLACK.getName());
        newColor = oldColor = AmazeColor.getByName(oldColorName);
        final String oldIconName = sharedPreferences.getString(PersonalizeActivity.PREFERENCE_KEY_ICON, AmazeIcon.ICON_1.getName());
        newIcon = oldIcon = AmazeIcon.getByName(oldIconName);

        //Icon
        iconPager = findViewById(R.id.iconPager);
        iconPagerAdapter = new IconPagerAdapter(getSupportFragmentManager());
        iconPager.setAdapter(iconPagerAdapter);
        iconPager.setPageTransformer(true, new ZoomOutPageTransformer());

        //Color
        colorPager = findViewById(R.id.colorPager);
        colorPagerAdapter = new ColorPagerAdapter(getSupportFragmentManager());
        colorPager.setAdapter(colorPagerAdapter);
        colorPager.setPageTransformer(true, new ZoomOutPageTransformer());

        //Set the defaults:
        iconPager.setCurrentItem(AmazeIcon.getIndex(oldIcon));
        colorPager.setCurrentItem(AmazeColor.getIndex(oldColor));

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
                PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putString(PersonalizeActivity.PREFERENCE_KEY_COLOR, newColor.getName()).apply();
                PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putString(PersonalizeActivity.PREFERENCE_KEY_ICON, newIcon.getName()).apply();
                PersonalizationSliderActivity.super.onBackPressed();
            }//end onClick()
        });

        iconOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { /* nothing */ }

            @Override
            public void onPageSelected(int position) {
                newIcon = AmazeIcon.values()[position];
                //TODO: Make this work if possible --> Playing only the GIF that is currently viewable
                //IconFragment currentFragment = iconPagerAdapter.getRegisteredFragment(iconPager.getCurrentItem());
                //currentFragment.playGIF();
            }//end onPageSelected()

            @Override
            public void onPageScrollStateChanged(int state) { }
        };

        colorOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) { newColor = AmazeColor.values()[position]; }

            @Override
            public void onPageScrollStateChanged(int state) { }
        };
    }//end onCreate()

    @Override
    protected void onResume() {
        super.onResume();

        iconPager.addOnPageChangeListener(iconOnPageChangeListener);
        colorPager.addOnPageChangeListener(colorOnPageChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        iconPager.removeOnPageChangeListener(iconOnPageChangeListener);
        colorPager.removeOnPageChangeListener(colorOnPageChangeListener);
    }

    @Override
    public void onBackPressed() {
        if ((newIcon != oldIcon) || (newColor != oldColor)) {
            AlertDialog askForSaveDialog = createSaveDialog();
            askForSaveDialog.show();
        }//end if icons or colors differ from older ones
        else super.onBackPressed();
    }//end onBackPressed()

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
               preferences.edit().putString(PersonalizeActivity.PREFERENCE_KEY_COLOR, newColor.getName()).apply();
               preferences.edit().putString(PersonalizeActivity.PREFERENCE_KEY_ICON, newIcon.getName()).apply();
               dialogInterface.dismiss();
               PersonalizationSliderActivity.super.onBackPressed();
            }//end onClick()
        });
        return builder.create();
    }//end createSaveDialog()

}//end class PersonalizationSliderActivity
