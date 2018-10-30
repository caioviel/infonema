package com.example.caio.infonema.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.caio.infonema.AppExecutors;
import com.example.caio.infonema.database.AppDatabase;
import com.example.caio.infonema.database.MovieEntity;
import com.example.caio.infonema.service.RetrofitSingletron;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailViewModel extends ViewModel implements DataRepository<LiveData<MovieEntity>> {

    private static final String LOG_TAG = DetailViewModel.class.getSimpleName();

    private AppDatabase mDb;
    private int mMovieId;
    private LiveData<MovieEntity> movie;
    private RetrofitSingletron retrofit;

    public DetailViewModel(AppDatabase db, int movieId) {
        Log.i(LOG_TAG, "DetailViewModel Constructor: " + Integer.toString(movieId));
        mDb = db;
        retrofit = RetrofitSingletron.getInstance();
    }

    public void runInMainThread(@NonNull Runnable runnable) {
        AppExecutors.getInstance().mainThread().execute(runnable);
    }

    public void setMovieId(int movieId) {
        mMovieId = movieId;
    }


    @Override
    public void loadData(final LoadedDataListener<LiveData<MovieEntity>> listener) {
        Log.i(LOG_TAG, "loadData: " + Integer.toString(mMovieId));

        Call<MovieEntity> call = retrofit.service().getMovieById(mMovieId);
        call.enqueue(new Callback<MovieEntity>() {
            @Override
            public void onResponse(Call<MovieEntity> call, final Response<MovieEntity> response) {
                Log.i(LOG_TAG, "Movie Detail Obtained from API");

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.movieDAO().insertMovie(response.body());
                        movie = mDb.movieDAO().loadMovieById(mMovieId);

                        runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.OnDataLoading(DataRepository.API_DATA, movie);
                            }
                        });

                    }
                });
            }

            @Override
            public void onFailure(Call<MovieEntity> call, Throwable t) {
                Log.e(LOG_TAG, "Error requesting Movie Detail");

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final int status;
                        movie = mDb.movieDAO().loadMovieById(mMovieId);
                        MovieEntity m = mDb.movieDAO().getMovieById(mMovieId);

                        Log.e(LOG_TAG, movie.toString());


                        if (m != null) {
                            Log.e(LOG_TAG, "There is a movie on local data");
                            Log.e(LOG_TAG, "Using local movie detail cache");
                            status = DataRepository.DATABASE_DATA;
                        } else {
                            Log.e(LOG_TAG, "Empty Info");
                            movie = null;
                            status = DataRepository.ERROR;
                        }

                        runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.OnDataLoading(status, movie);
                            }
                        });

                    }
                });

            }
        });


    }
}
