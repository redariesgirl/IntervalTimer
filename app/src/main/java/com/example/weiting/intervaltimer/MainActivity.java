package com.example.weiting.intervaltimer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;



public class MainActivity extends ActionBarActivity {

    static private final String TAG = "MainActivity";
    private EditText exerciseTimePicker1;
    private EditText exerciseTimePicker2;
    private EditText restTimePicker1;
    private EditText restTimePicker2;
    private EditText setPicker;
    private RadioGroup notifyRadioGroup;
    private CheckBox screenOnCB;
   //private Button alarmButton;
    //private Button vibrateButton;
    //private Button bothButton;
    private Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        exerciseTimePicker1 = (EditText)findViewById(R.id.exerciseTimePicker1);
        exerciseTimePicker2 = (EditText)findViewById(R.id.exerciseTimePicker2);
        restTimePicker1 = (EditText)findViewById(R.id.restTimePicker1);
        restTimePicker2 = (EditText)findViewById(R.id.restTimePicker2);
        setPicker = (EditText)findViewById(R.id.setPicker);
        notifyRadioGroup = (RadioGroup)findViewById(R.id.notifyRadioGroup);
        //alarmButton = (Button)findViewById(R.id.alarmButton);
        //vibrateButton = (Button)findViewById(R.id.vibrateButton);
        //bothButton = (Button)findViewById(R.id.bothButton);
        startButton = (Button)findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCounting();
            }
        });
        screenOnCB = (CheckBox)findViewById(R.id.screenOnCB);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void startCounting(){
        Intent i = new Intent(getApplicationContext(), counting.class);
        i.putExtra("Exercise time",exerciseTimePicker1.getText().toString()+":"+exerciseTimePicker2.getText().toString());
        i.putExtra("Rest time",restTimePicker1.getText().toString()+":"+restTimePicker2.getText().toString());
        i.putExtra("Set", setPicker.getText().toString());
        i.putExtra("Notify", String.valueOf(notifyRadioGroup.getCheckedRadioButtonId()));
        i.putExtra("ScreenOn", String.valueOf(screenOnCB.isChecked()));
        //Log.e(TAG, i.getStringExtra("notify"));
        startActivity(i);
    }
}
