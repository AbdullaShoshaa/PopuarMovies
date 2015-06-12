package com.fedaros.www.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedaros.www.popularmovies.Beans.Movie;
import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing views to show Movie cover, title, release date, vote rate and overview.
 */
public class DetailsActivityFragment extends Fragment {

    private ImageView movieCover;
    private TextView title,overview,vote,releaseDate;
    private Movie movieObject;

    public DetailsActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        movieCover = (ImageView) view.findViewById(R.id.movie_cover);
        title = (TextView) view.findViewById(R.id.movie_title);
        overview = (TextView) view.findViewById(R.id.movie_overview);
        vote = (TextView) view.findViewById(R.id.movie_votes);
        releaseDate = (TextView) view.findViewById(R.id.movie_release_date);
        movieObject = intent.getParcelableExtra("MovieItem");

        //if Picasso fails to get images just set ic_launcher as image.
        try{
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185"+movieObject.getMovieThumbnail()).into(movieCover);

        }catch (Exception ex){
            movieCover.setImageResource(R.mipmap.ic_launcher);
        }

        title.setText(movieObject.getMovieTitle());
        overview.setText(overview.getText()+movieObject.getMovieOverview());
        vote.setText(vote.getText()+movieObject.getMovieVoteAverage());
        releaseDate.setText(releaseDate.getText()+movieObject.getMovieReleaseDate());
        return view;
    }
}
