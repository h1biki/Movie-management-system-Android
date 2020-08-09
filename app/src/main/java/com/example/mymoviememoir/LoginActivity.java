package com.example.mymoviememoir;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {
    public static List<HashMap<String, String >> cinemaData;
    private EditText et_UserName;
    private EditText et_PassWord;
    public static int userid;
    public static Person person = new Person();
    public static String lat;
    public static String lng;
    public static List<String> cinemaNamesList = new ArrayList<>();
    public static final String backendRootURL = "http://192.168.1.5:8080/m3/webresources/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button loginBtn = findViewById(R.id.b_Login);
        TextView tv_signup = findViewById(R.id.tv_signup);
        et_UserName = findViewById(R.id.et_UserName);
        et_PassWord = findViewById(R.id.et_PassWord);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncLogin().execute(et_UserName.getText().toString(), et_PassWord.getText().toString());//strings[0], strings[1]
                //to prevent waiting too long to crash, using asynctask
            }
        });

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);//jump to signup
            }
        });


    }

    private class AsyncLogin extends AsyncTask<String, Void, String> {//android.os.AsyncTask<Params, Progress, Result>

        @Override
        protected String doInBackground(String... strings) {
            String textResult = "";
            String username = strings[0];
            String password = strings[1];
            String md5password = MD5.md5(password);
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "m3.credential/findByUsernameAndPassword/";
            try {
                url = new URL(backendRootURL + methodPath + username + "/" + md5password);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            } finally {
                conn.disconnect();
            }
            try {
                String temp = textResult.replace("[", "").replace("]", "");
                JSONObject jo1 = new JSONObject(temp);
                String tempUserid = jo1.getString("userid");
                JSONObject jo2 = new JSONObject(tempUserid);
                userid = Integer.parseInt(jo2.getString("userid"));
                //SharedPreferences
                String useridStr = jo2.getString("userid");
                SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(useridStr, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("firstname", jo2.getString("firstname"));
                editor.putString("email", username);
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return textResult;
        }

        protected void onPostExecute(String textResult) {//pass textResult in, checking login credentials
            super.onPostExecute(textResult);
            if (textResult.equals("[]")) {//empty return from backend means not found user credentials
                Toast.makeText(LoginActivity.this, "Username or Password wrong, or backend error", Toast.LENGTH_SHORT).show();
            } else {
                new AsyncGetPersonInfo().execute(userid);// all green and jump to Homepage
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private class AsyncGetPersonInfo extends AsyncTask<Integer, Void, Person> {
        @SuppressLint("WrongThread")
        @Override
        protected Person doInBackground(Integer... userid) {
            String personAddress = "";
            String textResult = "";
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "m3.credential/findByUserid/" + userid[0];
            try {
                url = new URL(backendRootURL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            try {
                JSONArray ja = new JSONArray(textResult);
                JSONObject jo = (JSONObject) ja.get(0);
                JSONObject jo2 = new JSONObject(jo.getString("userid"));
                person.setUserid(jo2.getString("userid"));
                person.setFirstname(jo2.getString("firstname"));
                person.setSurname(jo2.getString("surname"));
                person.setGender(jo2.getString("gender"));
                person.setDob(jo2.getString("dob"));
                person.setAddress(jo2.getString("address"));
                person.setPostcode(jo2.getString("postcode"));
                person.setState(jo2.getString("state"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new MapSearchAsyncTask().execute(person.getAddress());
            return null;
        }
    }

    private class MapSearchAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return GoogleMapAPI.search(strings[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject jo1 = new JSONObject(result);
                String a = jo1.getString("candidates");
                JSONArray ja1 = new JSONArray(a);
                JSONObject jo2 = (JSONObject) ja1.get(0);
                String b = jo2.getString("geometry");
                JSONObject jo3 = new JSONObject(b);
                String c = jo3.getString("location");
                JSONObject jo4 = new JSONObject(c);
                lat = jo4.getString("lat");
                lng = jo4.getString("lng");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new AsyncGetCinema().execute();
        }
    }

    private class AsyncGetCinema extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String textResult = "";

            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "m3.cinema";
            try {
                url = new URL(backendRootURL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            try {
                JSONArray ja = new JSONArray(textResult);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = (JSONObject) ja.get(i);
                    cinemaNamesList.add(jo.getString("cinemaname"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SearchCinemaInGoogleAPI().execute();
            return null;
        }
    }

    private class SearchCinemaInGoogleAPI extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return GoogleMapAPI.searchCinema(cinemaNamesList);
        }
    }
}
