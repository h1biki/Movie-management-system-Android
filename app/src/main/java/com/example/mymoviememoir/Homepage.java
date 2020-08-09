package com.example.mymoviememoir;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mymoviememoir.DB.WatchListViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class Homepage extends Fragment {
    private View mainView;
    private TextView tv_welcome;
    private ListView movieList;
    private ArrayList<HashMap<String, String>> movieListArray = new ArrayList<>();
    public static WatchListViewModel watchListViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.homepage, container, false);
        movieList = mainView.findViewById(R.id.movieList);
        tv_welcome = mainView.findViewById(R.id.tv_welcome);
        new AsyncGetTop5().execute();
        return mainView;
    }



    public void onStart(){//running after onCreateView()
        super.onStart();
        SharedPreferences s = getActivity().getSharedPreferences(String.valueOf(LoginActivity.userid), Context.MODE_PRIVATE);
        String firstname = s.getString("firstname", "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        tv_welcome.setText("Welcome, " + firstname + "." + " Today is: " + time);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {//init DB in onActivityCreated is better
        super.onActivityCreated(savedInstanceState);
        watchListViewModel = new ViewModelProvider(this).get(WatchListViewModel.class);
        watchListViewModel.initializeVars(getActivity().getApplication());
    }


    public class AsyncGetTop5 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String textRecv = "";
            URL url = null;
            HttpURLConnection conn = null;
            final String restfulPath = "m3.memoir/findTop5/";
            try {
                url = new URL(LoginActivity.backendRootURL + restfulPath + LoginActivity.userid);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textRecv += inStream.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return textRecv;
        }

        @Override
        protected void onPostExecute(String textResult) {
            super.onPostExecute(textResult);//processing received data
            try {
                JSONArray ja = new JSONArray(textResult);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = (JSONObject) ja.get(i);
                    HashMap<String, String > hm = new HashMap<>();//HashMap: the first one is using for find the second one.
                    hm.put("Movie Name", jo.getString("Movie name"));//listview is consisted with multiple HashMap.
                    hm.put("Release Date", jo.getString("Release date"));
                    hm.put("Rating Score", jo.getString("Rating Score"));
                    movieListArray.add(hm);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
            String[] listHeader = new String[] {"Movie Name", "Release Date", "Rating Score"};//related to the code above for item allocation.
            int[] data = new int[] {R.id.tv_movieName_list, R.id.tv_releaseDate_list, R.id.tv_ratingScore_list};
            SimpleAdapter simpleMovieListAdapter = new SimpleAdapter(Homepage.this.getActivity(), movieListArray, R.layout.listview_movie, listHeader, data);//getActivity() for using in fragments
            movieList.setAdapter(simpleMovieListAdapter);
        }
    }
}
