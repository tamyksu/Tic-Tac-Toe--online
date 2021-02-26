package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password, nickname;
    Button register;
   FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        nickname = findViewById(R.id.nickname);

        auth = FirebaseAuth.getInstance();
        // 3-Anonymous inner Class
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_name = nickname.getText().toString();

                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }else if (txt_password.length()<5){
                    Toast.makeText(RegisterActivity.this, "Password short!", Toast.LENGTH_SHORT).show();
                }else {
                    registerUser(txt_email,txt_password,txt_name);
                }}
       });

    }


    public void backToMainActivity(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


  private void registerUser(String email, String password, String name){
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>(){
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                FirebaseUser user = auth.getCurrentUser();
                String userID = user.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
                Map<String, String> userData = new HashMap<>();
              //  userData.put("userId", userID);
                userData.put("name", name);
                userData.put("email", email);
                userData.put("password", password);
                userData.put("score", "0");

                String emailWithoutDots = email;
                while(emailWithoutDots.equals(emailWithoutDots.replace(".", "_")) == false)
                    emailWithoutDots = emailWithoutDots.replace(".", "_");
                reference.setValue(userData);
                Toast.makeText(RegisterActivity.this, "Register sucssesfully",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }else {
                Toast.makeText(RegisterActivity.this, "registration failled", Toast.LENGTH_SHORT).show();
            }
        }
    });

    }
}


