package com.example.sikobeh;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CekLaporan extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    WartawanAdapter wartawanAdapter;
    ArrayList<User> list;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_laporan);

        if (WartawanAdapter.perintahDelete.equals(true)) {
            String value3 = getIntent().getStringExtra("deleteKey");
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(value3);
            String uid = db.child("uid").toString();
            FirebaseUser fr = FirebaseAuth.getInstance().getCurrentUser();
            db.removeValue();
            fr.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CekLaporan.this, "Data Wartawan berhasil dihapus, memuat data kembali",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CekLaporan.this, "Gagal menghapus data",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            StorageReference str = FirebaseStorage.getInstance().getReference().child("users/"+uid);
            str.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CekLaporan.this, "GAGAL",
                            Toast.LENGTH_LONG).show();
                }
            });
            WartawanAdapter.perintahDelete = false;
        }

        toolbar = findViewById(R.id.toolbar2);
        recyclerView = findViewById(R.id.recview);
        database = FirebaseDatabase.getInstance().getReference("Users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);

        list = new ArrayList<>();
        wartawanAdapter = new WartawanAdapter(this,list);
        recyclerView.setAdapter(wartawanAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                wartawanAdapter.notifyDataSetChanged();
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