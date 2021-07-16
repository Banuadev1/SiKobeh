package com.example.sikobeh;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class CekLaporan2 extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    BeritaAdapter beritaAdapter;
    ArrayList<Berita> list;
    TextView jikaKosong, ket2;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_laporan2);

        if (WartawanAdapter.clickBerita == true) { WartawanAdapter.clickBerita = false; }

        String value = getIntent().getStringExtra("key");
        String value2 = getIntent().getStringExtra("key2");
        recyclerView = findViewById(R.id.recview2);
        toolbar = findViewById(R.id.toolbar2);
        jikaKosong = findViewById(R.id.idKosong);
        ket2 = findViewById(R.id.ket2_berita);
        ket2.setText("Berita dari " + value2);
        database = FirebaseDatabase.getInstance().getReference("DataBerita").child(value);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        setSupportActionBar(toolbar);

        list = new ArrayList<>();
        beritaAdapter = new BeritaAdapter(this,list);
        recyclerView.setAdapter(beritaAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            jikaKosong.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            jikaKosong.setVisibility(View.VISIBLE);
                        }
                    });
                    Berita berita = dataSnapshot.getValue(Berita.class);
                    list.add(berita);
                }
                beritaAdapter.notifyDataSetChanged();
                Collections.reverse(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.koordinator_navigation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menunav:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}