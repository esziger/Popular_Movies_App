package com.example.esziger.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG= "MainActivityFragment";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        //updateMovies();
    }

    private void updateMovies() {
        MovieDownloader movieTask = new MovieDownloader();

        movieTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_mainactivityfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_refresh)
        {
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MovieDownloader extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            final String BASE_URL= "https://api.themoviedb.org/3/movie/550?";
            final String API_KEY="api_key";

            //one picture can be found here:
            //http://image.tmdb.org/t/p/w185/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg

            final String API_KEY_VALUE=appid.getApiKeyValue();

            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY,API_KEY_VALUE)
                    .build();

            try {
                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null)
                {
                    moviesJsonStr = null;
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader((inputStream)));

                String line;

                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0)
                {
                    moviesJsonStr = null;
                    return null;
                }

                moviesJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movies JSON String:" + moviesJsonStr);

            }  catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                moviesJsonStr = null;
                return null;
            } finally {
                if(urlConnection != null)
                {
                    urlConnection.disconnect();
                }
                if(reader != null)
                {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }

                }
            }

            //getMoviesDataFromJson()

            return null;

        }
    }

}
