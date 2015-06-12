package com.fedaros.www.popularmovies.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fedaros.www.popularmovies.Beans.Movie;
import com.fedaros.www.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Shoshaa on 6/10/15.
 */
public class ThumbnailsAdapter extends BaseAdapter{
    private String LOG_TAG = ThumbnailsAdapter.class.getSimpleName();
    private ArrayList<Movie> moviesList;
    Context mContext;

    public ThumbnailsAdapter(Context context,ArrayList<Movie> list) {

        this.moviesList = list;
        this.mContext = context;
        Log.d(LOG_TAG,"ArrayList Inside Adapter: count "+moviesList.size());
    }

    @Override
    public int getCount() {
        Log.d(LOG_TAG,"getCount() "+moviesList.size());
        return moviesList.size();
    }

    @Override
    public Object getItem(int position) {
        return moviesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView thumbnail ;

        //LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            thumbnail = new ImageView(mContext);
            //thumbnail.setLayoutParams(new GridView.LayoutParams(200, 400));
            thumbnail.setMinimumWidth(200);
            thumbnail.setMinimumHeight(800);
            thumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
            thumbnail.setPadding(5, 5, 5, 5);
            Log.d(LOG_TAG,"Image "+position+" has been set");
        }else{
            thumbnail = (ImageView) convertView;
            Log.d(LOG_TAG, "Image " + position + " has been set");
        }
        thumbnail.setImageResource(R.mipmap.ic_launcher);
        try{
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185"+moviesList.get(position).getMovieThumbnail()).into(thumbnail);

        }catch (Exception ex){
            thumbnail.setImageResource(R.mipmap.ic_launcher);
        }
        return thumbnail;
    }


}
