package com.example.weiting.intervaltimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import android.os.Handler;


/**
 * Created by weiting on 2015/2/9.
 */
public class counting extends Activity{
    static private final String TAG = "Counting";
    private int mode;
    private final int EXERCISE = 0;
    private final int REST = 1;
    private TextView exerciseTime;
    private TextView restTime;
    private TextView setRemaining;
    private Handler handler = new Handler();
    //seconds remaining for the current activity
    private int length;
    // length for each sets
    private int exerciseTimeInSec;
    private int restTimeInSec;
    String e_time;
    String r_time;
    //private int seconds;
    private int set;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            countDown();
            if (mode == REST && set == -1){
                handler.removeCallbacks(runnable);
            }
            else handler.postDelayed(this, 1000);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {

        String[] temp;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.counting_basic);
        exerciseTime = (TextView)findViewById(R.id.exerciseTime);
        restTime = (TextView)findViewById(R.id.restTime);
        setRemaining = (TextView) findViewById(R.id.setRemaining);

        Intent i = getIntent();
        if (i != null) {
            e_time = i.getStringExtra("Exercise time");
            r_time = i.getStringExtra("Rest time");
            set = Integer.parseInt(i.getStringExtra("Set"));
            exerciseTime.setText(e_time);
            restTime.setText(r_time);
            setRemaining.setText(String.valueOf(set));
            temp = e_time.split(":");
            temp[0]= String.format("%02d", Integer.parseInt(temp[0]));
            temp[1] = String.format("%02d", Integer.parseInt(temp[1]));
            //Log.e(TAG, temp[0]);
            //Log.e(TAG, temp[1]);
            exerciseTimeInSec = Integer.parseInt(temp[0])*60 + Integer.parseInt(temp[1]);
            temp = r_time.split(":");
            restTimeInSec = Integer.parseInt(temp[0])*60 + Integer.parseInt(temp[1]);
            length = exerciseTimeInSec;
            //Log.e(TAG, "exercise time: "+String.valueOf(exerciseTimeInSec)+ " /Rest time: "+String.valueOf(restTimeInSec));
        }
        set -= 1;  //set to remaining sets
        mode = EXERCISE;
        handler.postDelayed(runnable,1000);



    }

    void countDown(){
        TextView v;
        if (mode == EXERCISE){
            v = exerciseTime;
        }
        else v = restTime;
        length -= 1;
        if (length < 0){
            //TODO buzz or alarm

            if (mode == REST) {
                //if in rest mode
                //if set = 0;
                if (set == 0){
                    //  stop activity
                    set -= 1;
                    handler.removeCallbacks(runnable);

                }
                else{
                    //not finished yet
                    //reset length
                    length = exerciseTimeInSec;
                    set -= 1;
                    setRemaining.setText(String.valueOf(set));
                    exerciseTime.setText(e_time);
                    restTime.setText(r_time);
                    //restart exercise countdown
                    mode = EXERCISE;
                    countDown();

                }
            }
            //else (in exercise mode)
            else if (mode == EXERCISE ) {
                // exercise time show 00:00
                exerciseTime.setText("00:00");
                length = restTimeInSec;
                // start rest mode;
                mode = REST;
                countDown();

                //countDown(restTime);
            }
            return;
        }
        Log.e("countDown", String.valueOf(length));
        int minutes = length/60;
        int seconds = length % 60;
        //parse into xx:xx String and update text
        String str = String.format("%02d:%02d", minutes,seconds);
        v.setText(str);

        //handler.postDelayed(runnable,1000);

    }
}