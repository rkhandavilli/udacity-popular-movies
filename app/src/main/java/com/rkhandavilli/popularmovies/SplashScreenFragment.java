package com.rkhandavilli.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * SplashScreenFragment handles logic to display the list of popular movies.
 * Contains additional logic to handle single pane or dual pane mode.
 * Created by ravi on 02/29/16.
 */
public class SplashScreenFragment extends Fragment {

    private MovieListAdapter movieListAdapter;
    ArrayList<MovieDetail> movieDetailList = new ArrayList<>();
    boolean mDualPane;

    private final String LOG_TAG = SplashScreenFragment.class.getSimpleName();

    public SplashScreenFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Look for movie detail frame created in dual pane mode
        View movieDetailFrame = getActivity().findViewById(R.id.movie_detail_frame);

        // Ascertain dual pane mode
        mDualPane = movieDetailFrame != null && movieDetailFrame.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "onCreateView - retrieve from saved list");
            movieDetailList = savedInstanceState.getParcelableArrayList("savedMovieList");
        }

        // Init custom MovieListAdapter
        movieListAdapter = new MovieListAdapter(getActivity(), new ArrayList<MovieDetail>());

        // Get a reference to the GridView and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieListAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showMovieDetail(position);
            }
        });

        return rootView;
    }

    /**
     * Helper function to display movie detail screen either by displaying a fragment in current UI in dual pane mode
     * or starting a new activity in single pane mode  in which to display the movie detail.
     * @param position - index of the movie selected
     */
    void showMovieDetail(int position) {
        if (mDualPane) {
            ShowMovieDetailFragment showMovieDetailFragment = (ShowMovieDetailFragment) getFragmentManager().findFragmentById(R.id.movie_detail_frame);
            if (showMovieDetailFragment == null || position != showMovieDetailFragment.getMovieIndex()) {
                showMovieDetailFragment = ShowMovieDetailFragment.newInstance(movieListAdapter.getItem(position), position);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.movie_detail_frame, showMovieDetailFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            Intent showMovieDetail = new Intent(getActivity(), ShowMovieDetail.class);
            showMovieDetail.putExtra("movieParcel", movieListAdapter.getItem(position));
            startActivity(showMovieDetail);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // if movie list has not been fetched before, start async task to fetch movie detail
        if (movieDetailList.isEmpty()) {
            Log.d(LOG_TAG, "onStart - no saved list");
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute();
        } else {

            // movie list has already been fetched, clear and reset ArrayAdapter
            Log.d(LOG_TAG, "onStart - retrieving saved list");
            movieListAdapter.clear();
            movieListAdapter.addAll(movieDetailList);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save the movie list fetched from TMDB
        Log.d(LOG_TAG, "in onSaveInstanceState");
        outState.putParcelableArrayList("savedMovieList", movieDetailList);
    }

    /**
     * AsyncTask class to fetch movie detail from TMDB.
     * Created by ravi on 02/29/16.
     */
    public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieListResult = null;

            // Setup the fetch url and params
            //final String TMDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            //final String SORT_BY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            //final String VOTE_COUNT_GTE_PARAM = "vote_count.gte";
            //final String VOTE_COUNT_VALUE = "1000";
            //final String SORT_BY_POPULAR = "popularity.desc";
            //final String SORT_BY_RATING = "vote_average.desc";

            final String TMDB_POPULAR_URL = "http://api.themoviedb.org/3/movie/popular?";
            final String TMDB_TOP_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated?";

            // Update correct api key in app/build.grade
            final String apiKey = BuildConfig.TMDB_API_KEY;

            Uri fetchMoviesUri;

            // Get the sort order preference from Settings
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortOrderPref = preferences.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_most_popular));

             if (sortOrderPref.equals(getString(R.string.pref_sort_order_most_popular))) {

             // Fetch the most popular movies
                fetchMoviesUri = Uri.parse(TMDB_POPULAR_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
            } else if (sortOrderPref.equals(getString(R.string.pref_sort_order_highest_rated))) {

            // Fetch the highest rated movies
                fetchMoviesUri = Uri.parse(TMDB_TOP_RATED_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();

            } else {
                Log.d(LOG_TAG, "Invalid sort order parameter; " + sortOrderPref + ". Preference set to Most popular");
                return null;
            }

            try {

                URL url = new URL(fetchMoviesUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append (line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream is empty, nothing to do
                    return null;
                }

                movieListResult = buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error", e);
                    }
                }
            }

            try {
                getMovieListFromJson(movieListResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {

            if (movieDetailList != null) {
                movieListAdapter.clear();
                movieListAdapter.addAll(movieDetailList);
                if (mDualPane) {
                    showMovieDetail(0);
                }
            }
        }


        /**
         * Helper function to extract movie detail as ArrayList from JSON string
         * @param movieListStr - Json string of movie detail
         * @throws JSONException
         */
        private void getMovieListFromJson(String movieListStr) throws JSONException {

            final String MOVIE_RESULTS = "results";
            final String MOVIE_ORIGINAL_TITLE = "original_title";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_POSTER_PATH = "poster_path";
            final String MOVIE_SUMMARY = "overview";
            final String MOVIE_RATING = "vote_average";

            JSONObject movieListJson = new JSONObject(movieListStr);
            JSONArray movieListArray = movieListJson.getJSONArray(MOVIE_RESULTS);

            // clear previous movie list (fetched via a post call)
            movieDetailList.clear();

            // iterate through json string array to conver to the ArrayList of movie detail
            for (int i = 0; i < movieListArray.length(); i++) {

                JSONObject movieJsonObject = movieListArray.getJSONObject(i);
                String posterPath = movieJsonObject.getString(MOVIE_POSTER_PATH);
                String plotSummary = movieJsonObject.getString(MOVIE_SUMMARY);
                String releaseDate = (movieJsonObject.getString(MOVIE_RELEASE_DATE)).substring(0, 4);
                String originalTitle = movieJsonObject.getString(MOVIE_ORIGINAL_TITLE);
                String movieRating = movieJsonObject.getString(MOVIE_RATING) + "/10";
                movieDetailList.add(new MovieDetail(originalTitle, posterPath, plotSummary, movieRating, releaseDate));

            }

        }


    }
}
