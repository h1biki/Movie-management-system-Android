package com.example.mymoviememoir.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WatchListDao {
    @Query("SELECT * FROM watchlist")
    List<WatchList> getAll();
    @Query("SELECT * FROM watchlist WHERE watchid = :wId LIMIT 1")
    WatchList findByID(int wId);
    @Insert
    void insertAll(WatchList... watchList);
    @Insert
    long insert(WatchList watchList);
    @Delete
    void delete(WatchList watchList);
    @Update(onConflict = REPLACE)
    void updateWatchList(WatchList... watchList);
    @Query("DELETE FROM WatchList")
    void deleteAll();



    @Query("select * from watchlist where userid = :userid")
    LiveData<List<WatchList>> findByUserid(String userid);

    @Query("select * from watchlist where userid = :userid")
    List<WatchList> findByUseridList(String userid);

    @Query("delete from watchlist where watchid = :watchid")
    void deleteByID(int watchid);






}
