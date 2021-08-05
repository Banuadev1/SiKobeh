package com.example.sikobeh;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

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
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginb = findViewById(R.id.loginb);
        progressBar = findViewById(R.id.progressbar);
        lPassword = findViewById(R.id.lihatPassword);
        mAuth = FirebaseAuth.getInstance();
        forgotpassword = findViewById(R.id.forgotpassword);

        forgotpassword.setOnClickListener(v -> reset());

        lPassword.setOnClickListener(v -> {
            if(lPassword.isChecked()){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        loginb.setOnClickListener(v -> {
            if (v.getId() == R.id.loginb) {
                UserLogin();
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
        mAuth.signInWithEmailAndPassword(email1, password1).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                try {
                    RegisterUser.encryptedPass = RegisterUser.encrypt(password1, "rasul19");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String pass = RegisterUser.encryptedPass;
                String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("password", pass);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(auth);
                ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        email.setText("");
                        password.setText("");
                        openafterLogin();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(LoginWartawan.this, "Login Error : Akun Belum Di Daftarkan!!", Toast.LENGTH_LONG, true).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void openafterLogin(){
        Intent intent = new Intent(this, WartawanForm.class);
        startActivity(intent);
        finish();
    }

}