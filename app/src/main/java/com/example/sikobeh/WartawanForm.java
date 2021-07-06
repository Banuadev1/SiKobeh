package com.example.sikobeh;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
    FirebaseAuth fAuth;
    StorageReference storageReference;
    FirebaseUser user;
    DataBeritaAdapter dataBeritaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wartawan_form);
        fLaporan = findViewById(R.id.bukaFLaporan);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profil_navigation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.checprofil:
                startActivity(new Intent(getApplicationContext(), CheckProfilWartawan.class));
                return true;
            case R.id.logout:
                AlertDialog.Builder logout = new AlertDialog.Builder(this);
                logout.setTitle("LogOut Akun");
                logout.setMessage("Apakah Anda Yakin Ingin Logout?");
                logout.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();//logout
                        Intent intent = new Intent(WartawanForm.this, LoginWartawan.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                logout.create().show();
        }
        return super.onOptionsItemSelected(item);
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