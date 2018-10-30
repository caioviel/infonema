package com.example.caio.infonema;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.caio.infonema.database.MovieItem;
import com.example.caio.infonema.viewModel.DataRepository;
import com.example.caio.infonema.viewModel.MovieListViewModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MovieListFragment.OnListFragmentInteractionListener,
        SwipeRefreshLayout.OnRefreshListener,
        DataRepository.LoadedDataListener<LiveData<List<MovieItem>>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ImageView imgLoading;
    private LinearLayout layoutList, layoutEmpty;

    private DetailFragment detailFragment;
    private FragmentManager fragmentManager;
    private MovieListFragment movieListFragment;
    private MovieListViewModel viewModel;

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
                .showImageOnFail(R.drawable.img_not_found)
                .showImageOnLoading(R.drawable.progress_animation)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(config);


        imgLoading = findViewById(R.id.img_loading);
        layoutList = findViewById(R.id.layout_list);
        layoutEmpty = findViewById(R.id.layout_noview);

        fragmentManager = getSupportFragmentManager();
        detailFragment = (DetailFragment) fragmentManager.findFragmentById(R.id.detail_list_fragment);
        movieListFragment = (MovieListFragment) fragmentManager.findFragmentById(R.id.movie_list_fragment);

        viewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);

        fetchList();
    }


    @Override
    public void onListFragmentInteraction(MovieItem item) {
        if (detailFragment == null) {
            Log.i(LOG_TAG, "Calling Detail Activity");
            Intent detailIntent = new Intent(this, DetailActivity.class);
            Log.i(LOG_TAG, "MovieID: " + Integer.toString(item.getId()));
            detailIntent.putExtra("MOVIE_ID", item.getId());
            startActivity(detailIntent);
        } else {
            Log.i(LOG_TAG, "Updating Detail Fragment");
            detailFragment.setMovieId(item.getId());

        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void OnClickBtnRefresh(View view) {
        imgLoading.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        layoutList.setVisibility(View.GONE);
        fetchList();
    }

    private void fetchList() {
        viewModel.loadData(this);
    }

    @Override
    public void onRefresh() {
        fetchList();
    }

    @Override
    public void OnDataLoading(int status, LiveData<List<MovieItem>> data) {
        movieListFragment.setRefreshing(false);

        switch (status) {
            case DataRepository.DATABASE_DATA:
                Toast.makeText(MainActivity.this, R.string.cache_info, Toast.LENGTH_LONG).show();
            case DataRepository.API_DATA:

                imgLoading.setVisibility(View.GONE);
                layoutList.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);

                data.observe(this, new Observer<List<MovieItem>>() {
                    @Override
                    public void onChanged(@Nullable List<MovieItem> movieEntities) {
                        movieListFragment.setMovies(movieEntities);
                        if (!movieEntities.isEmpty() && detailFragment != null) {
                            detailFragment.setMovieId(movieEntities.get(0).getId());
                        }
                    }
                });

                break;
            case DataRepository.ERROR:
                imgLoading.setVisibility(View.GONE);
                layoutList.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                break;
        }

    }
}
