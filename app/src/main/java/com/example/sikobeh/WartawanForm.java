package com.example.sikobeh;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WartawanForm extends AppCompatActivity {

    ExtendedFloatingActionButton fLaporan;
    Calendar calendar;
    Toolbar toolBar;
    public static TextView jikaKosong;
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

        jikaKosong = findViewById(R.id.idKosongW);
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
        autoDelete();

        FirebaseRecyclerOptions<Berita> data = new FirebaseRecyclerOptions.Builder<Berita>()
                .setQuery((Query) FirebaseDatabase.getInstance().getReference("DataBerita").child(FirebaseAuth.getInstance().getCurrentUser().getUid()), Berita.class)
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
                SweetAlertDialog alertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                alertDialog.setTitle("Logout Akun!");
                alertDialog.setContentText("Apakah Yakin Ingin Logout?")
                        .setConfirmText("Lanjut")
                        .setCancelText("Batal")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                FirebaseAuth.getInstance().signOut();//logout
                                Intent intent = new Intent(WartawanForm.this, LoginWartawan.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        alertDialog.cancel();
                    }
                }).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataBeritaAdapter.startListening();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            startActivity(new Intent(WartawanForm.this, LoginWartawan.class));
            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        dataBeritaAdapter.stopListening();
    }

    public void delayRefresh(){
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void autoDelete(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        String datetime = simpleDateFormat.format(calendar.getTime());
        String getMonth = datetime.substring(3, 5);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DataBerita");
        DatabaseReference refData = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Query applesQuery = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("month").equalTo(getPreviousMonth(getMonth));

        Toast.makeText(WartawanForm.this, "Memeriksa berita..", Toast.LENGTH_LONG).show();
        delayRefresh();
        refData.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0){
                    jikaKosong.setVisibility(View.INVISIBLE);
                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount() < 1){
                                Toast.makeText(WartawanForm.this, "Berita lama tidak ada", Toast.LENGTH_LONG).show();
                            } else {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            String urlGambar = dataSnapshot.child("beritaurl").getValue().toString();
                                            StorageReference ref2 = FirebaseStorage.getInstance().getReferenceFromUrl(urlGambar);
                                            ref2.delete();
                                            //
                                            applesQuery.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.getChildrenCount() < 1) {
                                                        jikaKosong.setVisibility(View.VISIBLE);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            Toast.makeText(WartawanForm.this, "Membersihkan berita lama", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(WartawanForm.this, "Gagal membersihkan data lama", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    jikaKosong.setVisibility(View.VISIBLE);
                    Toast.makeText(WartawanForm.this, "Berita kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                jikaKosong.setVisibility(View.VISIBLE);
                Toast.makeText(WartawanForm.this, "Berita kosong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}