package com.example.mymoviememoir;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;

public class SignupActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private HashMap<String, EditText> etHashMap = new HashMap<>();
    private TextView tv_datepick;
    private Button submitBtn;
    private Button datepickBtn;
    private String gender = "M";
    private Spinner sp_state;
    private String state;
    private String DOB;
    private String signupDate;
    private List<String> username = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        scrollView = findViewById(R.id.sv_scrollView);
        submitBtn = findViewById(R.id.b_submit);
        datepickBtn = findViewById(R.id.b_datepick);
        etHashMap.put("et_newUserName", (EditText) findViewById(R.id.et_newUserName));
        etHashMap.put("et_newPassword",(EditText) findViewById(R.id.et_newPassword));
        etHashMap.put("et_confirmPassword",(EditText) findViewById(R.id.et_confirmPassword));
        etHashMap.put("et_first_name",(EditText) findViewById(R.id.et_first_name));
        etHashMap.put("et_last_name",(EditText) findViewById(R.id.et_last_name));
        etHashMap.put("et_address",(EditText) findViewById(R.id.et_address));
        etHashMap.put("et_postcode",(EditText) findViewById(R.id.et_postcode));
        tv_datepick = findViewById(R.id.tv_datepick);
        sp_state = findViewById(R.id.sp_state);

        new AsyncGetUsername().execute();//get all existed username

        //RadioGroup
        RadioGroup rg = findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = findViewById(checkedId);
                if (radbtn.getText().equals("Male")) {
                    gender = "M";
                } else {
                    gender = "F";
                }
            }
        });

        //spinner
        List<String> list = new ArrayList<String>();
        list.add("NSW");
        list.add("VIC");
        list.add("QLD");
        list.add("ACT");
        list.add("SA");
        list.add("WA");
        list.add("TAS");
        list.add("NT");
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_state.setAdapter(spinnerAdapter);
        sp_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = parent.getItemAtPosition(position).toString();
                if (selectedState != null) {
                    state = selectedState;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //date picker
        datepickBtn.setOnClickListener(new View.OnClickListener() {
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
                String spDate = myear + "-" + mm + "-" + dd;
                StringBuffer tt = new StringBuffer(spDate);
                signupDate = tt.append("T00:00:00+10:00").toString();
                DatePickerDialog datePicker = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        String dob = year + "-" + mon + "-" + day;
                        tv_datepick.setText(dob);
                        StringBuffer temp = new StringBuffer(dob);
                        DOB = temp.append("T00:00:00+10:00").toString();
                    }
                }, myear, mmonthOfYear, mdayOfMonth);
                DatePicker dp = datePicker.getDatePicker();
                dp.setMaxDate(System.currentTimeMillis());
                datePicker.show();
            }
        });

        //submit profile button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                String newusername = etHashMap.get("et_newUserName").getText().toString();
                for (int i = 0; i < username.size(); i++) {
                    if (newusername.equals(username.get(i))) {
                        flag = false;
                        etHashMap.get("et_newUserName").setError("The username is exist");
                    }
                }
                if (etHashMap.get("et_newPassword").getText().toString().equals(etHashMap.get("et_confirmPassword").getText().toString()) ||
                        !etHashMap.get("et_newPassword").equals("") || !etHashMap.get("et_first_name").equals("") || !etHashMap.get("et_last_name").equals("")) {
                    if (flag) {
                        Person person = new Person();
                        person.setFirstname(etHashMap.get("et_first_name").getText().toString());
                        person.setSurname(etHashMap.get("et_last_name").getText().toString());
                        person.setAddress(etHashMap.get("et_address").getText().toString());
                        person.setPostcode(etHashMap.get("et_postcode").getText().toString());
                        person.setDob(DOB);
                        person.setGender(gender);
                        person.setState(state);

                        new AsyncPostUser().execute(person);//post person -> get latest userid -> post credential

                        //jump back to login page
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignupActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private class AsyncPostUser extends AsyncTask<Person, Void, Person> {
        @Override
        protected Person doInBackground(Person... person) {
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "m3.person/";
            try {
                Gson gson = new Gson();
                String personJson = gson.toJson(person[0]);
                String temp = personJson.replace("[", "").replace("]", "");

                url = new URL(LoginActivity.backendRootURL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(temp.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/json");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(temp);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return person[0];
        }
        protected void onPostExecute(Person person) {
            new AsyncGetJustPostedUserid().execute(person);
        }
    }

    private class AsyncGetUsername extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... strings) {
            String textResult = "";
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "m3.credential/findAllUsername";
            try {
                url = new URL(LoginActivity.backendRootURL + methodPath);
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
                JSONArray jsonArray = new JSONArray(textResult);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String temp = jsonObject.getString("username");
                    username.add(temp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncGetJustPostedUserid extends AsyncTask<Person, Void, Person> {
        @Override
        protected Person doInBackground(Person... person) {
            String textResult = "";
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "m3.person/findLatestUserid";
            try {
                url = new URL(LoginActivity.backendRootURL + methodPath);
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
                JSONObject jsonObject = new JSONObject(textResult);
                person[0].setUserid(jsonObject.getString("userid") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return person[0];
        }
        protected void onPostExecute(Person person) {
            Credential c = new Credential();
            c.setUserid(person);
            c.setPassword(MD5.md5(etHashMap.get("et_confirmPassword").getText().toString()));
            c.setUsername(etHashMap.get("et_newUserName").getText().toString());
            c.setSignupdate(signupDate);
            new AsyncPostCredential().execute(c);
        }
    }

    private class AsyncPostCredential extends AsyncTask<Credential, Void, Void> {
        @Override
        protected Void doInBackground(Credential... credential) {
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "m3.credential/";
            try {
                Gson gson = new Gson();
                String stringCourseJson = gson.toJson(credential);
                String temp = stringCourseJson.replace("[", "").replace("]", "");
                url = new URL(LoginActivity.backendRootURL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(temp.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/json");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(temp);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return null;
        }
    }
}
