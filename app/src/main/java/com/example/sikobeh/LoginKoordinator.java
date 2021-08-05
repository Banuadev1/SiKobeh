package com.example.sikobeh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;

public class LoginKoordinator extends AppCompatActivity {

    private Button loginb;
    private EditText email, password;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private CheckBox lPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_koordinator);
        loginb = findViewById(R.id.loginb);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressbar);
        lPassword = findViewById(R.id.lihatPassword);

        lPassword.setOnClickListener(v -> {
            if(lPassword.isChecked()){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

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
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(LoginKoordinator.this, KoordinatorForm.class));
            finish();
            editor.putInt("logged", 1);
            editor.apply();
        }
        else{
            Toasty.error(LoginKoordinator.this, "Login Gagal Periksa Email atau Password anda!!", Toast.LENGTH_LONG, true).show();
        }
    }
}