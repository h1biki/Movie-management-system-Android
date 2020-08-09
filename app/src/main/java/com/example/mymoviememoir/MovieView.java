package com.example.mymoviememoir;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.mymoviememoir.DB.WatchList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MovieView extends Fragment {
    View movieView;
    private TextView movieName;
    private TextView movieGenre;
    private TextView movieReleaseDate;
    private TextView movieCountry;
    private TextView movieOverview;
    private TextView movieRating;
    private String mId;
    private String releaseDate;
    private List<HashMap<String, String>> castListArray = new ArrayList<>();
    private List<HashMap<String, String>> directorListArray = new ArrayList<>();
    private Button addWatchList;
    private Button addMemoir;
    private ListView castList;
    private ListView directorList;
    private RatingBar ratingBar;
    private int watchid;
    private String imgURL;
    private String detailResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        movieView = inflater.inflate(R.layout.movieview, container, false);

        movieName = movieView.findViewById(R.id.movieName);
        movieGenre = movieView.findViewById(R.id.movieGenre);
        movieReleaseDate = movieView.findViewById(R.id.movieReleaseDate);
        movieCountry = movieView.findViewById(R.id.movieCountry);
        movieOverview = movieView.findViewById(R.id.movieOverview);
        movieRating = movieView.findViewById(R.id.rating);
        addWatchList = movieView.findViewById(R.id.addToWatchList);
        addMemoir = movieView.findViewById(R.id.addToMemoir);
        castList = movieView.findViewById(R.id.castList);
        directorList = movieView.findViewById(R.id.directorList);

        if (getArguments() != null) {
            mId = getArguments().getString("mId");
            String mName = getArguments().getString("mName");
            if(getArguments().getString("flag").equals("true")) {
                addWatchList.setVisibility(View.INVISIBLE);
            }
            movieName.setText(mName);
            new detailAsyncTask().execute(mId);

            addWatchList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new isExist().execute(String.valueOf(LoginActivity.userid));
                }
            });
        }

        addMemoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddMemoir();
                Bundle args = new Bundle();
                args.putString("movieName", movieName.getText().toString());
                args.putString("releaseDate", releaseDate);
                args.putString("imgURL", imgURL);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });
        return movieView;
    }

    @SuppressLint("StaticFieldLeak")
    private class isExist extends AsyncTask<String, Void, List<WatchList>> {

        @Override
        protected List<WatchList> doInBackground(String... uid) {
            return Homepage.watchListViewModel.getWatchListByID(uid[0]);
        }

        @Override
        protected void onPostExecute(List<WatchList> watchList) {
            boolean flag = true;
            for(WatchList wl :watchList) {
                if(wl.getMovieName().equals(movieName.getText().toString()) && wl.getReleaseDate().equals(releaseDate)) {
                    Toast toast = Toast.makeText(movieView.getContext(), "Movie Exist", Toast.LENGTH_SHORT);
                    toast.show();
                    flag = false;
                    break;
                }
            }
            if(flag) {
                Date date = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String addDate = dateFormat.format(date.getTime());
                String addTime = timeFormat.format(date.getTime());
                WatchList watchList1 = new WatchList(movieName.getText().toString(), releaseDate, addDate, addTime, String.valueOf(LoginActivity.userid), mId);
                Homepage.watchListViewModel.insert(watchList1);
                Toast.makeText(movieView.getContext(), "Movie Added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class detailAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return TmdbAPI.getMovieData(params[0]);
        }
        protected void onPostExecute(String result) {
            detailResult = result;
            new detailAndcastAndDirectorAsyncTask().execute(mId);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class detailAndcastAndDirectorAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return TmdbAPI.getMovieCast(params[0]);
        }
        protected void onPostExecute(String result) {
            HashMap<String, Object> resultHm = TmdbAPI.processMovieJSON(detailResult, result);
            releaseDate = Objects.requireNonNull(resultHm.get("release date")).toString();

            movieReleaseDate.setText(Objects.requireNonNull(resultHm.get("release date")).toString());
            movieGenre.setText(Objects.requireNonNull(resultHm.get("genre")).toString());
            movieCountry.setText(Objects.requireNonNull(resultHm.get("country")).toString());
            movieOverview.setText(Objects.requireNonNull(resultHm.get("overview")).toString());
            imgURL = Objects.requireNonNull(resultHm.get("imgURL")).toString();


            Object object = resultHm.get("castList");
            List<String> castList1 = (List<String>) object;
            assert castList1 != null;
            for(int i = 0; i < castList1.size(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("castList", castList1.get(i));
                castListArray.add(hashMap);
            }
            String[] header = new String[] {"castList"};
            int[] data = new int[] {R.id.castname};
            SimpleAdapter simpleAdapterCast = new SimpleAdapter(MovieView.this.getActivity(), castListArray, R.layout.cast, header, data);
            castList.setAdapter(simpleAdapterCast);


            Object object1 = resultHm.get("directorList");
            List<String> directorList1 = (List<String>) object1;
            assert directorList1 != null;
            for(int i = 0; i < directorList1.size(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("directorList", directorList1.get(i));
                directorListArray.add(hashMap);
            }
            String[] header1 = new String[] {"directorList"};
            int[] data1 = new int[] {R.id.directorname};
            SimpleAdapter simpleAdapterDirector = new SimpleAdapter(MovieView.this.getActivity(), directorListArray, R.layout.director, header1, data1);
            directorList.setAdapter(simpleAdapterDirector);
        }
    }



}
