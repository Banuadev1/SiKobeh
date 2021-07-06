package com.example.sikobeh;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CekLaporan2 extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    BeritaAdapter beritaAdapter;
    ArrayList<Berita> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_laporan2);
        String value = getIntent().getStringExtra("key");
        recyclerView = findViewById(R.id.recview2);
        database = FirebaseDatabase.getInstance().getReference("Users").child(value).child("DataBerita");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        beritaAdapter = new BeritaAdapter(this,list);
        recyclerView.setAdapter(beritaAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Berita berita = dataSnapshot.getValue(Berita.class);
                    list.add(berita);
                }
                beritaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}