package com.randi.MQTT_example;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends AppCompatActivity {

    /*================     all arduino data  from MQTT should be int instead of float ================*/

    MqttHelper mqttHelper;
    TextView tempText;
    TextView humidText;
    TextView levelText;
    TextView gasText;
    TextView flameText;
    ProgressBar levelBar;
    ArrayList<Integer> tempArray = new ArrayList<Integer>();    //***** care of float data*****
    ArrayList<Integer> humidArray = new ArrayList<Integer>();
    boolean levelNotification = true;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //android component declare
        tempText = (TextView) findViewById(R.id.tempText);
        humidText = (TextView) findViewById(R.id.humidText);
//        levelText = (TextView) findViewById(R.id.levelText);
        gasText = (TextView) findViewById(R.id.gasText);
        flameText = (TextView) findViewById(R.id.flameText);
//        levelBar = (ProgressBar) findViewById(R.id.levelBar);

        //MQTT function
        startMqtt();
    }

    private void startMqtt(){   //MQTT function
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Debug","Connected");
            }   //log for debug

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("DebugTopic",topic);  //log for debug (finding topic)


                if (topic.equals("sensor/temp")){
                    Log.w("DebugMessage",mqttMessage.toString());   //log for debug (showing message)
                    if (Numeric(mqttMessage.toString())) {    //cannot pass a non-integer to show temp
                        tempText.setText(mqttMessage.toString() + " °C");
                        tempArray.add(Integer.parseInt(mqttMessage.toString()));
                    }
                }
                if (topic.equals("sensor/humid")){
                    Log.w("DebugMessage",mqttMessage.toString());
                    if (Numeric(mqttMessage.toString())) {
                        humidText.setText(mqttMessage.toString() + " %");
                        humidArray.add(Integer.parseInt(mqttMessage.toString()));
                    }
                }
//                if (topic.equals("sensor/level")){
//                    Log.w("DebugMessage",mqttMessage.toString());
//                    if (Numeric(mqttMessage.toString())) {
//                        levelText.setText(mqttMessage.toString() + " %");
//                        levelBar.setProgress(Integer.parseInt(mqttMessage.toString())); //progress bar status
//                        if(Integer.parseInt(mqttMessage.toString()) > 79 && levelNotification == true) {  //only notice once while almost full
//                            addNotification("Your trash bin almost full");
//                            levelNotification = false;
//                        }
//                        if(Integer.parseInt(mqttMessage.toString()) == 100) //full
//                            addNotification("Your trash bin is full");
//                    }
//                }
                if (topic.equals("sensor/light")){
                    Log.w("DebugMessage",mqttMessage.toString());
                        gasText.setText(Bool2(mqttMessage.toString(),"Temporary light turned on!"));
                }
                if (topic.equals("sensor/flame")){
                    Log.w("DebugMessage",mqttMessage.toString());
                    flameText.setText(Bool(mqttMessage.toString(),"FLAME Detected!"));
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }



    public boolean Numeric(String string) {        //check the string whether is a number
            boolean numeric = true;
            try {
                int num = Integer.parseInt(string);
                if (num >100)
                    numeric = false;
            } catch (NumberFormatException e) {
                numeric = false;
            }
            return numeric;
    }

    public String Bool (String string, String notification){        //check the string whether is 1/0 or true/false
        String bool = "No data";
        try{
            int num = Integer.parseInt(string);
            if(num == 0) {
                bool = "Undetected";
                addNotification(notification);
            }
            if(num == 1)
                bool = "Detected";

        } catch (NumberFormatException e){
            if(string.equalsIgnoreCase("false")) {
                bool = "Undetected";
                addNotification(notification);
            }
            if(string.equalsIgnoreCase("true"))
                bool = "Detected";
        }

        return bool;
    }

    public String Bool2 (String string, String notification){        //check the string whether is 1/0 or true/false
        String bool = "No data";
        try{
            int num = Integer.parseInt(string);
            if(num == 0) {
                bool = "OFF";
                addNotification(notification);
            }
            if(num == 1)
                bool = "ON";

        } catch (NumberFormatException e){
            if(string.equalsIgnoreCase("false")) {
                bool = "OFF";
                addNotification(notification);
            }
            if(string.equalsIgnoreCase("true"))
                bool = "ON";
        }

        return bool;
    }

    public void tempOnclick(View view){
        Intent i = new Intent(this, TempChart.class);   //open new activity
        i.putExtra("temp",tempArray);   //transfer array list to the chart activity
        startActivity(i);   //start chart activity
    }

    public void humidOnclick(View view){
        Intent i = new Intent(this, HumidChart.class);   //open new activity
        i.putExtra("humid",humidArray);   //transfer array list to the chart activity
        startActivity(i);   //start chart activity
    }

    public void addNotification(String string) {
        // Builds notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)   //deprecated but should be used for API23
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Factory safety system notification")
                .setContentText(string);

        // Creates the intent needed to show the notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
