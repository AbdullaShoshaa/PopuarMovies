package com.fedaros.www.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
 * A placeholder fragment containing Movies thumbnails List as GridView.
 */
public class MainActivityFragment extends Fragment {

    //ArrayList will hold Movie Objects generated in AsynTask.
    public ArrayList<Movie> moviesReadyList ;
    //GridView hold movies thumbnails.
    private GridView moviesGrid ;
    //Custom Adapter to generate movies thumbnails and poulates them in GridView.
    private ThumbnailsAdapter thumbnailsAdapter;
    public MainActivityFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        moviesGrid = (GridView) view.findViewById(R.id.movies_gridview);
        moviesReadyList = new ArrayList<>();
        thumbnailsAdapter = new ThumbnailsAdapter(getActivity(),moviesReadyList);
        moviesGrid.setAdapter(thumbnailsAdapter);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("MovieItem", moviesReadyList.get(position));
                getActivity().startActivity(intent);
            }
        });
        return view;
    }
    //updates the GridView Adapter with Movies items.
    private void updateMovies(){
        //get sorting option from SharedPreferences.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortValue = settings.getString(getString(R.string.sort_option_key),getString(R.string.sorting_option_defaultvalue));
        //execute fetchMoviesInfo from API.
        FetchMovieInfo task =new FetchMovieInfo();
        task.execute(sortValue);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isOnline())
            updateMovies();
        else
            alertNotOnline();
    }
    //show simple dialog to tell the user that he/she is not connected to the internet.
    private void alertNotOnline(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.notconnected_message))
                .setTitle(getActivity().getString(R.string.notconnected_dialog_title)).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing.
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //check if user online. find this answer http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //AsyncTask to pull movies data from TheMovieDB.com API
    private class FetchMovieInfo extends AsyncTask<String,Void,String> {
        final private String LOG_TAG = FetchMovieInfo.class.getSimpleName();
        private ArrayList<Movie> moviesList;
        //a method takes JSON resault as String and stores it in Mvies Object, add them to ArrayList and returns the ArrayList.
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
                final String API_KEY = "818b4fa1efcc19d0a512cc15d1a706e0";
                //construct a URL with required parameters.
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to themoviedb.com API, and open the connection
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
                // If the code didn't successfully get the movies data, there's no point in attempting
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
                //reset Movie Objects in Adapter
                moviesReadyList.clear();
                moviesReadyList.addAll(getMovieInfoAsList(s));
                thumbnailsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}


