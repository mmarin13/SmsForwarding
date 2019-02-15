package com.android.mm13.smsforwarding;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load saved phone numberTo
        String numberTo = getSharedPreferences("data", Context.MODE_PRIVATE).getString("numberTo", "");
        String numberFrom = getSharedPreferences("data", Context.MODE_PRIVATE).getString("numberFrom", "");
        Boolean state = getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("state", true);
        Log.d("numberTo", numberTo);
        Log.d("numberFrom", numberFrom);

        EditText editTextFrom = findViewById(R.id.edit_number_from);
        if(!numberFrom.isEmpty()) {
            editTextFrom.setText(numberFrom, TextView.BufferType.EDITABLE);
        }

        EditText editTextTo = findViewById(R.id.edit_number_to);
        if(!numberTo.isEmpty()) {
            editTextTo.setText(numberTo, TextView.BufferType.EDITABLE);
        }

        Switch run = findViewById(R.id.run);
        run.setChecked(state);
        if(state) {
            enableBroadcastReceiver();
        } else {
            disableBroadcastReceiver();
        }
    }

    /**
     * This method updates the phone number and settings.
     * @param v view
     */
    public void setPhoneNumber(View v) {
        EditText editTextFrom = findViewById(R.id.edit_number_from);
        String numberFrom = editTextFrom.getText().toString();
        Log.d("numberFrom", numberFrom);

        EditText editTextTo = findViewById(R.id.edit_number_to);
        String numberTo = editTextTo.getText().toString();
        Log.d("numberTo", numberTo);

        Switch run = findViewById(R.id.run);

        //save phone numbers and app state
        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("numberFrom", numberFrom);
        editor.putString("numberTo", numberTo);
        editor.putBoolean("state", run.isChecked());
        editor.commit();

        if(numberTo.isEmpty() || numberFrom.isEmpty()) {
                Toast.makeText(getApplicationContext(), "All fields are required!",
                        Toast.LENGTH_SHORT).show();
        } else {
                Toast.makeText(getApplicationContext(), "Settings are saved!",
                        Toast.LENGTH_SHORT).show();
            if(!checkForSmsPermission()) {
                requestPermission();
            }
        }

        if(run.isChecked()) {
            enableBroadcastReceiver();
        } else {
            disableBroadcastReceiver();
        }
    }

    /**
     * This method checks for permission for SMS
     * @return true/false
     */
    private boolean checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("warning", "Permission not granted");
            return false;
        } else {
            Log.d("warning", "Permission granted");
            return true;
        }
    }

    /**
     *This method requests permission for SMS
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_REQUEST_SEND_SMS);
    }

    /**
     * This method enables the Broadcast receiver registered in the AndroidManifest file.
     */
    private void enableBroadcastReceiver(){
        ComponentName receiver = new ComponentName(this, SmsListener.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        //Toast.makeText(this, "Enabled broadcast receiver", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method disables the Broadcast receiver registered in the AndroidManifest file.
     */
    private void disableBroadcastReceiver(){
        ComponentName receiver = new ComponentName(this, SmsListener.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        //Toast.makeText(this, "Disabled broadcst receive", Toast.LENGTH_SHORT).show();
    }
}
