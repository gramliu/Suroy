package com.dyip.suroy.rider.utility;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.dyip.suroy.rider.R;

public class Utility_Settings {

    public static void initSettingsGUI(final AppCompatActivity activity) {

        ActionBar bar = activity.getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        } else {
            Utility.log("Utility_Settings::initGUI", "ActionBar is null!");
        }

    }

}
