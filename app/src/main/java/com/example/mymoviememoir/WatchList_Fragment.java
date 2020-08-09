package com.example.mymoviememoir;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.example.mymoviememoir.DB.WatchList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WatchList_Fragment extends Fragment {
    View watchListV;
    private List<HashMap<String, String>> watchListList = new ArrayList<>();
    private ListView watchList;
    private String movieID;
    private String wID;
    private TextView seletedmovie;
    private Button viewmoviewatchlist;
    private Button deletemoviewatchlist;
    private String movieName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        watchListV = inflater.inflate(R.layout.watch_list, container, false);

        watchList = watchListV.findViewById(R.id.watchlistlist);
        seletedmovie = watchListV.findViewById(R.id.selectedmovie);
        viewmoviewatchlist = watchListV.findViewById(R.id.viewmoviewatchlist);
        deletemoviewatchlist = watchListV.findViewById(R.id.deletemoviewatchlist);
        //the buttons or indicator shouldn't be displayed when no movie in watchlist selected
        viewmoviewatchlist.setVisibility(View.INVISIBLE);
        deletemoviewatchlist.setVisibility(View.INVISIBLE);
        seletedmovie.setVisibility(View.INVISIBLE);

        //db access
        Homepage.watchListViewModel.getAllWatchList(String.valueOf(LoginActivity.userid)).observe(getViewLifecycleOwner(), new Observer<List<WatchList>>() {
            @Override
            public void onChanged(List<WatchList> watchLists) {
                watchListList.clear();
                for (WatchList temp : watchLists) {
                    HashMap<String, String> map = new HashMap<String, String >();
                    map.put("movie name", temp.getMovieName());
                    map.put("watch id", String.valueOf(temp.getWatchid()));
                    map.put("movie id", temp.getMovieid());
                    map.put("release date", temp.getReleaseDate());
                    map.put("add date", temp.getAddDate());
                    map.put("add time", temp.getAddTime());
                    watchListList.add(map);
                }
                String[] header = new String[]{"movie name", "watch id", "movie id", "release date", "add date", "add time"};
                int[] data = new int[]{R.id.tv_movieName_watchlist, R.id.tv_watchid_watchlist, R.id.tv_movieid_watchlist, R.id.tv_releaseDate_watchlist, R.id.tv_adddate_watchlist, R.id.tv_addtime_watchlist};
                SimpleAdapter simpleAdapter = new SimpleAdapter(WatchList_Fragment.this.getActivity(), watchListList, R.layout.listview_watchlist, header, data);
                watchList.setAdapter(simpleAdapter);
            }
        });

        watchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                movieID = watchListList.get(position).get("movie id");
                wID = watchListList.get(position).get("watch id");
                movieName = watchListList.get(position).get("movie name");
                seletedmovie.setText(movieName + " has been seleted");
                viewmoviewatchlist.setVisibility(View.VISIBLE);
                deletemoviewatchlist.setVisibility(View.VISIBLE);
                seletedmovie.setVisibility(View.VISIBLE);
            }
        });

        viewmoviewatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MovieView();

                Bundle args = new Bundle();
                args.putString("mId", movieID);
                args.putString("mName", movieName);
                args.putString("flag", "true");
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        deletemoviewatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(WatchList_Fragment.this.getActivity());
                alert.setTitle("Confirm Delete Movie?");
                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Homepage.watchListViewModel.deleteById(Integer.parseInt(wID));
                        viewmoviewatchlist.setVisibility(View.INVISIBLE);
                        deletemoviewatchlist.setVisibility(View.INVISIBLE);
                        seletedmovie.setVisibility(View.INVISIBLE);
                        Toast.makeText(watchListV.getContext(), "DELETED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();
            }
        });
        return watchListV;
    }
}








