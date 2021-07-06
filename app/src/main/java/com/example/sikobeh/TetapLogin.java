package com.example.sikobeh;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TetapLogin extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();


        if (user != null){
            startActivity(new Intent(TetapLogin.this, WartawanForm.class));
        }else {

        }
    }
}
