package com.example.caio.infonema;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.caio.infonema.database.AppDatabase;
import com.example.caio.infonema.database.MovieEntity;
import com.example.caio.infonema.service.RetrofitSingletron;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieListFragment.OnListFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private AppDatabase mDb;

    private ImageView imgLoading;
    private LinearLayout layoutList;

    private DetailFragment detailFragment;
    private FragmentManager fragmentManager;

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

        fragmentManager = getSupportFragmentManager();
        detailFragment = (DetailFragment) fragmentManager.findFragmentById(R.id.detail_list_fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchList();
    }

    public void fetchList() {
        RetrofitSingletron retrofit = RetrofitSingletron.getInstance();
        Call<List<MovieEntity>> call = retrofit.service().getMoviesList();

        call.enqueue(new Callback<List<MovieEntity>>() {
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
                                if (imgLoading != null) {
                                    imgLoading.setVisibility(View.GONE);
                                    layoutList.setVisibility(View.VISIBLE);
                                }
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
        if (detailFragment == null) {
            Log.i(LOG_TAG, "Calling Detail Activity");
            Intent detailIntent = new Intent(this, DetailActivity.class);
            Log.i(LOG_TAG, "MovieID: " + Integer.toString(item.getId()));
            detailIntent.putExtra("MOVIE_ID", item.getId());
            startActivity(detailIntent);
        } else {
            Log.i(LOG_TAG, "Updating Detail Fragment");

            Bundle args = new Bundle();
            args.putInt("MOVIE_ID", item.getId());
            detailFragment.setArguments(args);
            FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
            fragTransaction.detach(detailFragment).attach(detailFragment).commit();
            Log.i(LOG_TAG, "After Commit");

        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
