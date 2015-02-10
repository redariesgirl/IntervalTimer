package com.example.weiting.intervaltimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
    private boolean pause = Boolean.FALSE;
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
    private String notifyMode;
    //private int seconds;
    private int set;
    private int totalSet;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            countDown();
            if (mode == REST && set == -1){
                handler.removeCallbacks(runnable);
                showFinishMessage();
            }

            else handler.postDelayed(this, 1000);
            Log.e(TAG, "runnable");
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        final Button pauseButton;
        Button stopButton;
        String[] temp;
        Boolean screenOn;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.counting_basic);
        exerciseTime = (TextView)findViewById(R.id.exerciseTime);
        restTime = (TextView)findViewById(R.id.restTime);
        setRemaining = (TextView) findViewById(R.id.setRemaining);
        pauseButton = (Button)findViewById(R.id.pauseButton);
        stopButton = (Button)findViewById(R.id.stopButton);

        Intent i = getIntent();
        if (i != null) {
            e_time = i.getStringExtra("Exercise time");
            r_time = i.getStringExtra("Rest time");
            try{
                set = Integer.parseInt(i.getStringExtra("Set"));
            }
            catch(NullPointerException e){
                //default 5 sets
                set = 5;
            }
            totalSet = set;
            set -= 1;
            notifyMode = i.getStringExtra("Notify");
            screenOn = Boolean.getBoolean(i.getStringExtra("ScreenOn"));

            if (screenOn){
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            //restTime.setText(r_time);
            setRemaining.setText(String.valueOf(set));
            temp = e_time.split(":");
            if (temp[0] == ""){
                temp[0] = "0";
            }
            if(temp[1] == ""){
                temp[1] = "0";
            }
            e_time = String.format("%02d:%02d", Integer.parseInt(temp[0]) , Integer.parseInt(temp[1]));
            exerciseTime.setText(e_time);
            exerciseTimeInSec = Integer.parseInt(temp[0])*60 + Integer.parseInt(temp[1]);
            temp = r_time.split(":");
            if (temp[0] == ""){
                temp[0] = "0";
            }
            if(temp[1] == ""){
                temp[1] = "0";
            }
            r_time = String.format("%02d:%02d", Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
            restTime.setText(r_time);
            restTimeInSec = Integer.parseInt(temp[0])*60 + Integer.parseInt(temp[1]);
            length = exerciseTimeInSec;
            //Log.e(TAG, "exercise time: "+String.valueOf(exerciseTimeInSec)+ " /Rest time: "+String.valueOf(restTimeInSec));
        }

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pause){
                    //if current state = pause, then restart
                    pauseButton.setText("Pause");
                    pause = Boolean.FALSE;
                    setRemaining.setText(String.valueOf((set)));
                    handler.postDelayed(runnable,0);
                }
                else{
                    //if ongoing, change text and pause
                    pauseButton.setText("Restart");
                    pause = Boolean.TRUE;
                    handler.removeCallbacks(runnable);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop activity and reset
                handler.removeCallbacks(runnable);
                reset();
                pauseButton.setText("Start");
                pause = Boolean.TRUE;
                reset();

            }
        });


        mode = EXERCISE;
        handler.postDelayed(runnable,1000);
    }
    protected void onStop(){
        super.onStop();
        handler.removeCallbacks(runnable);
    }


    void countDown(){
        TextView v;
        if (mode == EXERCISE){
            v = exerciseTime;
        }
        else v = restTime;

        Log.e("countDown", String.valueOf(length));
        int minutes = length/60;
        int seconds = length % 60;
        //parse into xx:xx String and update text
        String str = String.format("%02d:%02d", minutes,seconds);
        v.setText(str);


        if (length == 0){
            if (mode == REST) {
                //if in rest mode
                //if set = 0;
                if (set == 0){
                    //  stop activity
                    set -= 1;
                    //handler.removeCallbacks(runnable);
                    //long buzz or alarm
                    buzzOrAlarm(notifyMode, Boolean.TRUE);
                    reset();

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
                    //short buzz or alarm
                    buzzOrAlarm(notifyMode, Boolean.FALSE);

                }
            }
            //else (in exercise mode)
            else if (mode == EXERCISE ) {
                // exercise time show 00:00
                exerciseTime.setText("00:00");
                length = restTimeInSec;
                //short buzz or alarm
                buzzOrAlarm(notifyMode, Boolean.FALSE);
                // start rest mode;
                mode = REST;
                countDown();

            }
            return;
        }
        length -= 1;

        //handler.postDelayed(runnable,1000);

    }
    void buzzOrAlarm(String notify, Boolean isLong){
        //time in seconds
        switch (Integer.parseInt(notify)) {
            case R.id.alarmButton:
                //alarm
                alarm(isLong);
                break;

            case R.id.vibrateButton:
                //vibrate
                buzz(isLong);
                break;
            case R.id.bothButton:
                //both
                buzz(isLong);
                alarm(isLong);
                break;
        }
    }
    void buzz(Boolean isLong){
        //time in seconds
        int time = 1;
        double pauseTime = 0.1;
        int repeat = 3;

        Vibrator vibrator = (Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        if (isLong){
            Log.e("TAG", "Vibrate long");
            vibrator.vibrate(new long[]{(long)(pauseTime*1000),time*1000,(long)(pauseTime*1000),time*1000,(long)(pauseTime*1000),time*1000}, -1);

        }
        else vibrator.vibrate(time * 1000);
    }
    void alarm(Boolean isLong){
        //TODO get alarm work
    }

    void showFinishMessage(){

        int totalTime = (exerciseTimeInSec + restTimeInSec)*totalSet;
        String totalTimeStr = String.format("%02d:%02d",totalTime/60, totalTime%60);

        //TODO show custom finish message (including total workout time) :http://stackoverflow.com/questions/13341560/how-to-create-a-custom-dialog-box-in-android
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Interval Training Done");
        dialog.setMessage("Total workout time: " + totalTimeStr);
        dialog.show();
    }
    void reset(){
        exerciseTime.setText(e_time);
        restTime.setText(r_time);
        setRemaining.setText(String.valueOf((totalSet)));
        set = totalSet - 1;
        length = exerciseTimeInSec;
    }
}