package com.example.sikobeh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginKoordinator extends AppCompatActivity {

    private Button loginb;
    private EditText email, password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_koordinator);
        loginb = (Button)findViewById(R.id.loginb);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userlogin();
            }
        });
    }

    private void userlogin()
    {
        if (email.getText().toString().equals("Koordinator@gmail.com")&&password.getText().toString().equals("123456789"))
        {
            Intent i = new Intent(LoginKoordinator.this, KoordinatorForm.class);
            progressBar.setVisibility(View.GONE);
            startActivity(i);
            finish();
        }
    }
}