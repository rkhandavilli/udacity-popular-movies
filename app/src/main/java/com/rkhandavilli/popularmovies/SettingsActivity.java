package com.rkhandavilli.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * SettingsActivity class to implement the Settings menu.
 * Created by ravi on 02/29/16.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
