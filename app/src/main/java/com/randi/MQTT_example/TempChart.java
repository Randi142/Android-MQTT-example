package com.randi.MQTT_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

public class TempChart extends AppCompatActivity {

    ChartHelper mChart;
    LineChart chart;
    ArrayList<Integer> tempArray = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_chart);
        //declare
        chart = (LineChart) findViewById(R.id.chart);
        mChart = new ChartHelper(chart);

        Bundle extras = getIntent().getExtras();    //get data from MainActivity
        if (extras != null) {   //if no data = not passing anything
            tempArray = extras.getIntegerArrayList("temp"); //MainActivity arraylist -> TempChart arraylist

            for (int i : tempArray) //put all arraylist data
                mChart.addEntry(Integer.valueOf(i));
        }
    }
}
