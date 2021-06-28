package com.example.sikobeh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class WartawanForm extends AppCompatActivity {

    ExtendedFloatingActionButton fLaporan;
    ImageView checkprofil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wartawan_form);

        fLaporan = (ExtendedFloatingActionButton)findViewById(R.id.bukaFLaporan);
        checkprofil = (ImageView)findViewById(R.id.profilbutton);

        fLaporan.setOnClickListener((view) ->{
            startActivity(new Intent(getApplicationContext(), WartawanInputB.class));
        });

        checkprofil.setOnClickListener((view) -> {
            startActivity(new Intent(getApplicationContext(), CheckProfilWartawan.class));
        });
    }
}