package com.example.sikobeh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button loginkoordinator, loginwartawan;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        loginkoordinator = findViewById(R.id.LoginKoordinator);
        loginwartawan = findViewById(R.id.LoginWartawan);

        loginkoordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KoordinatorOpen();
            }
        });

        /*/loginwartawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WartawanOpen();
            }
        });*/
    }

    public void KoordinatorOpen(){
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
        int j = sharedPreferences.getInt("logged", 0);
        if(j > 0){
            Intent intent = new Intent(this, KoordinatorForm.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(this, LoginKoordinator.class);
            startActivity(intent);
        }
    }

    /*/public void WartawanOpen(){
        Intent intent = new Intent(this, LoginWartawan.class);
        startActivity(intent);
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();

        loginwartawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null){
                    startActivity(new Intent(MainActivity.this, WartawanForm.class));
                    finish();
                }
                else{
                    startActivity(new Intent(MainActivity.this, LoginWartawan.class));
                }
            }
        });
    }
}