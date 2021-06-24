package com.example.sikobeh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginWartawan extends AppCompatActivity {

    private TextView forgotpassword;
    private EditText email, password;
    private Button loginb;
    private CheckBox lPassword;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_wartawan);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        loginb = (Button)findViewById(R.id.loginb);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        lPassword = (CheckBox) findViewById(R.id.lihatPassword);
        mAuth = FirebaseAuth.getInstance();
        forgotpassword = (TextView)findViewById(R.id.forgotpassword);

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        lPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lPassword.isChecked()){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.loginb:
                        UserLogin();
                        break;
                }
            }
        });
    }

    public void reset(){
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    public void UserLogin(){
        String email1 = email.getText().toString().trim();
        String password1 = password.getText().toString().trim();

        if (email1.isEmpty()){
            email.setError("Email anda masih Kosong");
            progressBar.setVisibility(View.GONE);
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
            email.setError("Email anda tidak Valid");
            progressBar.setVisibility(View.GONE);
            email.requestFocus();
        }

        if (password1.isEmpty()){
            password.setError("Password anda Masih Kosong");
            password.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (password1.length() < 6){
            password.setError("Password anda tidak boleh kurang dari 6 karakter");
            password.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()){
                        openafterLogin();
                        progressBar.setVisibility(View.GONE);
                    }
                    else {
                        user.sendEmailVerification();
                        Toast.makeText(LoginWartawan.this, "Periksa Email anda untuk verifikasi!!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void openafterLogin(){
        Intent intent = new Intent(this, WartawanForm.class);
        startActivity(intent);
    }
}