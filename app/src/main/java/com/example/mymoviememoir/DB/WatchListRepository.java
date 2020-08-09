package com.example.mymoviememoir.DB;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class WatchListRepository {
    private WatchListDao dao;
    private LiveData<List<WatchList>> allWatchList;
    private WatchList watchList;
    private List<WatchList> watchLists;

    public WatchListRepository(Application application) {
        WatchListDatabase db = WatchListDatabase.getInstance(application);
        dao = db.watchListDao();
    }

    public LiveData<List<WatchList>> getAllWatchList(String uId) {
        allWatchList = dao.findByUserid(uId);
        return allWatchList;
    }

    public List<WatchList> getAllWatchListByID(String uId) {
        watchLists = dao.findByUseridList(uId);
        return watchLists;
    }

    public void insert(final WatchList watchList) {
        WatchListDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insert(watchList);
            }
        });
    }

    public void deleteAll() {
        WatchListDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteAll();
            }
        });
    }

    public void delete(final WatchList watchList) {
        WatchListDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(watchList);
            }
        });
    }

    public void deleteByID(final int watchID) {
        WatchListDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteByID(watchID);
            }
        });
    }

    public void insertAll(final WatchList... watchList) {
        WatchListDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insertAll(watchList);
            }
        });
    }

    public void updateWatchList(final WatchList... watchList) {
        WatchListDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.updateWatchList(watchList);
            }
        });
    }

    public WatchList findByID(final int watchListId) {
        WatchListDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                WatchList runWatchList = dao.findByID(watchListId);
                setWatchList(runWatchList);
            }
        });
        return watchList;
    }

    public void setWatchList(WatchList watchList){
        this.watchList=watchList;
    }








}
