package com.example.caio.infonema;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.caio.infonema.database.AppDatabase;
import com.example.caio.infonema.database.MovieEntity;
import com.example.caio.infonema.service.TMDBService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieListFragment.OnListFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private AppDatabase mDb;

    private ImageView imgLoading;
    private LinearLayout layoutList;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .resetViewBeforeLoading(true)
                //.showImageForEmptyUri()
                //.showImageOnFail()
                .showImageOnLoading(R.drawable.progress_animation)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(config);


        mDb = AppDatabase.getsInstance(this);

        imgLoading = findViewById(R.id.img_loading);
        layoutList = findViewById(R.id.layout_list);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchList();
    }

    public void fetchList() {
        Call<List<MovieEntity>> myCall = service.getMoviesList();

        myCall.enqueue(new Callback<List<MovieEntity>>() {
            @Override
            public void onResponse(Call<List<MovieEntity>> call, final Response<List<MovieEntity>> response) {
                Log.i(LOG_TAG, "Movie List Obtained");
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.movieDAO().insertAllMovies(response.body());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgLoading.setVisibility(View.GONE);
                                layoutList.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<List<MovieEntity>> call, Throwable t) {
                Log.e(LOG_TAG, "Error requesting Movie List");
            }
        });

    }


    @Override
    public void onListFragmentInteraction(MovieEntity item) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
