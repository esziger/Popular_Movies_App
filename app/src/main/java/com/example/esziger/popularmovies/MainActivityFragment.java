package com.example.esziger.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG= "MainActivityFragment";

    private MovieAdapter mMovieAdapter;

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

        updateMovies();
    }

    private void updateMovies() {
        MovieDownloader movieTask = new MovieDownloader();

        movieTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mMovieAdapter = new MovieAdapter(getActivity(),new ArrayList<Movie>());

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listview = (ListView)view.findViewById(R.id.listView_movies);
        listview.setAdapter(mMovieAdapter);

        return view;
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

    private class MovieDownloader extends AsyncTask<Void,Void,Movie []>
    {
        @Override
        protected void onPostExecute(Movie[] movies) {

            if(movies != null)
            {
                mMovieAdapter.clear();
                for(int i = 0; i < movies.length ; ++i)
                {
                    mMovieAdapter.add(movies[i]);
                }
            }

            super.onPostExecute(movies);
        }

        @Override
        protected Movie [] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

           // final String BASE_URL= "https://api.themoviedb.org/3/movie/550?";
            final String API_KEY="api_key";

            final String POPULAR_BASE_URL = "https://api.themoviedb.org/3/movie/popular?";

            //one picture can be found here:
            //http://image.tmdb.org/t/p/w185/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg

            final String API_KEY_VALUE=appid.getApiKeyValue();

            Uri buildUri = Uri.parse(POPULAR_BASE_URL).buildUpon()
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

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error in getMoviesDataFromJson()", e);
            }

            return null;

        }

        private
        Movie [] getMoviesDataFromJson(String moviesJsonString) throws JSONException {

            //List of JSON objects which needs to be extracted
            final String ARRAY_NAME = "results";
            final String TITLE = "original_title";
            final String IMAGE = "poster_path";
            final String OVERVIEW = "overview";
            final String USER_RATING = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray movieArray = moviesJson.getJSONArray(ARRAY_NAME);

            Movie [] moviesResult = new Movie[movieArray.length()];

            //final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
            //final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";
            final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

            for(int i = 0; i < movieArray.length(); ++i)
            {
                JSONObject movieJson =  movieArray.getJSONObject(i);

                //get Title
                String title = movieJson.getString(TITLE);

                String image = movieJson.getString(IMAGE);

                String overview = movieJson.getString(OVERVIEW);

                Double user_rating = movieJson.getDouble(USER_RATING);

                String release_date = movieJson.getString(RELEASE_DATE);

                Movie movie = new Movie(title, IMAGE_BASE_URL + image, overview, user_rating, release_date);
                moviesResult[i] = movie;

                Log.v(LOG_TAG, movie.toString());
            }

            return moviesResult;
        }
    }

}
