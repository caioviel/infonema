package com.example.caio.infonema.viewModel;

public interface DataRepository<T> {

    int API_DATA = 1;
    int DATABASE_DATA = 2;
    int ERROR = 3;

    void loadData(LoadedDataListener<T> listener);

    interface LoadedDataListener<T> {

        void OnDataLoading(int status, T data);

    }
}
