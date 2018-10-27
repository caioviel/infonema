package com.example.caio.infonema.service;

import com.example.caio.infonema.database.MovieEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TMDBService {

    public static final String BASE_URL = "https://desafio-mobile.nyc3.digitaloceanspaces.com";

    @GET("movies")
    Call<List<MovieEntity>> getMoviesList();

    @GET("movies/{id}")
    Call<MovieEntity> getMovieById(@Path("id") int id);
}
