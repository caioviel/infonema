package com.example.caio.infonema;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caio.infonema.database.AppDatabase;
import com.example.caio.infonema.database.GenresConveter;
import com.example.caio.infonema.database.MovieEntity;
import com.example.caio.infonema.viewModel.DataRepository;
import com.example.caio.infonema.viewModel.DetailViewModel;
import com.example.caio.infonema.viewModel.DetailViewModelFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.SimpleDateFormat;


public class DetailFragment extends Fragment implements DataRepository.LoadedDataListener<LiveData<MovieEntity>> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private AppDatabase mDb;
    private TextView txtTitle, txtPopularity, txtVotes, txtOverview, txtGenres,
            txtRevenue, txtBudget, txtTagLine, txtReleaseDate, txtRuntime;
    private ImageView imgCover, imgBackDrop, imgAdult;

    private View detailLayout;
    private View imgLoading;
    private View emptyLayout;

    private DetailViewModel viewModel;

    private SimpleDateFormat dateFormat;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onCreateView of DetailFragment");

        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        detailLayout = view.findViewById(R.id.detail_layout);
        imgLoading = view.findViewById(R.id.img_loading);
        emptyLayout = view.findViewById(R.id.layout_noview);


        txtTitle = view.findViewById(R.id.txt_title);
        txtPopularity = view.findViewById(R.id.txt_popularity);
        txtVotes = view.findViewById(R.id.txt_votes);
        txtOverview = view.findViewById(R.id.txt_overview);
        txtRevenue = view.findViewById(R.id.txt_revenue);
        txtBudget = view.findViewById(R.id.txt_budget);
        txtTagLine = view.findViewById(R.id.txt_tagline);
        txtReleaseDate = view.findViewById(R.id.txt_release_date);
        txtRuntime = view.findViewById(R.id.txt_runtime);
        txtGenres = view.findViewById(R.id.txt_genres);

        imgCover = view.findViewById(R.id.img_cover);
        imgBackDrop = view.findViewById(R.id.img_backdrop_url);
        imgAdult = view.findViewById(R.id.img_adult);


        mDb = AppDatabase.getsInstance(getContext());

        dateFormat = new SimpleDateFormat("yyyy-MMM");


        Intent intent = getActivity().getIntent();
        int movieID = intent.getIntExtra("MOVIE_ID", 0);

        if (movieID == 0 && getArguments() != null) {
            movieID = getArguments().getInt("MOVIE_ID");
        }

        DetailViewModelFactory factory = new DetailViewModelFactory(mDb, movieID);
        viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);

        if (movieID != 0) {
            Log.i(LOG_TAG, "Movie ID to Loading: " + Integer.toString(movieID));
            loadMovieById(movieID);
        }

        return view;

    }

    @Override
    public void onAttach(Context context) {
        Log.i(LOG_TAG, "onAttach of DetailFragment");
        super.onAttach(context);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void loadMovieById(int movieID) {
        Log.i(LOG_TAG, "loadMovieById: " + Integer.toString(movieID));

        imgLoading.setVisibility(View.VISIBLE);
        detailLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);

        viewModel.setMovieId(movieID);
        viewModel.loadData(this);
    }

    @Override
    public void OnDataLoading(int status, final LiveData<MovieEntity> data) {
        Log.i(LOG_TAG, "OnDataLoading");
        switch (status) {
            case DataRepository.DATABASE_DATA:
                Toast.makeText(getActivity(), R.string.cache_info, Toast.LENGTH_LONG).show();
            case DataRepository.API_DATA:

                data.observe(this, new Observer<MovieEntity>() {
                    @Override
                    public void onChanged(@Nullable MovieEntity movie) {
                        Log.i(LOG_TAG, "onChanged: " + movie.getTitle());
                        adjustUI(movie);

                        data.removeObserver(this);
                    }
                });

                break;
            case DataRepository.ERROR:
                imgLoading.setVisibility(View.GONE);
                detailLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void adjustUI(MovieEntity movieEntity) {
        txtTitle.setText(movieEntity.getTitle());
        txtPopularity.setText(Float.toString(movieEntity.getPopularity()));
        txtVotes.setText(Float.toString(movieEntity.getVoteAverage()));
        txtOverview.setText(movieEntity.getOverview());
        txtRevenue.setText(Integer.toString(movieEntity.getReveneu()));
        txtBudget.setText(Integer.toString(movieEntity.getBudget()));
        txtRuntime.setText(Integer.toString(movieEntity.getRuntime()));
        if (movieEntity.getTagline() != null && !movieEntity.getTagline().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(movieEntity.getTagline());
            sb.append("\"");
            txtTagLine.setText(sb.toString());
        }
        txtReleaseDate.setText(dateFormat.format(movieEntity.getReleaseDate()));

        txtGenres.setText(GenresConveter.toString(movieEntity.getGenres()));

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(movieEntity.getPosterUrl(), imgCover);
        imageLoader.displayImage(movieEntity.getBackdropUrl(), imgBackDrop, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imgLoading.setVisibility(View.GONE);
                detailLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
            }

        });

        if (movieEntity.isAdult()) {
            imgAdult.setVisibility(View.VISIBLE);
        } else {
            imgAdult.setVisibility(View.GONE);
        }

    }

    public void setMovieId(int id) {
        loadMovieById(id);
    }
}
