package com.rkhandavilli.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * ShowMovieDetail is the second level activity which handles the display of movie detail
 * Created by ravi on 02/29/16.
 */

public class ShowMovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_movie_detail);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_fragment, new ShowMovieDetailFragment())
                    .commit();
        }
    }
}
