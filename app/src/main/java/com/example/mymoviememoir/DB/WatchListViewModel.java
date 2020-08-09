package com.example.mymoviememoir.DB;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class WatchListViewModel extends ViewModel {
    private WatchListRepository watchListRepository;
    private MutableLiveData<List<WatchList>> allWatchList;

    public WatchListViewModel() {
        allWatchList = new MutableLiveData<>();
    }

    public LiveData<List<WatchList>> getAllWatchList(String uID) {
        return watchListRepository.getAllWatchList(uID);
    }

    public List<WatchList> getWatchListByID(String uID) {
        return watchListRepository.getAllWatchListByID(uID);
    }

    public void initializeVars(Application application) {
        watchListRepository = new WatchListRepository(application);
    }

    public void insert(WatchList watchList) {
        watchListRepository.insert(watchList);
    }

    public void insertAll(WatchList watchList) {
        watchListRepository.insertAll(watchList);
    }

    public void deleteAll() {
        watchListRepository.deleteAll();
    }

    public void delete(WatchList watchList) {
        watchListRepository.delete(watchList);
    }

    public void update(WatchList watchList) {
        watchListRepository.updateWatchList(watchList);
    }

    public WatchList insertAll(int id) {
        return watchListRepository.findByID(id);
    }

    public WatchList findByID(int watchListID) {
        return watchListRepository.findByID(watchListID);
    }

    public void deleteById(int watchListID) {
        watchListRepository.deleteByID(watchListID);
    }
}
