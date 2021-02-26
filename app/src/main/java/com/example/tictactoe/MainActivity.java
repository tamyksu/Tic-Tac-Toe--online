package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity  {
    SharedPreferences sp;
    EditText email, password;
    FirebaseAuth auth;
    String userId;
    Broad broad = new Broad();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());
     //   sp.edit().clear().commit(); //TO CLEAN THE SP FILE
        email = findViewById(R.id.email1);
        password = findViewById(R.id.password1);
        auth = FirebaseAuth.getInstance();
        //for tests
       email.setText(R.string.user1_email);
      password.setText(R.string.user1_password);
        // /email.setText(R.string.user2_email);
       // password.setText(R.string.user2_password);

       /* FirebaseDatabase.getInstance().getReference("arenas")
                .child("AAJc7iQ1aqdFkcpxaOYwXgSXW4T2").removeValue();
        FirebaseDatabase.getInstance().getReference("waiting room")
                .child("AAJc7iQ1aqdFkcpxaOYwXgSXW4T2").setValue(1);*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broad, filter);
    }


    public void loginButton(View view){
        if(email.getText().toString().equals("") || password.getText().toString().equals("")
                || email.getText().toString().contains("@") == false){
                  Toast.makeText(getApplicationContext(),
                    "email or password is empty or not in the format", Toast.LENGTH_LONG).show();
            return;
        }

        auth.signInWithEmailAndPassword(email.getText().toString(),
                password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                userId = authResult.getUser().getUid();
                MyReceiver.userId = userId;
                createNotificationChannel();
                notifications();
                Intent intent = new Intent(MainActivity.this, Loading.class);
                intent.putExtra("userId", userId);
                intent.putExtra("cameFrom", "MainActivity");
                startActivity(intent);
                finish();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "login failed! email or password are incorrect", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void tests(View view){
        FirebaseDatabase.getInstance().getReference("arenas")
                .child("AAJc7iQ1aqdFkcpxaOYwXgSXW4T2").removeValue();
        FirebaseDatabase.getInstance().getReference("waiting room")
                .child("AAJc7iQ1aqdFkcpxaOYwXgSXW4T2").setValue("1");
    }


    public void notifications(){
        //Toast.makeText(this, "Alarm worked.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
                intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long timeAtButtonClick = System.currentTimeMillis();
        long oneDayInMill = 1000*60*60*24;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                timeAtButtonClick + 8000, 4000, pendingIntent);
    }


    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myNotifyChannel";
            String description = "Channel for my notify";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("myNotify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //explicit intent - communication between activities
    public void registrationButton(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }



}

