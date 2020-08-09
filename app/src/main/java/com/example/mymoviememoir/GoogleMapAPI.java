package com.example.mymoviememoir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class GoogleMapAPI {
    private static final String googleApi_key = "";

    public static String search(String Address) {
        URL url = null;
        HttpURLConnection conn = null;
        String textRecv = "";
        try {
            StringBuffer stringBuffer = new StringBuffer("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=");
            String wholeUrl = stringBuffer.append(Address).append("&inputtype=textquery&fields=name,geometry&key=").append(googleApi_key).toString();
            url = new URL(wholeUrl);
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

    public static String searchCinema(List<String> cinemaNamesList) {
        //LoginActivity.cinemaData.clear();
        for (int i = 0; i < cinemaNamesList.size(); i++) {
            URL url = null;
            HttpURLConnection conn = null;
            String textRecv = "";
            try {
                StringBuffer stringBuffer = new StringBuffer("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=");
                String wholeUrl = stringBuffer.append(cinemaNamesList.get(i)).append("&inputtype=textquery&fields=name,geometry&key=").append(googleApi_key).toString();
                url = new URL(wholeUrl);
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
            try {
                JSONObject jo1 = new JSONObject(textRecv);
                String a = jo1.getString("candidates");
                JSONArray ja1 = new JSONArray(a);
                JSONObject jo2 = (JSONObject) ja1.get(0);
                String b = jo2.getString("geometry");
                JSONObject jo3 = new JSONObject(b);
                String c = jo3.getString("location");
                JSONObject jo4 = new JSONObject(c);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("lat", jo4.getString("lat"));
                hashMap.put("lng", jo4.getString("lng"));
//                LoginActivity.cinemaData.add(hashMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}

