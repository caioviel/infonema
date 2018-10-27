package com.example.caio.infonema;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.caio.infonema.database.AppDatabase;
import com.example.caio.infonema.database.MovieEntity;

import java.util.List;


public class DetailViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private LiveData<MovieEntity> movie;
    

    public DetailViewModel(AppDatabase mDb, int mMovieId) {
        getFromWeb(mMovieId);
        movie = mDb.movieDAO().loadTaskById(mMovieId);
    }

    private boolean getFromWeb(int mMovieId) {

    }

    public LiveData<MovieEntity> getMovie() {
        return movie;
    }
}
