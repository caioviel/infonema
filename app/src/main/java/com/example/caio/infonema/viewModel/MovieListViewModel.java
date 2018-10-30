package com.example.caio.infonema.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.caio.infonema.AppExecutors;
import com.example.caio.infonema.MovieListFragment;
import com.example.caio.infonema.database.AppDatabase;
import com.example.caio.infonema.database.MovieItem;
import com.example.caio.infonema.service.RetrofitSingletron;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieListViewModel extends AndroidViewModel implements DataRepository<LiveData<List<MovieItem>>> {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();


    AppDatabase mDb;
    private LiveData<List<MovieItem>> movies;
    private RetrofitSingletron retrofit;

    public MovieListViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getsInstance(this.getApplication());
        retrofit = RetrofitSingletron.getInstance();

    }


    public void runInMainThread(@NonNull Runnable runnable) {
        AppExecutors.getInstance().mainThread().execute(runnable);
    }


    @Override
    public void loadData(final LoadedDataListener<LiveData<List<MovieItem>>> listener) {

        Log.i(LOG_TAG, "loadData");

        Call<List<MovieItem>> call = retrofit.service().getMoviesList();
        call.enqueue(new Callback<List<MovieItem>>() {
            @Override
            public void onResponse(Call<List<MovieItem>> call, final Response<List<MovieItem>> response) {
                Log.i(LOG_TAG, "Movie List Obtained from API");

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.movieDAO().insertAllMovies(response.body());
                        movies = mDb.movieDAO().loadAllMovies();

                        runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.OnDataLoading(DataRepository.API_DATA, movies);
                            }
                        });

                    }
                });
            }

            @Override
            public void onFailure(Call<List<MovieItem>> call, Throwable t) {
                Log.e(LOG_TAG, "Error requesting Movie List");

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final int status;
                        int count = mDb.movieDAO().countMovies();
                        if (count == 0) {
                            Log.e(LOG_TAG, "Local movie cache is empty");
                            movies = null;
                            status = DataRepository.ERROR;
                        } else {
                            Log.e(LOG_TAG, "Using local movie cache");
                            movies = mDb.movieDAO().loadAllMovies();
                            status = DataRepository.DATABASE_DATA;
                        }

                        runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.OnDataLoading(status, movies);
                            }
                        });

                    }
                });

            }
        });

    }
}
