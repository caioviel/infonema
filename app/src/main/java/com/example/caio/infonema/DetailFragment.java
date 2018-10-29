package com.example.caio.infonema;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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

import com.example.caio.infonema.database.AppDatabase;
import com.example.caio.infonema.database.MovieEntity;
import com.example.caio.infonema.service.RetrofitSingletron;
import com.nostra13.universalimageloader.core.ImageLoader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private AppDatabase mDb;
    private int movieID;
    private TextView txtTitle, txtPopularity, txtVotes, txtOverview,
            txtRevenue, txtBudget, txtTagLine, txtReleaseDate, txtRuntime;
    private ImageView imgCover, imgBackDrop, imgAdult;


    public static DetailFragment newInstance() {
        return new DetailFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        txtTitle = view.findViewById(R.id.txt_title);
        txtPopularity = view.findViewById(R.id.txt_popularity);
        txtVotes = view.findViewById(R.id.txt_votes);
        txtOverview = view.findViewById(R.id.txt_overview);
        txtRevenue = view.findViewById(R.id.txt_revenue);
        txtBudget = view.findViewById(R.id.txt_budget);
        txtTagLine = view.findViewById(R.id.txt_tagline);
        txtReleaseDate = view.findViewById(R.id.txt_release_date);
        txtRuntime = view.findViewById(R.id.txt_runtime);

        imgCover = view.findViewById(R.id.img_cover);
        imgBackDrop = view.findViewById(R.id.img_backdrop_url);
        imgAdult = view.findViewById(R.id.img_adult);


        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDb = AppDatabase.getsInstance(getContext());

        Intent intent = getActivity().getIntent();
        movieID = intent.getIntExtra("MOVIE_ID", 0);


        if (movieID != 0) {
            RetrofitSingletron retrofit = RetrofitSingletron.getInstance();
            Call<MovieEntity> call = retrofit.service().getMovieById(movieID);
            call.enqueue(new Callback<MovieEntity>() {
                @Override
                public void onResponse(Call<MovieEntity> call, final Response<MovieEntity> response) {
                    Log.i(LOG_TAG, "Movie List Obtained");
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(LOG_TAG, "Update Database");
                            mDb.movieDAO().updateMovie(response.body());

                        }
                    });
                }

                @Override
                public void onFailure(Call<MovieEntity> call, Throwable t) {
                    Log.e(LOG_TAG, "Failure retring data from API: " + t.getMessage());

                }
            });


            DetailViewModelFactory factory = new DetailViewModelFactory(mDb, movieID);
            final DetailViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
            viewModel.getMovie().observe(this, new Observer<MovieEntity>() {
                @Override
                public void onChanged(@Nullable MovieEntity movieEntity) {
                    Log.e(LOG_TAG, "onChanged");

                    txtTitle.setText(movieEntity.getTitle());
                    txtPopularity.setText(Float.toString(movieEntity.getPopularity()));
                    txtVotes.setText(Float.toString(movieEntity.getVoteAverage()));
                    txtOverview.setText(movieEntity.getOverview());
                    txtRevenue.setText(Integer.toString(movieEntity.getReveneu()));
                    txtBudget.setText(Integer.toString(movieEntity.getBudget()));
                    txtRuntime.setText(Integer.toString(movieEntity.getRuntime()));
                    StringBuilder sb = new StringBuilder();
                    sb.append("\"");
                    sb.append(movieEntity.getTagline());
                    sb.append("\"");
                    txtTagLine.setText(sb.toString());
                    txtReleaseDate.setText(movieEntity.getReleaseDate().toString());

                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(movieEntity.getPosterUrl(), imgCover);
                    imageLoader.displayImage(movieEntity.getBackdropUrl(), imgBackDrop);

                    if (movieEntity.isAdult()) {
                        imgAdult.setVisibility(View.VISIBLE);
                    } else {
                        imgAdult.setVisibility(View.GONE);
                    }

                }
            });
        }
    }

}
