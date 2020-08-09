package com.example.mymoviememoir;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MovieSearch extends Fragment {

    private View vMovieSearch;
    private Button tmdbbutton;
    private EditText searchBar;
    private String movieSearchKeyword;
    private ArrayList<String> resultList = new ArrayList<>();
    private ListView searchListview;
    private ArrayList<HashMap<String, Object>> searchListArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMovieSearch = inflater.inflate(R.layout.movie_search, container, false);
        searchBar = vMovieSearch.findViewById(R.id.searchBar);
        searchListview = vMovieSearch.findViewById(R.id.SearchListView);
        tmdbbutton = vMovieSearch.findViewById(R.id.tmdbbutton);
        tmdbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieSearchKeyword = searchBar.getText().toString();
                new SearchAsyncTask().execute(movieSearchKeyword);
            }
        });
        return vMovieSearch;
    }

    @SuppressLint("StaticFieldLeak")
    public class SearchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return TmdbAPI.searchTMDB(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            new displaySearchResultListAsync().execute(result);
        }//data loading from TMDB consuming time...use async
    }

    @SuppressLint("StaticFieldLeak")
    private class displaySearchResultListAsync extends AsyncTask<String, Void, SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(String... param) {
            resultList = TmdbAPI.getNeededData(param[0]);
            for (int i = 0; i < resultList.size(); i += 4) {// i += 4 means one movie has 4 continuous attributes received from TMDB API, then next movie
                HashMap<String, Object> map = new HashMap<>();//Object can storage all types of object
                map.put("Movie Name", resultList.get(i));//sequence follows the insert sequence in TMDB_API.java
                map.put("Release Date", resultList.get(i + 1));
                String coverUrl = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/" + resultList.get(i + 2);//the api returns a rel path of img
                map.put("movieID", resultList.get(i + 3));
                Bitmap bm;
                try {
                    URL url = new URL(coverUrl);
                    InputStream inputStream = url.openStream();
                    bm = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    map.put("cover", bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                searchListArray.add(map);
            }
            String[] header = new String[] {"cover", "Movie Name", "Release Date", "movieID"};
            int[] data = new int[] {R.id.iv_moviePoster, R.id.tv_searchMovieName_list, R.id.tv_searchReleaseDate_list, R.id.tv_searchMovieID_list};
            SimpleAdapter simpleMovieListAdapter = new SimpleAdapter(MovieSearch.this.getActivity(), searchListArray, R.layout.listview_search, header, data);//getActivity() for using in fragments
            simpleMovieListAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if ((view instanceof ImageView) & (data instanceof Bitmap)) {
                        ImageView iv = (ImageView) view;
                        Bitmap bm = (Bitmap) data;
                        iv.setImageBitmap(bm);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            return simpleMovieListAdapter;//to onPost
        }

        @Override
        protected void onPostExecute(SimpleAdapter simpleAdapter) {
            searchListview.setAdapter(simpleAdapter);
            searchListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {// button->onClick
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String movieID = Objects.requireNonNull(searchListArray.get(position).get("movieID")).toString(); //using toString because it's Object type
                    String movieName = Objects.requireNonNull(searchListArray.get(position).get("Movie Name")).toString();
                    String releaseDate = Objects.requireNonNull(searchListArray.get(position).get("Release Date")).toString();
                    Fragment movieView = new MovieView();
                    Bundle args = new Bundle();//passing data to next fragment
                    args.putString("flag", "false");//when a movie added to the watchlist, then the add button cannot be displayed anymore
                    args.putString("mId", movieID);
                    args.putString("mName", movieName);
                    args.putString("rDate", releaseDate);
                    movieView.setArguments(args);
                    FragmentManager fm = getFragmentManager();
                    assert fm != null;
                    fm.beginTransaction().replace(R.id.content_frame, movieView).commit();//passing info to movieView
                }
            });
        }



    }











    }
