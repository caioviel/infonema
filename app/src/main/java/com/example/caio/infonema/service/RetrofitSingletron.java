package com.example.caio.infonema.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingletron {

    private Retrofit retrofit;
    private TMDBService service;

    private static final Object LOCK = new Object();
    private static RetrofitSingletron sInstance;

    private RetrofitSingletron() {
        retrofit = new Retrofit.Builder()
                .baseUrl(TMDBService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(TMDBService.class);
    }

    public TMDBService service() {
        return service;
    }

    public static RetrofitSingletron getInstance() {

        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new RetrofitSingletron();
            }
        }
        return sInstance;
    }
}
