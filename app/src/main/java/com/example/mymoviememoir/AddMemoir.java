package com.example.mymoviememoir;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class AddMemoir extends Fragment {
    private View addMemoirV;
    private ImageView poster;
    private String imgPath;
    private EditText editComment;
    private TextView movieName;
    private TextView movieReleasedate;
    private Button addDateBtn;
    private Button addTimeBtn;
    private TextView pickedDate;
    private TextView pickedTime;
    private Button addCinema;
    private Button addMemoirBtn;
    private Spinner cinemaSpinner;
    private List<String> cinemaList = new ArrayList<>();
    private List<Cinema> cinemas = new ArrayList<>();
    private Cinema cinema;
    private String addDate;
    private String addTime;
    private Person person = new Person();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addMemoirV = inflater.inflate(R.layout.addmemoir, container, false);
        poster = addMemoirV.findViewById(R.id.moviePosterMemoir);
        editComment = addMemoirV.findViewById(R.id.commentMemoir);
        movieName = addMemoirV.findViewById(R.id.movieNameMemoir);
        movieReleasedate = addMemoirV.findViewById(R.id.movieReleaseDateMemoir);
        addDateBtn = addMemoirV.findViewById(R.id.pickdatememoirbtn);
        addTimeBtn = addMemoirV.findViewById(R.id.picktimememoirbtn);
        pickedDate = addMemoirV.findViewById(R.id.pickedDate);
        pickedTime = addMemoirV.findViewById(R.id.pickedTime);
        addCinema = addMemoirV.findViewById(R.id.addnewcinemamemoirbtn);
        cinemaSpinner = addMemoirV.findViewById(R.id.cinemaSpinnerMemoir);
        addMemoirBtn = addMemoirV.findViewById(R.id.addmemoirbtn);

        final TextView ratingMemoir = addMemoirV.findViewById(R.id.ratingMemoir);
        final SimpleRatingBar simpleRatingBar = addMemoirV.findViewById(R.id.ratingbarmemoir);

        if(getArguments() != null) {
            String mName = getArguments().getString("movieName");
            String rDate = getArguments().getString("releaseDate");
            movieName.setText(mName);
            movieReleasedate.setText(rDate);
            imgPath = getArguments().getString("imgURL");
            String imgURL = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/" + getArguments().getString("imgURL");
            new accessImage().execute(imgURL);
        }
        new accessCinema().execute();

        addDateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int myear = calendar.get(Calendar.YEAR);
                int mmonthOfYear = calendar.get(Calendar.MONTH);
                final int mdayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int mmo = mmonthOfYear + 1;
                String mm;
                if (mmo < 10) {
                    StringBuffer str = new StringBuffer("0");
                    mm = str.append(mmo).toString();
                } else {
                    mm = String.valueOf(mmo);
                }
                String dd = "";
                if (mdayOfMonth < 10) {
                    StringBuffer str = new StringBuffer("0");
                    dd = str.append(mdayOfMonth).toString();
                } else {
                    dd = String.valueOf(mdayOfMonth);
                }
                DatePickerDialog datePicker = new DatePickerDialog(addMemoirV.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        String mon = "";
                        int month = monthOfYear + 1;
                        if (month < 10) {
                            StringBuffer str = new StringBuffer("0");
                            mon = str.append(month).toString();
                        } else {
                            mon = String.valueOf(month);
                        }
                        String day = "";
                        if (dayOfMonth < 10) {
                            StringBuffer str = new StringBuffer("0");
                            day = str.append(dayOfMonth).toString();
                        } else {
                            day = String.valueOf(dayOfMonth);
                        }
                        String date = year + "-" + mon + "-" + day;
                        StringBuffer temp = new StringBuffer(date);
                        addDate = temp.append("T00:00:00+10:00").toString();
                        pickedDate.setText(addDate);
                    }
                }, myear, mmonthOfYear, mdayOfMonth);
                DatePicker dp = datePicker.getDatePicker();
                dp.setMaxDate(System.currentTimeMillis());
                datePicker.show();
            }
        });

        addTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(addMemoirV.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        StringBuffer time = new StringBuffer();
                        String hour = "";
                        if(hourOfDay < 10) {
                            StringBuffer stringBuffer = new StringBuffer("0");
                            hour = stringBuffer.append(hourOfDay).toString();
                        } else {
                            hour = String.valueOf(hourOfDay);
                        }
                        String min = "";
                        if (minute < 10) {
                            StringBuffer stringBuffer = new StringBuffer();
                            min = stringBuffer.append(minute).toString();
                        } else {
                            min = String.valueOf(minute);
                        }
                        time.append(hour + ":" + min + ":" + "00");
                        String temp = "1970-01-01T" + time + "+10:00";
                        pickedTime.setText(temp);

                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        simpleRatingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                ratingMemoir.setText(String.valueOf(rating));
            }
        });

        addMemoirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rDate = movieReleasedate.getText().toString() + "T00:00:00+10:00";

                new AsyncGetUserMemoir().execute(LoginActivity.userid);
                person.setUserid(Integer.toString(LoginActivity.userid));
                person.setAddress(LoginActivity.person.getAddress());
                person.setDob(LoginActivity.person.getDob());
                person.setFirstname(LoginActivity.person.getFirstname());
                person.setSurname(LoginActivity.person.getSurname());
                person.setGender(LoginActivity.person.getGender());
                person.setPostcode(LoginActivity.person.getState());
                person.setState(LoginActivity.person.getState());

                Memoir memoir = new Memoir(cinema, editComment.getText().toString(), movieName.getText().toString(), ratingMemoir.getText().toString(), rDate, LoginActivity.person, addDate);
                new PostMemoirAsync().execute(memoir);
            }
        });
        return addMemoirV;
    }

    private class accessImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... imgURL) {
            Bitmap bm;
            try {
                URL url = new URL(imgURL[0]);
                InputStream inputStream = url.openStream();
                bm = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
               return bm;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            poster.setImageBitmap(bm);
        }
    }

    private class accessCinema extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            URL url = null;
            final String methodPath = "m3.cinema";
            HttpURLConnection conn = null;
            String textRecv = "";
            try {
                url = new URL(LoginActivity.backendRootURL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);//https://api.themoviedb.org/3/search/movie?api_key=fd62ffa46266b8ba9b9a1b5d7934e33d&language=en-US&query=Superman&page=1&include_adult=false
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

        @Override
        protected void onPostExecute(String textRecv) {
            try {
                JSONArray ja = new JSONArray(textRecv);
                for(int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    String tempCinemaString = jo.getString("cinemaname") + " " + jo.getString("surburb");
                    Cinema tempCinema = new Cinema(jo.getString("cinemaid"), jo.getString("cinemaname"), jo.getString("surburb"));
                    cinemaList.add(tempCinemaString);
                    cinemas.add(tempCinema);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //SpinnerAdapter
            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(addMemoirV.getContext(), android.R.layout.simple_spinner_item, cinemaList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cinemaSpinner.setAdapter(spinnerAdapter);
            cinemaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cinema = cinemas.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    protected class PostMemoirAsync extends AsyncTask<Memoir, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Memoir... memoirs) {
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "m3.memoir/";
            try {
                Gson gson = new Gson();
                String personJson = gson.toJson(memoirs[0]);
                String a = personJson.replace("[", "").replace("]", "");

                url = new URL(LoginActivity.backendRootURL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(a.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/json");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(a);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                conn.disconnect();
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean flag) {
            if(flag) {
                Toast.makeText(addMemoirV.getContext(), "MEMOIR POSTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(addMemoirV.getContext(), "MEMOIR POST ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private class AsyncGetUserMemoir extends AsyncTask<Integer, Void, Person> {
        @Override
        protected Person doInBackground(Integer... userid) {
            String textResult = "";
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "m3.credential/findByUserid/" + userid[0];
            try{
                url = new URL (LoginActivity.backendRootURL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()){
                    textResult += inStream.nextLine();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
            try {
                JSONArray ja = new JSONArray(textResult);
                JSONObject jo = (JSONObject) ja.get(0);
                person.setUserid(jo.getString("userid"));
                person.setFirstname(jo.getString("firstname"));
                person.setSurname(jo.getString("surname"));
                person.setGender(jo.getString("gender"));
                person.setDob(jo.getString("dob"));
                person.setAddress(jo.getString("address"));
                person.setPostcode(jo.getString("postcode"));
                person.setState(jo.getString("state"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
