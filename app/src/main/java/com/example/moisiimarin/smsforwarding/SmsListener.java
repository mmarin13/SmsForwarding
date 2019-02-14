package com.example.moisiimarin.smsforwarding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for(SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody();
                String address = smsMessage.getOriginatingAddress();
                Log.d("message", messageBody);
                Log.d("address", address);

                //message to forward
                String message = "From " + address + ": " + messageBody;

                //verify if a phone number is set
                String number = context.getSharedPreferences("data", context.MODE_PRIVATE).getString("number", "");
                if(number.isEmpty()) {
                    Toast.makeText(context, "A phone number is not set!", Toast.LENGTH_SHORT).show();
                } else {
                    SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
                    Toast.makeText(context, "A message was redirected!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
