package com.example.sikobeh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class WartawanForm extends AppCompatActivity {

    Button fLaporan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wartawan_form);

        fLaporan = (Button)findViewById(R.id.bukaFLaporan);

        fLaporan.setOnClickListener((view) ->{
            startActivity(new Intent(getApplicationContext(), WartawanInputB.class));
        });
    }
}