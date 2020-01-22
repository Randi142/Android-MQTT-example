package com.randi.MQTT_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

public class HumidChart extends AppCompatActivity {

    ChartHelper mChart;
    LineChart chart;
    ArrayList<Integer> humidArray = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humid_chart);
        //declare
        chart = (LineChart) findViewById(R.id.chart2);
        mChart = new ChartHelper(chart);

        Bundle extras = getIntent().getExtras();    //get data from MainActivity
        if (extras != null) {   //if no data = not passing anything
            humidArray = extras.getIntegerArrayList("humid"); //MainActivity arraylist -> TempChart arraylist

            for (int i : humidArray) //put all arraylist data
                mChart.addEntry(Integer.valueOf(i));
        }
    }
}
