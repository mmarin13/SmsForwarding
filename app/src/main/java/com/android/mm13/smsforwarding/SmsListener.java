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

                int counter = context.getSharedPreferences("data", context.MODE_PRIVATE).getInt("counter", 1);
                for (int i = 0; i < counter; i++) {
                    String numberFrom = context.getSharedPreferences("data", context.MODE_PRIVATE).getString("numberFrom" + i, "");
                    if (address.endsWith(numberFrom) && numberFrom.length() > 3) {
                        //message to forward
                        String message = "From " + address + ": " + messageBody;
                        String numberTo = context.getSharedPreferences("data", context.MODE_PRIVATE).getString("numberTo" + i, "");
                        //verify if a phone numberTo is set
                        if (!numberTo.isEmpty()) {
                            SmsManager.getDefault().sendTextMessage(numberTo, null, message, null, null);
                            Toast.makeText(context, "A message was redirected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }
}
