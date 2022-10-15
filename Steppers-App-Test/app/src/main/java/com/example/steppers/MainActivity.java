package com.example.steppers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private int activityPermissionCode = 1;
    //Class which allows access to the device's sensors
    private SensorManager Manager;

    //Text view for the number of steps;
    private TextView txtView;
    private ImageButton btn1;

    //Class that represents a Sensor
    private Sensor stepSensor;

    //Steps to be displayed, Current stored steps, Total steps since last reboot, Total steps
    //since last reboot recorded last time the app was paused or closed
    private int displaySteps, currentSteps, totalSteps, prevSteps;

    //First time running flag
    private Boolean firstRun = true;

    //Shared preferences keys
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String CURRENT_STEPS = "stepstaken";
    private static final String PREVIOUS_STEPS = "prevstepstaken";

    //menu
    private void entermenu(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //MenuButton
        ImageButton menu = findViewById(R.id.menu_button);

        menu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                entermenu();
            }
        });





        //Get access to android's sensor service
        Manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Get a default step counter sensor
        stepSensor = Manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //Link the created text view to the right object on the screen;
        txtView = findViewById(R.id.StepsTaken);
        //btn1 = findViewById(R.id.ResetButton);


        //Check if the step sensor exist on the device
        if(stepSensor == null)
        {
            //TODO:
        }

        //checks for access to physical activity and request it if the app has no access
        checkActivityPermission();

        //Loads the last stored number of steps and total steps since reboot
        loadSteps();
    }

    public void reset(View view)
    {
        displaySteps = 0;
        currentSteps = 0;
        prevSteps = totalSteps;
        txtView.setText(String.valueOf(displaySteps));
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    //Saves the current number of steps and total steps since reboot
    public void saveSteps() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CURRENT_STEPS, displaySteps);
        editor.putInt(PREVIOUS_STEPS, totalSteps);
        editor.apply();
    }

    //Loads the last stored number of steps and total steps since reboot
    public void loadSteps(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //Reads values from shared preferences and sets a default value if no values have been saved
        currentSteps = sharedPreferences.getInt(CURRENT_STEPS,0);
        prevSteps = sharedPreferences.getInt(PREVIOUS_STEPS, -1);


        //If a value has been saved this is not the first run
        if(prevSteps != -1)
        {
            firstRun = false;
        }
    }

    // Following two methods deal with check anf requesting the needed permissions
    public void checkActivityPermission() {
        //checks if physical activity has not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED)
        {
            //Request physical activity permission if it has not been granted
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACTIVITY_RECOGNITION}, activityPermissionCode);
        }
    }

    //Outputs corresponding message after the user allows or denys permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == activityPermissionCode)
        {
            //Output messages if permission has been granted or denied
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Permission Must be granted to use app",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Called whenever the step counter sensor increments
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //If it is the first run the total steps = previous steps so that display steps will
        //display the steps counted since the app started running
        if (firstRun)
        {
            prevSteps = (int) sensorEvent.values[0];
            firstRun = false;
        }
        totalSteps = (int) sensorEvent.values[0];

        //Calculated the number of steps to be displayed taking into account the steps stored
        //and the steps counted by the step counter
        displaySteps = currentSteps + totalSteps - prevSteps;
        txtView.setText(String.valueOf(displaySteps));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //When the app is resumed the sensor starts listening, and the last stored number of steps
    // and total steps since reboot are loaded
    @Override
    protected void onResume() {
        super.onResume();
        //Tells the sensor to start 'listening' for steps and sets the sensor delay
        Manager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        loadSteps();
    }

    //When the app is minimized or closed the current number of steps and total steps since reboot
    //are stored
    @Override
    protected void onPause() {
        super.onPause();
        //Tells the sensor to stop listening when paused
        Manager.unregisterListener(this, stepSensor);
        saveSteps();
    }
}