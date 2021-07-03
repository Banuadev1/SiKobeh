package com.example.sikobeh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button loginkoordinator, loginwartawan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginkoordinator = findViewById(R.id.LoginKoordinator);
        loginwartawan = findViewById(R.id.LoginWartawan);

        loginkoordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KoordinatorOpen();
            }
        });

        loginwartawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WartawanOpen();
            }
        });
    }

    public void KoordinatorOpen(){
        Intent intent = new Intent(this, LoginKoordinator.class);
        startActivity(intent);
    }

    public void WartawanOpen(){
        Intent intent = new Intent(this, LoginWartawan.class);
        startActivity(intent);
    }

}