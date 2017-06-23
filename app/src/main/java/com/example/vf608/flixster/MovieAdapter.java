package com.example.vf608.flixster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vf608.flixster.models.Config;
import com.example.vf608.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by vf608 on 6/22/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    ArrayList<Movie> movies;
    Config config;
    Context context;
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        String imageUrl = null;
        if(isPortrait){
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        }
        else{
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView =  isPortrait? holder.ivPosterImage : holder.ivBackdropImage;
        Glide.with(context)
            .load(imageUrl)
            .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
            .placeholder(placeholderId)
            .error(placeholderId)
            .into(imageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v){
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                Movie movie = movies.get(position);
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                context.startActivity(intent);
            }
        }
    }
}
