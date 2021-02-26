package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//this is where the loading screen is loaded and the app retrieves the data of the user from the FB
public class Loading extends AppCompatActivity {

    String userId, userName, userScore, cameFrom = "MainActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);//fades animation
        setContentView(R.layout.progressbar_layout);

        userId = getIntent().getStringExtra("userId");
        cameFrom = getIntent().getStringExtra("cameFrom");
        GetScoreFromFBRunnable runnable = new GetScoreFromFBRunnable();
        new Thread(runnable).start();
    }


    public void startActivityLogin() {//used in ProgressBarAnimation class

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("userScore", userScore);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        startActivity(intent);
        finish();
    }


    private class GetScoreFromFBRunnable implements Runnable{

        @Override
        public void run() {

            if(cameFrom.equals("TypeGame")){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userScore = getIntent().getStringExtra("userScore");
                userName = getIntent().getStringExtra("userName");
                startActivityLogin();
            }
            else{//from main activity to login activity
                DatabaseReference usersRef = FirebaseDatabase.getInstance()
                        .getReference("users").child(userId);//with id we got from main activity we can get name and score from FB
                ValueEventListener eventListener = new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.getValue() != null){
                            userScore = (String) snapshot.child("score").getValue();
                            userName = (String) snapshot.child("name").getValue();
                        }
                        startActivityLogin();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                };
                usersRef.addListenerForSingleValueEvent(eventListener);
            }
        }
    }

}
