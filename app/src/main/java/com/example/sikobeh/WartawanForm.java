package com.example.sikobeh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class WartawanForm extends AppCompatActivity {

    ExtendedFloatingActionButton fLaporan;
    RecyclerView recyclerView;
    ImageView checkprofil;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    FirebaseUser user;
    DataBeritaAdapter dataBeritaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wartawan_form);

        fLaporan = findViewById(R.id.bukaFLaporan);
        checkprofil = findViewById(R.id.profilbutton);
        recyclerView = findViewById(R.id.listBerita);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = fAuth.getCurrentUser();

        FirebaseRecyclerOptions<Berita> data = new FirebaseRecyclerOptions.Builder<Berita>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("DataBerita"), Berita.class)
                .build();

        dataBeritaAdapter = new DataBeritaAdapter(data);
        recyclerView.setAdapter(dataBeritaAdapter);


        fLaporan.setOnClickListener((view) ->{
            startActivity(new Intent(getApplicationContext(), WartawanInputB.class));
        });

        checkprofil.setOnClickListener((view) -> {
            startActivity(new Intent(getApplicationContext(), CheckProfilWartawan.class));;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataBeritaAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataBeritaAdapter.stopListening();
    }
}