package com.example.caio.infonema;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.caio.infonema.database.MovieItem;

import java.util.ArrayList;
import java.util.List;
/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieListFragment extends Fragment {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();


    private OnListFragmentInteractionListener mListener;

    private RecyclerView rvMovies;

    private MovieAdapter mAdapter;

    private SwipeRefreshLayout swipeRefresh;

    public MovieListFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        rvMovies = view.findViewById(R.id.rv_movie);
        Context context = view.getContext();
        rvMovies.setLayoutManager(new LinearLayoutManager(context));

        mAdapter = new MovieAdapter(
                new ArrayList<MovieItem>(),
                        (OnListFragmentInteractionListener) getActivity());

        rvMovies.setAdapter(mAdapter);


        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) getActivity());

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setRefreshing(boolean b) {
        swipeRefresh.setRefreshing(b);
    }

    public void setMovies(List<MovieItem> movies) {
        mAdapter = new MovieAdapter(
                movies,
                (OnListFragmentInteractionListener) getActivity());

        rvMovies.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(MovieItem item);
    }
}
