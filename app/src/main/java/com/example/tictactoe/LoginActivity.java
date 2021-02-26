package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements FragmentBackgrounds.fragInterface ,Dialog1.MyDialogFragmentListener{
    SharedPreferences sp;
    TextView stored_score, textOnProgressBar, levelTextView, userName;
    ImageView playerX, playerO;
    MediaPlayer player;
    Button play;
    boolean flagMusic = true;
    ProgressBar progressBar;
    int userLevel = 1, playerX_id, playerO_id;
    //String [] icon_players = {"x", "circle"};
    String [] pointsForLevelUps =
            new String[]{"10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());

        if(sp.getInt("playerX", -1) == -1)
            sp.edit().putInt("playerX", R.drawable.x).commit();
        if(sp.getInt("playerO", -1) == -1)
            sp.edit().putInt("playerO", R.drawable.circle).commit();

        playerX = findViewById(R.id.imageX);
        playerO = findViewById(R.id.imageO);
        playerX_id= sp.getInt("playerX", 0);
        playerO_id= sp.getInt("playerO", 0);
        playerX.setImageResource(playerX_id);
        playerO.setImageResource(playerO_id);

        player = MediaPlayer.create(getApplicationContext(), R.raw.sound);
        if (player.isPlaying()==false) {
        player.setLooping(true); // Set looping
        player.setVolume(1.0f, 1.0f);
        player.start();//make sound
         }
        stored_score = (TextView)findViewById(R.id.score);
        stored_score.setText(getIntent().getStringExtra("userScore"));

        userName = (TextView)findViewById(R.id.userName);
        String temp = getIntent().getStringExtra("userName");
        userName.setText(temp + "'s Profile");

        levelTextView = (TextView)findViewById(R.id.textLevel);
        progressBar = (ProgressBar) findViewById(R.id.progressbarLevel);
        textOnProgressBar = (TextView)findViewById(R.id.progressbarLevelText);

        updateProgress();

        play = (Button) findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                setContentView(R.layout.arena);
            }
        });

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
                    player.pause();
                    flagMusic = false;
                }
                else {
                    player.start();
                    flagMusic = true;
                }
                return true;

            case R.id.player1:

                FragmentManager fm = getSupportFragmentManager();
                Dialog1 editNameDialog = new Dialog1(0);
                editNameDialog.show(fm, "fragment_edit_name");

                return true;

            case R.id.player2:

                FragmentManager fm1 = getSupportFragmentManager();
                Dialog1 editNameDialog1 = new Dialog1(1);
                editNameDialog1.show(fm1, "fragment_edit_name");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void backFromChooseArena(View view){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("userName", getIntent().getStringExtra("userName"));
        intent.putExtra("userScore", getIntent().getStringExtra("userScore"));
        intent.putExtra("userId", getIntent().getStringExtra("userId"));
        startActivity(intent);
        player.pause();
        finish();
    }


    public void showAlertDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Closing the application");
        alert.setMessage("Are you sure? ");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);//Leave the application
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        alert.create().show();
    }


    @Override//create menu
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.local, menu);
        return  true;
    }


    @Override//returning from dialog class
    public void onReturnValue(String icon, int i) {
      //  icon_players[i]=icon;
        if(i == 0){
            playerX_id = playerX.getContext().getResources().getIdentifier(icon , "drawable", playerX.getContext().getPackageName());
            playerX.setImageResource(playerX_id);
            sp.edit().putInt("playerX", playerX_id).commit();
        }
        else {
            playerO_id = playerO.getContext().getResources().getIdentifier(icon , "drawable", playerO.getContext().getPackageName());
            playerO.setImageResource(playerO_id);
            sp.edit().putInt("playerO", playerO_id).commit();
        }
    }


    private void updateProgress(){
        int userPoints = Integer.parseInt(getIntent().getStringExtra("userScore"));
        int pointsForNextLevel = Integer.parseInt(pointsForLevelUps[userLevel-1]);

        while(userPoints >= pointsForNextLevel){
            userPoints = userPoints - pointsForNextLevel;
            userLevel++;
            if(userLevel == 10){//its max level
                progressBar.setProgress(1);
                progressBar.setMax(1);
                textOnProgressBar.setText("MAX LEVEL");
                levelTextView.setText(String.valueOf(userLevel));
                return;
            }
            pointsForNextLevel = Integer.parseInt(pointsForLevelUps[userLevel-1]);
        }
        progressBar.setProgress(userPoints);
        progressBar.setMax(pointsForNextLevel);
        levelTextView.setText(String.valueOf(userLevel));
        textOnProgressBar.setText(userPoints + " / " + pointsForNextLevel);

    }

//ContractsAdapter - we have on click item , we have lisener of FragmentBackgrounds that call createActivity
    public void createActivity(String name) {//on click item get to here
        Intent intent = new Intent(LoginActivity.this, FindRival.class);
        player.pause();
        flagMusic = false;
        intent.putExtra("background", name); //Put your id to your next Intent
        intent.putExtra("userId", getIntent().getStringExtra("userId"));
        intent.putExtra("userScore", getIntent().getStringExtra("userScore"));
        intent.putExtra("userName", getIntent().getStringExtra("userName"));
        intent.putExtra("playerX", playerX_id);
        intent.putExtra("playerO", playerO_id);
        startActivity(intent);
        finish();

    }

}







