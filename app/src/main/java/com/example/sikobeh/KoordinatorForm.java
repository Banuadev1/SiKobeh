package com.example.sikobeh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class KoordinatorForm extends AppCompatActivity {

    private Button register, laporan;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_koordinator_form);
        register = (Button)findViewById(R.id.registerUser);
        laporan = (Button)findViewById(R.id.cekLaporan);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Register(); }
        });

        laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Laporan(); }
        });
    }

    public void Register(){
        Intent intent = new Intent(this, RegisterUser.class);
        startActivity(intent);
    }

    public void Laporan(){
        Intent intent = new Intent(this, CekLaporan.class);
        startActivity(intent);
    }
}