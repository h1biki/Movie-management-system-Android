package com.example.mymoviememoir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TmdbAPI {
    private static String apikey = "";

    public static String searchTMDB(String keyword) {
        keyword = keyword.trim().replace(" ", "%20");//transform the space in the middle for url.
        URL url = null;
        HttpURLConnection conn = null;
        String textRecv = "";
        try {
                url = new URL("https://api.themoviedb.org/3/search/movie?api_key=" + apikey + "&language=en-US&query=" + keyword + "&page=1&include_adult=false");
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);//https://api.themoviedb.org/3/search/movie?api_key=&language=en-US&query=Superman&page=1&include_adult=false
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNextLine()) {
                    textRecv += scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textRecv;
    }

    public static ArrayList<String> getNeededData(String result) {
        ArrayList<String> neededData = new ArrayList<>();
        try {
            JSONObject jo = new JSONObject(result);
            JSONArray ja = new JSONArray(jo.getString("results"));
            for (int i = 0; i < ja.length(); i++) {
                JSONObject temp = (JSONObject) ja.get(i);
                neededData.add(temp.getString("title"));
                neededData.add(temp.getString("release_date"));
                neededData.add(temp.getString("poster_path"));
                neededData.add(temp.getString("id"));
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return neededData;
    }

    public static String getMovieData(String movieID) {
        URL url = null;
        HttpURLConnection conn = null;
        String textRecv = "";
        try {
            url = new URL("https://api.themoviedb.org/3/movie/" + movieID + "?api_key=" + apikey + "&language=en-US");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNextLine()) {
                textRecv += scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textRecv;
    }

    public static String getMovieCast(String movieID) {
        URL url = null;
        HttpURLConnection conn = null;
        String textRecv = "";
        try {
            url = new URL("https://api.themoviedb.org/3/movie/" + movieID + "/credits?api_key=" + apikey);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNextLine()) {
                textRecv += scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textRecv;
    }

    public static HashMap<String, Object> processMovieJSON(String detail, String cast) {
        HashMap<String, Object> neededData = new HashMap<>();
        try {
            JSONObject jo1 = new JSONObject(detail);
            neededData.put("release date", jo1.get("release_date"));
            neededData.put("overview", jo1.get("overview"));
            neededData.put("rate score", jo1.get("vote_average"));
            neededData.put("imgURL", jo1.get("poster_path"));

            JSONArray ja1 = jo1.getJSONArray("genres");
            JSONObject jo2 = ja1.getJSONObject(0);//in case of multiple genres
            neededData.put("genre", jo2.get("name"));

            JSONArray ja2 = jo1.getJSONArray("production_countries");
            JSONObject jo3 = ja2.getJSONObject(0);//in case of multiple countries
            neededData.put("country", jo3.get("name"));

            JSONObject jo4 = new JSONObject(cast);
            JSONArray ja3 = jo4.getJSONArray("cast");
            List<String> castList = new ArrayList<>();
            if(ja3.length() < 5) {//in case of the movie has less than 5 casts, to prevent error
                for(int i = 0; i < ja3.length(); i++) {
                    JSONObject tempJo = ja3.getJSONObject(i);
                    castList.add(tempJo.getString("name"));
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    JSONObject tempJo = ja3.getJSONObject(i);
                    castList.add(tempJo.getString("name"));
                }
            }
            neededData.put("castList", castList);

            JSONArray ja4 = jo4.getJSONArray("crew");
            List<String> directorList = new ArrayList<>();
            for (int i = 0; i < ja4.length(); i++) {
                JSONObject tempJo = ja4.getJSONObject(i);
                if(tempJo.getString("job").equals("Director")) {
                    directorList.add(tempJo.getString("name"));
                }
            }
            neededData.put("directorList", directorList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return neededData;
    }
}
