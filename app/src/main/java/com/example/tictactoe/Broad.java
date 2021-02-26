package com.example.tictactoe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class Broad extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE"))
        {
         if(isConnected(context))
             Toast.makeText(context, "Network ON", Toast.LENGTH_LONG).show();

          else Toast.makeText(context, "Network OFF", Toast.LENGTH_LONG).show();
        }
    }


    public boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }
}







