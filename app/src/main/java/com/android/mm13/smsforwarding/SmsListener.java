package com.android.mm13.smsforwarding;

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

                String numberFrom = context.getSharedPreferences("data", context.MODE_PRIVATE).getString("numberFrom", "");
                if(address.endsWith(numberFrom) && numberFrom.length() > 3) {
                    //message to forward
                    String message = "From " + address + ": " + messageBody;
                    String numberTo = context.getSharedPreferences("data", context.MODE_PRIVATE).getString("numberTo", "");
                    //verify if a phone numberTo is set
                    if(numberTo.isEmpty()) {
                        Toast.makeText(context, "A phone number is not set!", Toast.LENGTH_SHORT).show();
                    } else {
                        SmsManager.getDefault().sendTextMessage(numberTo, null, message, null, null);
                        Toast.makeText(context, "A message was redirected!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
