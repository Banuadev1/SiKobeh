package com.example.sikobeh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class WartawanForm extends AppCompatActivity {

    ExtendedFloatingActionButton fLaporan;
    ImageView checkprofil;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wartawan_form);

        fLaporan = (ExtendedFloatingActionButton)findViewById(R.id.bukaFLaporan);
        checkprofil = (ImageView)findViewById(R.id.profilbutton);

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = fAuth.getCurrentUser();


        fLaporan.setOnClickListener((view) ->{
            startActivity(new Intent(getApplicationContext(), WartawanInputB.class));
            finish();
        });

        checkprofil.setOnClickListener((view) -> {
            startActivity(new Intent(getApplicationContext(), CheckProfilWartawan.class));
            finish();
        });
    }
}