package com.android.mm13.smsforwarding;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private int counter;
    private Boolean state;
    private LinearLayout linerLayoutRule;
    private Vector<EditText> editTextFrom = new Vector<>();
    private Vector<EditText> editTextTo = new Vector<>();
    private Vector<String> numberFrom = new Vector<>();
    private Vector<String> numberTo = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linerLayoutRule = findViewById(R.id.rules);

        loadRules();

        Switch run = findViewById(R.id.run);
        run.setChecked(state);
        if(state) {
            enableBroadcastReceiver();
        } else {
            disableBroadcastReceiver();
        }
    }

    /**
     * This method updates the phone numbers and settings.
     * @param v view
     */
    public void setPhoneNumber(View v) {
        Switch run = findViewById(R.id.run);
        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        String tempFrom, tempTo;
        int newCounter = 0;
        for(int i = 0; i < counter; i++) {
            tempFrom = editTextFrom.elementAt(i).getText().toString();
            tempTo = editTextTo.elementAt(i).getText().toString();
            if(!tempFrom.isEmpty() && !tempTo.isEmpty()) {
                editor.putString("numberFrom" + newCounter, tempFrom);
                editor.putString("numberTo" + newCounter, tempTo);
                newCounter++;
            }
        }
        if(newCounter == 0) {
            editor.putString("numberFrom" + newCounter, "");
            editor.putString("numberTo" + newCounter, "");
            newCounter = 1;
        }
        editor.putInt("counter", newCounter);
        editor.putBoolean("state", run.isChecked());
        editor.commit();

        Toast.makeText(getApplicationContext(), "Settings are saved!",
                Toast.LENGTH_SHORT).show();
        if(!checkForSmsPermission()) {
            requestPermission();
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
        //Toast.makeText(this, "Disabled broadcast receive", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method add a new forwarding rule
     * @param view isn't use
     */
    public void addRule(View view) {
        if(counter < 3) {
            editTextFrom.add(new EditText(this));
            editTextFrom.elementAt(counter).setGravity(Gravity.CENTER);
            editTextFrom.elementAt(counter).setHint(R.string.hint_msg_from);
            editTextFrom.elementAt(counter).setInputType(InputType.TYPE_CLASS_PHONE);
            linerLayoutRule.addView(editTextFrom.elementAt(counter));

            editTextTo.add(new EditText(this));
            editTextTo.elementAt(counter).setGravity(Gravity.CENTER);
            editTextTo.elementAt(counter).setHint(R.string.hint_msg_to);
            editTextTo.elementAt(counter).setInputType(InputType.TYPE_CLASS_PHONE);
            linerLayoutRule.addView(editTextTo.elementAt(counter));

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(300,3));
            divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            linerLayoutRule.addView(divider);

            counter++;
        } else {
            Toast.makeText(this, "Limited up to 3 forwarding rules!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * This method loads saved forwarding rules.
     */
    private void loadRules() {
        counter = getSharedPreferences("data", Context.MODE_PRIVATE).getInt("counter", 1);
        state = getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("state", true);
        for(int i = 0; i < counter; i++) {
            numberTo.add(getSharedPreferences("data", Context.MODE_PRIVATE).getString("numberTo"+i, ""));
            numberFrom.add(getSharedPreferences("data", Context.MODE_PRIVATE).getString("numberFrom"+i, ""));
            Log.d("numberTo", numberTo.elementAt(i));
            Log.d("numberFrom", numberFrom.elementAt(i));

            editTextFrom.add(new EditText(this));
            editTextFrom.elementAt(i).setGravity(Gravity.CENTER);
            editTextFrom.elementAt(i).setInputType(InputType.TYPE_CLASS_PHONE);
            if(!numberFrom.elementAt(i).isEmpty()) {
                editTextFrom.elementAt(i).setText(numberFrom.elementAt(i), TextView.BufferType.EDITABLE);
            } else {
                editTextFrom.elementAt(i).setHint(R.string.hint_msg_from);
            }
            linerLayoutRule.addView(editTextFrom.elementAt(i));

            editTextTo.add(new EditText(this));
            editTextTo.elementAt(i).setGravity(Gravity.CENTER);
            editTextTo.elementAt(i).setInputType(InputType.TYPE_CLASS_PHONE);
            if(!numberTo.elementAt(i).isEmpty()) {
                editTextTo.elementAt(i).setText(numberTo.elementAt(i), TextView.BufferType.EDITABLE);
            } else {
                editTextTo.elementAt(i).setHint(R.string.hint_msg_to);
            }
            linerLayoutRule.addView(editTextTo.elementAt(i));

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(300,3));
            divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            linerLayoutRule.addView(divider);
        }
    }
}
