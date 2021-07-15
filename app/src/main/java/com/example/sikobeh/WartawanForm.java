package com.example.sikobeh;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WartawanForm extends AppCompatActivity {

    ExtendedFloatingActionButton fLaporan;
    Calendar calendar;
    Toolbar toolBar;
    RecyclerView recyclerView;
    FirebaseAuth fAuth;
    LinearLayoutManager mLayoutManager;
    StorageReference storageReference;
    FirebaseUser user;
    DataBeritaAdapter dataBeritaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wartawan_form);
        calendar = Calendar.getInstance();
        fLaporan = findViewById(R.id.bukaFLaporan);
        toolBar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.listBerita);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = fAuth.getCurrentUser();
        mLayoutManager = new LinearLayoutManager(WartawanForm.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        setSupportActionBar(toolBar);

        // TAMBAHAN DIGIT UNTUK HAPUS DATA OTOMATIS

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        String datetime = simpleDateFormat.format(calendar.getTime());
        String getMonth = datetime.substring(3, 5);
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("DataBerita").orderByChild("month").equalTo(getPreviousMonth(getMonth));

        Toast.makeText(WartawanForm.this, "Memeriksa data lama..", Toast.LENGTH_LONG).show();
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Boolean[] bisakagakneh = {false, true};
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String urlGambar = dataSnapshot.child("beritaurl").getValue().toString();
                            StorageReference ref2 = FirebaseStorage.getInstance().getReferenceFromUrl(urlGambar);
                            ref2.delete();

                            if (!bisakagakneh[0]){
                                bisakagakneh[0] = true;
                            }
                        }
                    });
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bisakagakneh[0]) {
                    Toast.makeText(WartawanForm.this, "Berhasil membersihkan data lama", Toast.LENGTH_LONG).show();
                } else {
                    if (bisakagakneh[1]) {
                        Toast.makeText(WartawanForm.this, "Data lama tidak ada", Toast.LENGTH_LONG).show();
                        bisakagakneh[1] = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WartawanForm.this, "Gagal membersihkan data lama", Toast.LENGTH_LONG).show();
            }
        });

        FirebaseRecyclerOptions<Berita> data = new FirebaseRecyclerOptions.Builder<Berita>()
                .setQuery((Query) FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("DataBerita"), Berita.class)
                .build();

        dataBeritaAdapter = new DataBeritaAdapter(data);
        recyclerView.setAdapter(dataBeritaAdapter);
        recyclerView.setLayoutManager(mLayoutManager);


        fLaporan.setOnClickListener((view) ->{
            startActivity(new Intent(getApplicationContext(), WartawanInputB.class));
        });
    }

    String getPreviousMonth(String getMonth){
        Integer i = null;
        String result;
        if (getMonth.substring(0, 1).equals("0")) {
            getMonth = getMonth.substring(1, 2);
            i = Integer.parseInt(getMonth);
            i -= 1;
            result = i.toString();
            result = "0" + result;
            //Toast.makeText(WartawanForm.this, "if = " + result, Toast.LENGTH_LONG).show();
        } else {
            i = Integer.parseInt(getMonth);
            i -= 1;
            result = i.toString();
            //Toast.makeText(WartawanForm.this, "else = " + result, Toast.LENGTH_LONG).show();
        }
        return result;
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
                logout.setTitle("Logout Akun");
                logout.setMessage("Apakah Anda yakin ingin logout?");
                logout.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();//logout
                        Intent intent = new Intent(WartawanForm.this, LoginWartawan.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
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