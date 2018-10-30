package com.example.caio.infonema.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM movie_item ORDER BY title")
    LiveData<List<MovieItem>> loadAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieEntity movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMovies(List<MovieItem> movie);

    @Update
    void updateMovie(MovieEntity movie);

    @Delete
    void deleteMovie(MovieEntity movie);

    @Query("SELECT count(*) FROM movie_item")
    int countMovies();

    @Query("SELECT * FROM movie WHERE id = :id")
    LiveData<MovieEntity> loadMovieById(int id);

    @Query("SELECT * FROM movie WHERE id = :id")
    MovieEntity getMovieById(int id);
}
