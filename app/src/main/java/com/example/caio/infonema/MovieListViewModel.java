package com.example.caio.infonema;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.caio.infonema.database.AppDatabase;
import com.example.caio.infonema.database.MovieEntity;

import java.util.List;


public class MovieListViewModel extends AndroidViewModel {

    private LiveData<List<MovieEntity>> movies;


    public MovieListViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        movies = database.movieDAO().loadAllMovies();
    }

    public LiveData<List<MovieEntity>> getMovies() {
        return movies;
    }
}
