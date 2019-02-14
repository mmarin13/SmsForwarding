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

        //load saved phone number
        String number = getSharedPreferences("data", Context.MODE_PRIVATE).getString("number", " ");
        Boolean state = getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("state", true);
        Log.d("number", number);

        EditText editText = findViewById(R.id.edit_number);
        if(!number.isEmpty()) {
            editText.setText(number, TextView.BufferType.EDITABLE);
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
        EditText editText = findViewById(R.id.edit_number);
        String number = editText.getText().toString();
        Log.d("number", number);

        Switch run = findViewById(R.id.run);

        //save phone number and app state
        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("number", number);
        editor.putBoolean("state", run.isChecked());
        editor.commit();

        if(number.isEmpty()) {
                Toast.makeText(getApplicationContext(), "You must enter a phone number",
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
