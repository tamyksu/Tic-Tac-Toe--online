package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;


public class FindRival extends AppCompatActivity {
    Table table = new Table();
    String guestOrHost, userId, rivalId="";
    long startingTime, timeToWaitInMillis = 500;//1000 millis = 1 sec
    boolean isWaiting = true, wasInWaitingRoom = false;//not to wait more than once per game
    boolean rivalWasFound = false, timeWasOver = false;
    ValueEventListener eventListener = null;
    DatabaseReference waitingRoomRefrence;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setContentView(R.layout.progressbar_layout);//change screen to progressbar while waiting for rival

        userId = getIntent().getStringExtra("userId");
       // startingTime = System.currentTimeMillis();

        ChecksIfTimeRunsOut runnable = new ChecksIfTimeRunsOut();
        new Thread(runnable).start();
        findRival(false);
    }

    public void findRival(boolean isSecondTime){
        waitingRoomRefrence = FirebaseDatabase.getInstance()
                .getReference("waiting room");

        eventListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String usersWaiting="";

                if(snapshot.getChildrenCount() == 0){
                    guestOrHost = "guest";
                    if(wasInWaitingRoom == true){//a host took me for his rival
                        isWaiting = false;
                    }
                    else{//waiting for the first time
                        waitingRoomRefrence.child(userId).setValue("null");
                        isWaiting = true;
                        wasInWaitingRoom = true;
                    }
                }
                else{//we have someone in waitting room
                    if(wasInWaitingRoom == true){//if I was in watting room = im guest
                        guestOrHost = "guest";
                        isWaiting = false;//inititilize to false and then check, if we not in waiting room some host took us from the room
                        for(DataSnapshot waitingUser : snapshot.getChildren()){
                            if(waitingUser.getKey().equals(userId) == true){
                                isWaiting = true;//if we in waiting room put true
                            }
                        }
                    }
                    else{
                        guestOrHost = "host";
                        isWaiting = false;
                        for(DataSnapshot waitingUser : snapshot.getChildren()){
                           rivalId = waitingUser.getKey();//get the user from waiting room
                           break;
                        }
                    }
                }

                if(isWaiting == false){
                    rivalWasFound = true;
                    finishFinding(true);
                }
                else if(timeWasOver == true){
                        finishFinding(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        if(isSecondTime == false)
            waitingRoomRefrence.addValueEventListener(eventListener);
        else
            waitingRoomRefrence.addListenerForSingleValueEvent(eventListener);
    }


    private void finishFinding(boolean isFound){

        waitingRoomRefrence.removeEventListener(eventListener);
        DatabaseReference arenasReference = FirebaseDatabase.getInstance()
                .getReference("arenas");

        if(isFound == false){//the time for connection is over
            waitingRoomRefrence.child(userId).removeValue().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {//if someone removed the user before for his rival
                    findRival(true);
                    return;
                }
            });
            Toast.makeText(getApplicationContext(), "No Rival Was Found", Toast.LENGTH_LONG).show();
            wasInWaitingRoom = false;
            Intent intent = new Intent(getApplicationContext(), Loading.class);
            intent.putExtra("userId", userId);
            intent.putExtra("cameFrom", "FindRival");
            intent.putExtra("userName", getIntent().getStringExtra("userName"));
            startActivity(intent);
            finish();
        }

        else{//found a match
            if(guestOrHost.equals("guest")){
                try {
                    Thread.sleep(300);//to wait for the host to build the arena
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                waitingRoomRefrence.child(rivalId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //arenasReference.child(rivalId).child("turn").setValue(rivalId);
                        //guest always starts
                        arenasReference.child(rivalId).child("play again").setValue("null");
                        arenasReference.child(rivalId).child("table").setValue(table.map);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {//if someone removed the user before for his rival
                        isWaiting = true;
                        findRival(false);
                    }
                });
            }

            try {
                Thread.sleep(500);//load the arena (onSuccess) before the next activity
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(), TypeGame.class);
            intent.putExtra("background", getIntent().getStringExtra("background"));
            intent.putExtra("userId", userId);
            intent.putExtra("playerX",  getIntent().getIntExtra("playerX",R.drawable.x));
            intent.putExtra("playerO",  getIntent().getIntExtra("playerO",R.drawable.circle));
            if(guestOrHost.equals("host"))
                intent.putExtra("rivalId", rivalId);
            intent.putExtra("guestOrHost", guestOrHost);
            intent.putExtra("userScore", getIntent().getStringExtra("userScore"));
            intent.putExtra("userName", getIntent().getStringExtra("userName"));
            startActivity(intent);
            finish();
        }
    }


    public class Table{
        public Map<String, Integer> map = new HashMap<>();

        public Table(){
            map.put("0", 2);
            map.put("1", 2);
            map.put("2", 2);
            map.put("3", 2);
            map.put("4", 2);
            map.put("5", 2);
            map.put("6", 2);
            map.put("7", 2);
            map.put("8", 2);
        }
    }

    private class ChecksIfTimeRunsOut implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(timeToWaitInMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(rivalWasFound == false){
                waitingRoomRefrence.removeEventListener(eventListener);
                timeWasOver = true;
                waitingRoomRefrence.addListenerForSingleValueEvent(eventListener);
                return;
            }
        }
    }
}
