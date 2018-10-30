package com.example.caio.infonema;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caio.infonema.MovieListFragment.OnListFragmentInteractionListener;
import com.example.caio.infonema.database.MovieItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<MovieItem> mItems;
    private final OnListFragmentInteractionListener mListener;

    public MovieAdapter(List<MovieItem> items, OnListFragmentInteractionListener listener) {
        mItems = items;
        mListener = listener;
    }

    public void setItems(List<MovieItem> items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(holder.mItem.getPosterUrl(), holder.mMovieCover);
        holder.mMovieTitle.setText(mItems.get(position).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mMovieCover;
        public final TextView mMovieTitle;
        public MovieItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMovieCover = (ImageView) view.findViewById(R.id.movie_cover);
            mMovieTitle = (TextView) view.findViewById(R.id.movie_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMovieTitle.getText() + "'";
        }
    }
}
