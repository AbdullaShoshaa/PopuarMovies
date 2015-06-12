package com.fedaros.www.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fedaros.www.popularmovies.Adapter.ThumbnailsAdapter;
import com.fedaros.www.popularmovies.Beans.Movie;

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
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public ArrayList<Movie> moviesReadyList ;
    private GridView moviesGrid ;
    ThumbnailsAdapter thumbnailsAdapter;
    public MainActivityFragment() {
        this.setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        moviesGrid = (GridView) view.findViewById(R.id.movies_gridview);
        moviesReadyList = new ArrayList<>();
        thumbnailsAdapter = new ThumbnailsAdapter(getActivity(),moviesReadyList);
        //thumbnailsAdapter.notifyDataSetChanged();
        moviesGrid.setAdapter(thumbnailsAdapter);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),DetailsActivity.class);
                intent.putExtra("MovieItem",moviesReadyList.get(position));
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

    private void updateMovies(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortValue = settings.getString(getString(R.string.sort_option_key),getString(R.string.sorting_option_defaultvalue));
        FetchMovieInfo task =new FetchMovieInfo();
        task.execute(sortValue);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private class FetchMovieInfo extends AsyncTask<String,Void,String> {
        final private String LOG_TAG = FetchMovieInfo.class.getSimpleName();
        private ArrayList<Movie> moviesList;

        private ArrayList<Movie> getMovieInfoAsList(String jsonString)throws JSONException{

            JSONObject fullObject = new JSONObject(jsonString);
            JSONArray fullArray = fullObject.getJSONArray("results");
            moviesList = new ArrayList<>();
            JSONObject movieItem ;

            for(int i = 0;i<fullArray.length();i++){
                movieItem = fullArray.getJSONObject(i);
                Movie movie = new Movie(movieItem.getString("id"), movieItem.getString("original_title"),
                        movieItem.getString("poster_path"), movieItem.getString("overview"),
                        movieItem.getString("release_date"),movieItem.getString("vote_average"));

                moviesList.add(movie);
                Log.d(LOG_TAG,"Movie Array size: "+moviesList.size());

            }
            return moviesList;
        }

        /*private String[] getThumbnailURLs(ArrayList<Movie> list){
            String[] thumbnailArray = new String[list.size()];
            for(int i=0;i<list.size();i++){
                thumbnailArray[i]=list.get(i).getMovieThumbnail();
            }

            return thumbnailArray;
        }*/
        @Override
        protected String doInBackground(String... params) {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieInfoString = null;

            try {
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";

                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";


                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, "818b4fa1efcc19d0a512cc15d1a706e0")
                        .build();
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieInfoString = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieInfoString = null;
                }
                movieInfoString = buffer.toString();

                Log.d(LOG_TAG, movieInfoString);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieInfoString = null;
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return movieInfoString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                moviesReadyList.clear();
                moviesReadyList.addAll(getMovieInfoAsList(s));
                for(int i=0;i<moviesReadyList.size();i++)
                    Log.d(LOG_TAG, moviesReadyList.get(i).getMovieTitle());


                Log.d(LOG_TAG, thumbnailsAdapter.getCount()+"§");
                thumbnailsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


