package com.example.esziger.popularmovies;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by esziger on 2016-09-09.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    public static class ViewHolder
    {
        ImageView image;
        TextView title;
    }

    public MovieAdapter(Context context, ArrayList<Movie> movies)
    {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_movie, parent, false);

            //viewHolder.image = (ImageView) convertView.findViewById(R.id.imageIcon);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.image.setImageResource(movie.getPosterImage());

        viewHolder.title.setText(movie.getOriginalTitle());

        return convertView;
    }
}
