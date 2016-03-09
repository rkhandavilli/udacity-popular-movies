package com.rkhandavilli.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie Detail class describes the movie details needed to display movie information
 * Created by ravi on 2/29/16.
 */
public class MovieDetail implements Parcelable {

    String originalTitle;
    String posterPath;
    String plotSynopsis;
    String userRating;
    String releaseDate;

    public MovieDetail(String originalTitle, String posterPath, String plotSynopsis, String userRating, String releaseDate) {
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    private MovieDetail(Parcel in) {
        this.originalTitle = in.readString();
        this.posterPath = in.readString();
        this.plotSynopsis = in.readString();
        this.userRating = in.readString();
        this.releaseDate = in.readString();
    }

    public static final Creator<MovieDetail> CREATOR = new Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(plotSynopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
    }
}