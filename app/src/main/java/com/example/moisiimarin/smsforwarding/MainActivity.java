package com.example.moisiimarin.smsforwarding;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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
        Log.d("number", number);

        EditText editText = findViewById(R.id.edit_number);
        if(!number.isEmpty()) {
            editText.setText(number, TextView.BufferType.EDITABLE);
        }
    }

    public void setPhoneNumber(View v) {
        EditText editText = findViewById(R.id.edit_number);
        String number = editText.getText().toString();
        Log.d("number", number);

        //save phone number
        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("number", number);
        editor.commit();

        CheckBox notification;
        notification = findViewById(R.id.notification);

        if(number.isEmpty()) {
            if(notification.isChecked()) {
                Toast.makeText(getApplicationContext(), "You must enter a phone number",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if(notification.isChecked()) {
                Toast.makeText(getApplicationContext(), "Phone number is set!",
                        Toast.LENGTH_SHORT).show();
            }
            if(!checkForSmsPermission()) {
                requestPermission();
            }
        }
    }

    //check for permission for SMS
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

    //request permission for SMS
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_REQUEST_SEND_SMS);
    }
}
