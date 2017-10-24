package org.inspirecenter.amazechallenge.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.BuildConfig;

import java.util.List;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new AboutFragment())
                .commit();
    }

    public static class AboutFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(org.inspirecenter.amazechallenge.R.xml.about);

            {
                final Preference applicationVersionPreference = findPreference("applicationVersion");
                applicationVersionPreference.setSummary(BuildConfig.VERSION_NAME);
            }
            {
                final Preference rateUsPreference = findPreference("rateUs");
                rateUsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=" + BuildConfig.APPLICATION_ID)));
                        return false;
                    }
                });
            }
            {
                final Preference sharePreference = findPreference("share");
                sharePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final String title = getString(org.inspirecenter.amazechallenge.R.string.app_name);
                        final String text = getString(org.inspirecenter.amazechallenge.R.string.SharingInstructions);
                        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                        final String shareText = getString(org.inspirecenter.amazechallenge.R.string.Share);
                        if(isIntentAvailable(getActivity(), shareIntent)) {
                            startActivity(Intent.createChooser(shareIntent, shareText));
                        } else {
                            Toast.makeText(getActivity(), org.inspirecenter.amazechallenge.R.string.NoAppsAvailable, Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
            }
        }
    }

    static private boolean isIntentAvailable(final Context context, final Intent intent)
    {
        if(context == null) return false;
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}