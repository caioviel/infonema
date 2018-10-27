package com.example.caio.infonema.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM movie ORDER BY title")
    LiveData<List<MovieEntity>> loadAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieEntity movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMovies(List<MovieEntity> movie);

    @Delete
    void deleteMovie(MovieEntity movie);

    @Query("SELECT * FROM movie WHERE id = :id")
    LiveData<MovieEntity> loadTaskById(int id);
}
