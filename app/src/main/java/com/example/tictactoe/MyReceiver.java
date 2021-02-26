package com.example.tictactoe;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyReceiver extends BroadcastReceiver {

    static String userId;
    Intent intent1;
    Context context;
    PendingIntent pendingIntent;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(userId == null)
            return;

        this.context = context;
        intent1 = new Intent(context, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 1,
                intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager = NotificationManagerCompat.from(context);


        DatabaseReference usersRef = FirebaseDatabase.getInstance()
                .getReference("users").child(userId).child("score");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String userScore = "";
                if (snapshot.getValue() != null)
                    userScore = ((String) snapshot.getValue());

                initNotification(userScore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.addListenerForSingleValueEvent(eventListener);
    }


    private void initNotification(String score){

        builder = new NotificationCompat.Builder(this.context, "myNotify")
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("Tic Tac Toe")
                .setContentText("Your score is:  " + score)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        notificationManager.notify(200, builder.build());
    }

}
