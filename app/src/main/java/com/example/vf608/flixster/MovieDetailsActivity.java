package com.example.vf608.flixster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vf608.flixster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.example.vf608.flixster.MovieListActivity.API_BASE_URL;
import static com.example.vf608.flixster.MovieListActivity.API_KEY_PARAM;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    Movie movie;
    public final static String TAG = "MovieDetailsActivity";

    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    AsyncHttpClient client;
    ArrayList<String> videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        client = new AsyncHttpClient();
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for %s", movie.getTitle()));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
        findViewById(R.id.ivTrailer).setOnClickListener(this);
    }
    public void onClick(View v){
        getVideoId();
        String videoId = videos.get(0);
        Intent intent = new Intent(this, MovieTrailerActivity.class);
        intent.putExtra(Movie.class.getSimpleName(), videoId);
        startActivity(intent);
    }
    private void getVideoId(){
        String url = API_BASE_URL + String.format("/movie/%s/videos", movie.getId());
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        Log.i(TAG, String.format("getVideoId %s", url));
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("key");
                    for (int i = 0; i < results.length(); i++) {
                        videos.add("" + results.getJSONObject(i));
                    }
                    Log.i(TAG, String.format("Loaded %s videos", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse videos", e, true);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from videos endpoint", throwable, true);
            }
        });
    }
    private void logError(String message, Throwable error, boolean alertUser){
        Log.e(TAG, message, error);
        if(alertUser){
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

}
