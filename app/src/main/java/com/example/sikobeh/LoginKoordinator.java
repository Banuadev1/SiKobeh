package com.example.sikobeh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginKoordinator extends AppCompatActivity {

    private Button loginb;
    private EditText email, password;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_koordinator);
        loginb = findViewById(R.id.loginb);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressbar);

        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userlogin();
            }
        });
    }

    private void userlogin()
    {
        sharedPreferences = getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (email.getText().toString().equals("Koordinator@gmail.com")&&password.getText().toString().equals("123456789"))
        {
            Intent i = new Intent(LoginKoordinator.this, KoordinatorForm.class);
            progressBar.setVisibility(View.GONE);
            startActivity(i);
            finish();
            editor.putInt("logged", 1);
            editor.apply();
        }
        else{
            Toast.makeText(LoginKoordinator.this, "Login Gagal Periksa Email atau Password anda!!", Toast.LENGTH_LONG).show();
        }
    }
}