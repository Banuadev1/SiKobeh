package com.example.sikobeh;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        String value = getIntent().getStringExtra("key");
        String value2 = getIntent().getStringExtra("key2");
        recyclerView = findViewById(R.id.recview2);
        toolbar = findViewById(R.id.toolbar2);
        jikaKosong = findViewById(R.id.idKosong);
        ket2 = findViewById(R.id.ket2_berita);
        ket2.setText("Berita dari " + value2);
        database = FirebaseDatabase.getInstance().getReference("Users").child(value).child("DataBerita");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);

        list = new ArrayList<>();
        beritaAdapter = new BeritaAdapter(this,list);
        recyclerView.setAdapter(beritaAdapter);
        Collections.reverse(list);

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
            case R.id.logout:
                SharedPreferences sharedPreferences = getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Logout Akun");
                builder.setMessage("Apakah Anda Yakin Ingin Logout?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putInt("logged", 0);
                        editor.apply();
                        Intent intent = new Intent(CekLaporan2.this, LoginKoordinator.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}