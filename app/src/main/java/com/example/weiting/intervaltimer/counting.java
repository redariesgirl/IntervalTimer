package com.example.weiting.intervaltimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import android.os.Handler;


/**
 * Created by weiting on 2015/2/9.
 */
public class Counting extends Activity{
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
    private Button pauseButton;
    private SoundPool sp;
    private int soundID;
    private FragmentManager mFragmentManager;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            countDown();
            if (mode == REST && set == -1){
                handler.removeCallbacks(runnable);
                showFinishMessage();
                reset();
            }
            else handler.postDelayed(this, 1000);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {

        int COUNT_ONLY = 1;
        int WITH_MUSIC = 2;
        String[] temp;
        Boolean screenOn;
        Button stopButton;
        AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        FrameLayout musicFragment;
        //count only or with music
        int mode = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.counting_basic);

        exerciseTime = (TextView)findViewById(R.id.exerciseTime);
        restTime = (TextView)findViewById(R.id.restTime);
        setRemaining = (TextView) findViewById(R.id.setRemaining);
        pauseButton = (Button)findViewById(R.id.pauseButton);
        stopButton = (Button)findViewById(R.id.stopButton);




        sp = new SoundPool(1,AudioManager.STREAM_MUSIC, 0 );
        soundID = sp.load(this, R.raw.beep,1);

        Intent i = getIntent();
        if (i != null) {
            mode = Integer.parseInt(i.getStringExtra("Mode"));
            Log.e(TAG, "initiaion, mode= "+mode);
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
            screenOn = new Boolean(i.getStringExtra("ScreenOn"));
            Log.e(TAG, "ScreenOn: "+screenOn);
            Log.e(TAG, "ScreenOn passed: " +i.getStringExtra("ScreenOn"));
            if (screenOn){
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Log.e(TAG, "ScreenOn()");
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
        //TODO: changed here
        if (mode == WITH_MUSIC){
            showMusicPlayer();
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
        handler.postDelayed(runnable,0);
    }
    protected void onStop(){
        super.onStop();
        handler.removeCallbacks(runnable);
    }


    void countDown(){
        TextView v;

        int threshold;
        //boolean highlight = false;
        if (mode == EXERCISE){
            v = exerciseTime;
            threshold = Math.min(15, exerciseTimeInSec/6);
        }
        else{
            v = restTime;
            threshold = Math.min(15, restTimeInSec/10);
        }

        //Log.e(TAG, "Threshold: " + threshold);
        int minutes = length/60;
        int seconds = length % 60;
        //parse into xx:xx String and update text
        String str = String.format("%02d:%02d", minutes,seconds);
        v.setText(str);

        //if remaining time is less than threshold then highlight text
        if (length < threshold){
            //if only 15 secs to go, increase text size and change color
            Log.e(TAG, "below threshold");
            changeColor(v, true);
        }

        if (length == 0){
            changeColor(v, false);
            if (mode == REST) {
                //if in rest mode
                //if set = 0;
                if (set == 0){
                    //  stop activity
                    set -= 1;
                    //handler.removeCallbacks(runnable);
                    //long buzz or alarm
                    buzzOrAlarm(notifyMode, Boolean.TRUE);
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
        //int repeat = 3;

        Vibrator vibrator = (Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        if (isLong){
            Log.e("TAG", "Vibrate long");
            vibrator.vibrate(new long[]{(long)(pauseTime*1000),time*1000,(long)(pauseTime*1000),time*1000,(long)(pauseTime*1000),time*1000}, -1);

        }
        else vibrator.vibrate(time * 1000);
    }
    void alarm(Boolean isLong){
        int streamID;

        if (isLong) {
            //repeat 5 times
            streamID = sp.play(soundID, 1, 1, 1, 4, 1);
            //Log.e(TAG, "Alarm long "+ streamID+ " " + isLong);
        }
        else{
            //repeat 2 times
            streamID = sp.play(soundID, 1.0F, 1.0F, 0, 1, 1.0F);
            //Log.e(TAG, "Alarm short" + streamID + " "+ isLong);
        }



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
        pauseButton.setText("Start");
        pause = Boolean.TRUE;
    }

    void changeColor(TextView v, boolean highlight){
        if (highlight) {
            v.setTextAppearance(this, R.style.textCountHighlight);
        }
        else{
            v.setTextAppearance(this, R.style.textCount);
        }


        //TODO change text size and color when current activity is about to end
    }

    void showMusicPlayer(){
        Log.e(TAG, "With_Music");
        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();
        fragmentTransaction.add(R.id.musicFragment, new MusicPlayerFragment());
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Music");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.e(TAG, "item ID: "+id);
        return super.onOptionsItemSelected(item);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        Log.e(TAG, "onKeyDown; KeyEvent: " + keyCode);
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            confirmExit();//按返回鍵，則執行退出確認
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    void confirmExit(){
        AlertDialog.Builder ad=new AlertDialog.Builder(this);
        ad.setTitle("Exit");
        ad.setMessage("Are you sure to leave current session?");
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {//退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                finish();//finish activity
            }
        });
        ad.setNegativeButton("No",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //do nothing
            }
        });
        ad.show();//示對話框
    }
}