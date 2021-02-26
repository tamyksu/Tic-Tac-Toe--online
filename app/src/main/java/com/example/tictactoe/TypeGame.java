package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TypeGame extends AppCompatActivity {
    int activePlayer =0, userScore, indexToChange, shapeForDB = 0;//X
    int[] arr= {2,2,2,2,2,2,2,2,2};
    int [][] winningPositions ={{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    boolean overGame= false, didIWin = false, playAgain = false;
    boolean flagMusic = true, waitForPlayAgain = true;
    String guestOrHost, arenaId, userId, playerX, playerO;
    DatabaseReference  tableReference, playAgainRef;
    ValueEventListener eventTable, eventPlayAgain;
    ConstraintLayout mConstraintLayout;
    Handler handler = new Handler(Looper.getMainLooper());
    MediaPlayer musicPlayer;
    TextView turns;
    ImageView [] tableImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_type_game);

        playerX = getIntent().getStringExtra("playerX");
        playerO = getIntent().getStringExtra("playerO");
        musicPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound);
        musicPlayer.setLooping(true); // Set looping
        musicPlayer.setVolume(1.0f, 1.0f);
        musicPlayer.start();
        tableImages = new ImageView[9];
        userScore = Integer.parseInt(getIntent().getStringExtra("userScore"));
        guestOrHost = getIntent().getStringExtra("guestOrHost");
        userId = getIntent().getStringExtra("userId");

        if(guestOrHost.equals("host")){
            arenaId = getIntent().getStringExtra("rivalId");
            shapeForDB = 1;//"O"
        }
        else
            arenaId = userId;
        tableImages[0] = (ImageView)findViewById(R.id.imageView11);
        tableImages[1] = (ImageView)findViewById(R.id.imageView12);
        tableImages[2] = (ImageView)findViewById(R.id.imageView13);
        tableImages[3] = (ImageView)findViewById(R.id.imageView14);
        tableImages[4] = (ImageView)findViewById(R.id.imageView15);
        tableImages[5] = (ImageView)findViewById(R.id.imageView16);
        tableImages[6] = (ImageView)findViewById(R.id.imageView17);
        tableImages[7] = (ImageView)findViewById(R.id.imageView18);
        tableImages[8] = (ImageView)findViewById(R.id.imageView19);
        for(ImageView iv : tableImages)
            iv.setAlpha(1.0f);

        turns = (TextView)findViewById(R.id.turnsTextView);
        turns.setVisibility(View.VISIBLE);
        LinearLayout playAgainLayout = (LinearLayout)findViewById(R.id.playAgainLayout);
        playAgainLayout.setVisibility(View.INVISIBLE);
        LinearLayout resultScreen = (LinearLayout)findViewById(R.id.resultContainer);
        resultScreen.setVisibility(View.INVISIBLE);
        TextView background = (TextView)findViewById(R.id.backgroundScreen);
        background.setVisibility(View.INVISIBLE);
        mConstraintLayout = (ConstraintLayout)findViewById(R.id.background);
        String value = getIntent().getStringExtra("background"); // or other values

       switch (value){//put background
            case "beach" :
                mConstraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.beach));
                break;
            case "ski" :
                mConstraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.ski));
                break;
            case "picnic" :
                mConstraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.picnic));
                break;
            case "forest" :
                mConstraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.forest));
                break;
            case "airplane" :
                mConstraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.airplane));
                break;
            case "water_park" :
                mConstraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.water_park));
                break;
           default:
        }

        listenerGame();
        initTableRefAndEvent();
        if(guestOrHost.equals("host")){
            activePlayer = 1;
            waitingForRival();
            turns.setText(R.string.waiting_for_rival);
        }
        else{
            activePlayer = 0;
            myTurnToPlay();
            turns.setText(R.string.your_move);
        }
    }

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playAgain==false)
            playAgainRef.setValue("left");
    }*/

    public void listenerGame(){

        playAgainRef = FirebaseDatabase.getInstance().getReference("arenas")
                .child(arenaId).child("play again");

        eventPlayAgain = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String value = (String)snapshot.getValue();
                if(value.equals("left") == true)
                {
                    musicPlayer.pause();
                    Toast.makeText(getApplicationContext(), "Your Rival Left The Game", Toast.LENGTH_LONG).show();
                    playAgainRef.removeEventListener(eventPlayAgain);
                    goBackToLogin();
                }}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        playAgainRef.addValueEventListener(eventPlayAgain);

    }


    private void initTableRefAndEvent(){

        tableReference = FirebaseDatabase.getInstance().getReference("arenas")
                .child(arenaId).child("table");

        eventTable = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                indexToChange=-1;
                int value;
                for(DataSnapshot image : snapshot.getChildren()){
                    indexToChange = Integer.parseInt(image.getKey());
                    value = Integer.parseInt(image.getValue().toString());
                    if(arr[indexToChange] != value)
                        break;
                    else
                        indexToChange = -1;//all table initilize to -1
                }
                if(indexToChange != -1)//someone make his turn
                    updateImageOnTable(indexToChange);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }


    public void waitingForRival(){
        for(ImageView image : tableImages)
            image.setOnClickListener(null);

        tableReference.addValueEventListener(eventTable);
        turns.setText(R.string.waiting_for_rival);
    }


    public void showAlertDialog() {
       // musicPlayer.pause();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Return to profile screen?");
        alert.setMessage("Are you sure? ");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                flagMusic = false;

                waitForPlayAgain=false;
                FirebaseDatabase.getInstance().getReference("arenas")
                        .child(arenaId).child("play again").setValue("left");

                Intent intent = new Intent(getApplicationContext(), Loading.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userScore", String.valueOf(userScore));
                intent.putExtra("cameFrom", "TypeGame");
                intent.putExtra("userName", getIntent().getStringExtra("userName"));
               // musicPlayer.pause();
                startActivity(intent);
                finish();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.create().show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                showAlertDialog();
                return true;
            case R.id.music:
                if(flagMusic==true)
                {
                    musicPlayer.pause();
                    flagMusic = false;
                }
                else {
                    musicPlayer.start();
                    flagMusic = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }}


    private void updateImageOnTable(int indexImage){
        ImageView counter = tableImages[indexImage];

        if(arr[indexImage]==2 && overGame == false ) {
            counter.setTranslationY(-1000f);

            if (activePlayer == 0) {
                counter.setImageResource(getIntent().getIntExtra("playerX",R.drawable.x));//if playerX no value image put in default x
                tableReference.child(String.valueOf(indexImage)).setValue(shapeForDB);
                if(guestOrHost.equals("host"))
                    arr[indexImage]=1;
                else
                    arr[indexImage]=0;
                activePlayer = 1;
                waitingForRival();
            } else {
                counter.setImageResource(getIntent().getIntExtra("playerO",R.drawable.circle));
                if(guestOrHost.equals("host"))
                    arr[indexImage]=0;
                else
                    arr[indexImage]=1;
                activePlayer = 0;
                myTurnToPlay();
            }
            counter.animate().translationYBy(1000f).rotation(360).setDuration(300);
            for (int[] index: winningPositions)
            {
                if (arr[index[0]]==arr[index[1]] && arr[index[1]]==arr[index[2]] && arr[index[0]] != 2)
                {
                    overGame=true;
                    ImageView imageView = (ImageView) findViewById(R.id.resultImageView);
                    TextView textView = (TextView)findViewById(R.id.resultTextView);
                    if(activePlayer == 1){
                        imageView.setImageResource(R.drawable.trophy);
                        textView.setText(R.string.victory);
                        didIWin = true;
                    }
                    else{
                        imageView.setImageResource(R.drawable.knockout);
                        textView.setText(R.string.you_lost);
                    }

                    if(didIWin == true){
                        userScore = userScore + 5;
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(userId).child("score").setValue(String.valueOf(userScore));
                    }

                    for(ImageView iv : tableImages)
                        iv.setAlpha(0.3f);
                    tableImages[index[0]].setAlpha(1.0f);
                    tableImages[index[1]].setAlpha(1.0f);
                    tableImages[index[2]].setAlpha(1.0f);

                    ShowResults showResults = new ShowResults();
                    new Thread(showResults).start();
                    ShowPlayAgainScreen showPlayAgainScreen = new ShowPlayAgainScreen();
                    new Thread(showPlayAgainScreen).start();
                }
            }
            if(overGame == false){
                for(int slot : arr){
                    if(slot == 2)
                        return;
                }


                ImageView imageView = (ImageView) findViewById(R.id.resultImageView);
                TextView textView = (TextView)findViewById(R.id.resultTextView);

                imageView.setImageResource(R.drawable.draw);
                textView.setText(R.string.draw);

                ShowResults showResults = new ShowResults();
                new Thread(showResults).start();
                ShowPlayAgainScreen showPlayAgainScreen = new ShowPlayAgainScreen();
                new Thread(showPlayAgainScreen).start();
            }
                turns.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return  true;
    }


    private void myTurnToPlay(){
        tableReference.removeEventListener(eventTable);

        for(int i=0 ; i<9 ; i++)
            if(arr[i] == 2)
                tableImages[i].setOnClickListener(this::dropin);//put lisener only on empty places in board
        turns.setText(R.string.your_move);
    }


    public void dropin(View view){
        ImageView counter = (ImageView) view;

        updateImageOnTable(Integer.parseInt(counter.getTag().toString()));
    }


    public void goBackToLogin(){
        musicPlayer.pause();
        FirebaseDatabase.getInstance().getReference("arenas").child(arenaId).removeValue();

        Intent intent = new Intent(getApplicationContext(), Loading.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userScore", String.valueOf(userScore));
        intent.putExtra("cameFrom", "TypeGame");
        intent.putExtra("userName", getIntent().getStringExtra("userName"));
        startActivity(intent);
        finish();
    }


    public void homeButton(View view){
        goBackToLogin();
    }


    public void setPlayAgain(){
        FindRival table = new FindRival();
        tableReference.setValue(table.table.map);
        musicPlayer.pause();
        overGame=false;
        Intent intent = new Intent(getApplicationContext(), TypeGame.class);
        intent.putExtra("background", getIntent().getStringExtra("background"));
        intent.putExtra("userId", userId);
        if(guestOrHost.equals("host"))
            intent.putExtra("rivalId", arenaId);
        intent.putExtra("guestOrHost", guestOrHost);
        intent.putExtra("userScore", String.valueOf(userScore));
        intent.putExtra("playerX",  getIntent().getIntExtra("playerX",R.drawable.x));
        intent.putExtra("playerO",  getIntent().getIntExtra("playerO",R.drawable.circle));
        intent.putExtra("userName", getIntent().getStringExtra("userName"));
        startActivity(intent);
        finish();
    }


    public void playAgain(View view){
        playAgainRef = FirebaseDatabase.getInstance().getReference("arenas")
                .child(arenaId).child("play again");
        playAgainRef.removeEventListener(eventPlayAgain);
        Toast.makeText(getApplicationContext(), "Waiting For Rival...", Toast.LENGTH_LONG).show();
        musicPlayer.pause();

        eventPlayAgain = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                        String value = (String)snapshot.getValue();
                      value="AAJc7iQ1aqdFkcpxaOYwXgSXW4T2";
                        waitForPlayAgain=true;
                        if(waitForPlayAgain == false){
                            Toast.makeText(getApplicationContext(), "Your Rival Left The Game", Toast.LENGTH_LONG).show();
                            playAgainRef.removeEventListener(eventPlayAgain);
                            playAgainRef.setValue("left");
                            goBackToLogin();
                        }
                        else{
                            if(value.equals(userId) == false && value.equals("null") == false){
                                Toast.makeText(getApplicationContext(), "Starting New Game...", Toast.LENGTH_LONG).show();
                                playAgain = true;
                                playAgainRef.setValue(userId);
                                playAgainRef.removeEventListener(eventPlayAgain);
                                setPlayAgain();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        playAgainRef.addValueEventListener(eventPlayAgain);
        playAgainRef.setValue(userId);

        ChecksIfTimeRunsOut checksIfTimeRunsOut = new ChecksIfTimeRunsOut();
        new Thread(checksIfTimeRunsOut).start();
    }


    private class ShowResults implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference("arenas").child(arenaId)
                            .child("play again").setValue("null");
                    LinearLayout resultScreen = (LinearLayout)findViewById(R.id.resultContainer);
                    resultScreen.setVisibility(View.VISIBLE);
                    TextView background = (TextView)findViewById(R.id.backgroundScreen);
                    background.setVisibility(View.VISIBLE);
                    Animation fadeInForPlayAgainLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein_play_again);
                    resultScreen.startAnimation(fadeInForPlayAgainLayout);
                }
            });
        }
    }



    private class ShowPlayAgainScreen implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout layout = (LinearLayout)findViewById(R.id.playAgainLayout);
                    layout.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    private class ChecksIfTimeRunsOut implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(playAgain == false){
                waitForPlayAgain = false;
                playAgainRef.addListenerForSingleValueEvent(eventPlayAgain);
            }
        }
    }


}