package com.example.moisiimarin.smsforwarding;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

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

        if(number.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You must enter a phone number", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Phone number is set!", Toast.LENGTH_SHORT).show();
        }
    }
}
