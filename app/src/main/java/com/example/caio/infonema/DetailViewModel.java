package com.example.caio.infonema;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.caio.infonema.database.AppDatabase;
import com.example.caio.infonema.database.MovieEntity;


public class DetailViewModel extends ViewModel {

    private static final String LOG_TAG = DetailViewModel.class.getSimpleName();

    private LiveData<MovieEntity> movie;


    public DetailViewModel(AppDatabase db, int mMovieId) {
        movie = db.movieDAO().loadTaskById(mMovieId);
    }


    public LiveData<MovieEntity> getMovie() {
        return movie;
    }
}
