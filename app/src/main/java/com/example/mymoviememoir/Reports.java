package com.example.mymoviememoir;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;


public class Reports extends Fragment {
    private View vReport;
    private Button startdateBtn;
    private Button endDateBtn;
    private TextView startdate;
    private TextView enddate;
    private Button pieSubmit;
    private Button barSubmit;
    private PieChart piechart;
    private BarChart barChart;
    private Spinner spinner;
    private String pickedYear;
    private String pickedDate = "";
    private List<Integer> yValue = new ArrayList<>();
    private List<String> xValue = new ArrayList<>();
    private List<IBarDataSet> dataSets = new ArrayList<>();
    private LinkedHashMap<String,List<Integer>> chartDataMap = new LinkedHashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vReport = inflater.inflate(R.layout.report, container, false);
        startdateBtn = vReport.findViewById(R.id.startdatereport);
        endDateBtn = vReport.findViewById(R.id.enddatereport);
        startdate = vReport.findViewById(R.id.startdatetv);
        enddate = vReport.findViewById(R.id.enddatetv);
        piechart = vReport.findViewById(R.id.piechart);
        spinner = vReport.findViewById(R.id.yearspinnerreport);
        barChart = vReport.findViewById(R.id.barchart);
        startdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(startdate);
            }
        });
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(enddate);
            }
        });
        pieSubmit = vReport.findViewById(R.id.reportpie);
        pieSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MovieCountSurburb().execute(startdate.getText().toString(),enddate.getText().toString());
            }
        });

        barSubmit = vReport.findViewById(R.id.reportbar);
        barSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new showBarChart().execute(pickedYear);
            }
        });

        List<String> year = new ArrayList<>();
        year.add("2020");year.add("2019");year.add("2018");year.add("2017");year.add("2016");year.add("2015");

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(Reports.this.getActivity(), android.R.layout.simple_spinner_item, year);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = parent.getItemAtPosition(position).toString();
                pickedYear = selectedState;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                pickedYear = "2020";
            }
        });
        return vReport;
    }

    private class MovieCountSurburb extends AsyncTask<String, Void, List<PieEntry>> {
        @Override
        protected List<PieEntry> doInBackground(String... strings) {
            String textResult = "";
            List<PieEntry> pieList = new ArrayList<>();
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "a3.memoir/MovieCountSurburb/";
            try {
                url = new URL(LoginActivity.backendRootURL + methodPath+LoginActivity.userid+"/"+strings[0]+"/"+strings[1]);
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
                    PieEntry pie = new PieEntry(Integer.parseInt(jsonObject.getString("totalnumber")),jsonObject.getString("surburb"));
                    pieList.add(pie);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return pieList;
        }
        @Override
        protected void onPostExecute(List<PieEntry> data) {
            piechart.setUsePercentValues(false);
            piechart.getDescription().setEnabled(false);
            piechart.setExtraOffsets(5, 10, 5, 5);
            ArrayList<Integer> colors = new ArrayList<Integer>();
            for (int c : ColorTemplate.MATERIAL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
            colors.add(ColorTemplate.getHoloBlue());
            PieDataSet pieDataSet = new PieDataSet(data, "");
            pieDataSet.setColors(colors);
            PieData pie = new PieData();
            pie.setDataSet(pieDataSet);
            piechart.setData(pie);
            piechart.invalidate();
        }
    }

    private class showBarChart extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... year) {
            xValue = new ArrayList<>();
            yValue = new ArrayList<>();
            dataSets = new ArrayList<>();
            chartDataMap = new LinkedHashMap<>();
            String textResult = "";
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "a3.memoir/MovieCountMonth/";
            try {
                url = new URL(LoginActivity.backendRootURL + methodPath + LoginActivity.userid + "/" + year[0]);
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
                    xValue.add(jsonObject.getString("Month"));
                    yValue.add(Integer.parseInt(jsonObject.getString("totalnumber")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chartDataMap.put("Month", yValue);
            ArrayList<Integer> colors = new ArrayList<>();
            for (int c : ColorTemplate.MATERIAL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
            colors.add(ColorTemplate.getHoloBlue());
            int currentPosition = 0;
            for (LinkedHashMap.Entry<String, List<Integer>> entry : chartDataMap.entrySet()) {
                String name = entry.getKey();
                List<Integer> yValueList = entry.getValue();
                List<BarEntry> entries = new ArrayList<>();
                for (int i = 0; i < yValueList.size(); i++) {
                    entries.add(new BarEntry(i, yValueList.get(i)));
                }
                BarDataSet barDataSet = new BarDataSet(entries, name);
                initBarDataSet(barDataSet, colors.get(currentPosition));
                dataSets.add(barDataSet);
                currentPosition++;
            }
            return textResult;
        }

        @Override
        protected void onPostExecute(String data) {
            BarData data2 = new BarData(dataSets);
            data2.setBarWidth(0.5f);
            int barAmount = chartDataMap.size();
            float groupSpace = 0.3f;
            float barWidth = (1f - groupSpace) / barAmount;
            data2.setBarWidth(barWidth);
            barChart.setBackgroundColor(Color.WHITE);
            barChart.setDrawGridBackground(false);
            barChart.setDrawBarShadow(false);
            barChart.setHighlightFullBarEnabled(false);
            barChart.setDrawBorders(true);
            barChart.setData(data2);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setAxisMinimum(0f);
            xAxis.setGranularity(1f);
            xAxis.setDrawGridLines(false);
            xAxis.setAxisMinimum(0f);
            xAxis.setAxisMaximum(xValue.size());
            xAxis.setCenterAxisLabels(true);
            YAxis leftAxis = barChart.getAxisLeft();
            YAxis rightAxis = barChart.getAxisRight();
            leftAxis.setAxisMinimum(0f);
            rightAxis.setAxisMinimum(0f);
            Legend legend = barChart.getLegend();
            legend.setForm(Legend.LegendForm.LINE);
            legend.setTextSize(11f);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xValue.get((int) Math.abs(value) % xValue.size());
                }
            });
        }
    }

    private void initBarDataSet(BarDataSet barDataSet, int color) {
        barDataSet.setColor(color);
        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(10f);
        barDataSet.setDrawValues(false);
    }

    public void showDatePicker(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int myear = calendar.get(Calendar.YEAR);
        int mmonthOfYear = calendar.get(Calendar.MONTH);
        final int mdayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
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
        DatePickerDialog datePicker = new DatePickerDialog(vReport.getContext(), new DatePickerDialog.OnDateSetListener() {
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
                textView.setText(date);
                StringBuffer temp = new StringBuffer(date);
                pickedDate = temp.append("T00:00:00+10:00").toString();
            }
        }, myear, mmonthOfYear, mdayOfMonth);
        DatePicker dp = datePicker.getDatePicker();
        dp.setMaxDate(System.currentTimeMillis());
        datePicker.show();
    }
}
