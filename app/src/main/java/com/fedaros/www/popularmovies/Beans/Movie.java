package com.fedaros.www.popularmovies.Beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shoshaa on 6/10/15.
 *
 * used this answer when I was searching about serialization
 * http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
 */
public class Movie implements Parcelable{

    private String movieID = null;
    private String movieTitle = null;
    private String movieOverview = null;
    private String movieThumbnail = null;
    private String movieReleaseDate = null;
    private String movieVoteAverage = null;

    public Movie(String movieID,String movieTitle, String movieThumbnail, String movieOverview, String movieReleaseDate, String movieVoteAverage) {
        this.movieOverview = movieOverview;
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.movieThumbnail = movieThumbnail;
        this.movieVoteAverage = movieVoteAverage;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public void setMovieThumbnail(String movieThumbnail) {
        this.movieThumbnail = movieThumbnail;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public void setMovieVoteAverage(String movieVoteAverage) {
        this.movieVoteAverage = movieVoteAverage;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieID() {
        return movieID;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public String getMovieThumbnail() {
        return movieThumbnail;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public String getMovieVoteAverage() {
        return movieVoteAverage;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String toString(){
        return "ID: "+movieID+" Title: "+movieTitle+" Overview: "+movieOverview+" imageURL: "+movieThumbnail+" Votes: "+movieVoteAverage+" Release Date: "
                +movieReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieID);
        dest.writeString(movieTitle);
        dest.writeString(movieOverview);
        dest.writeString(movieReleaseDate);
        dest.writeString(movieVoteAverage);
        dest.writeString(movieThumbnail);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        movieID = in.readString();
        movieTitle = in.readString();
        movieOverview = in.readString();
        movieReleaseDate = in.readString();
        movieVoteAverage = in.readString();
        movieThumbnail = in.readString();
    }
}

