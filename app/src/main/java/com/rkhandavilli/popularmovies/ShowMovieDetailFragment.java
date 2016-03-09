package com.rkhandavilli.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * ShowMovieDetailFragment class to populate the movie detail screen.
 * Created by ravi on 02/29/16.
 */

public class ShowMovieDetailFragment extends Fragment {

    public ShowMovieDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of ShowMovieDetailFragment taking the movie details and index position of movie
     * @param movieDetail - movie detail of the selected movie
     * @param movieIndex - index of the selected movie
     * @return ShowMovieDetailFragment
     */
    public static ShowMovieDetailFragment newInstance(MovieDetail movieDetail, int movieIndex) {
        ShowMovieDetailFragment showMovieDetailFragment = new ShowMovieDetailFragment();
        Bundle args =  new Bundle();
        args.putParcelable("movieDetail", movieDetail);
        args.putInt("movieIndex", movieIndex);
        showMovieDetailFragment.setArguments(args);
        return showMovieDetailFragment;
    }

    /**
     * Returns the index of the current movie being displayed in the movie detail screen
     * @return movieIndex
     */
    public int getMovieIndex() {
        return getArguments().getInt("movieIndex");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_show_movie_detail, container, false);

        Bundle args = getArguments();

        MovieDetail movieDetail;


        // If args is null, then Fragment was created in single pane mode. Grab movie detail from Intent
        // Else, fragment was created in dual pane mode. Grab movie detail from Bundle arguments.

        if (args == null ) {
            Intent intent = getActivity().getIntent();
            movieDetail = intent.getExtras().getParcelable("movieParcel");
        } else {
            movieDetail = getArguments().getParcelable("movieDetail");
        }

        // Load movie detail into movie detail screen
        if (movieDetail != null) {
            ((TextView) rootView.findViewById(R.id.movie_detail_original_title)).setText(movieDetail.originalTitle);
            ((TextView) rootView.findViewById(R.id.movie_detail_rating)).setText(movieDetail.userRating);
            ((TextView) rootView.findViewById(R.id.movie_detail_release_year)).setText(movieDetail.releaseDate);
            ((TextView) rootView.findViewById(R.id.movie_detail_summary)).setText(movieDetail.plotSynopsis);
            ImageView moviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
            String imageUrl = "http://image.tmdb.org/t/p/w500//" + movieDetail.posterPath;
            Picasso.with(getContext()).load(imageUrl).into(moviePoster);
        }

        return rootView;
    }


}
