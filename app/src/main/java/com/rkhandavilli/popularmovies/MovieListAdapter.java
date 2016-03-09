package com.rkhandavilli.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter class to handle loading the MovieDetail list in to GridView.
 * Created by ravi on 2/29/16.
 */

public class MovieListAdapter extends ArrayAdapter<MovieDetail> {


    public MovieListAdapter(Activity context, List<MovieDetail> moviesList) {
        super(context, 0, moviesList);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        // Get the movie details from the ArrayAdapter at the selected position
        MovieDetail movieDetail = getItem(position);

        // Load movie poster from url using Picasso
        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_poster);
        String imageUrl = "http://image.tmdb.org/t/p/w500//" + movieDetail.posterPath;
        Picasso.with(getContext()).load(imageUrl).into(imageView);
        return convertView;
    }

}
