package com.example.sikobeh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class KoordinatorForm extends AppCompatActivity {

    private Button register, laporan, logout;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_koordinator_form);
        register = findViewById(R.id.registerUser);
        laporan = findViewById(R.id.cekLaporan);
        logout = findViewById(R.id.logOutKoordinator);

        reference = FirebaseDatabase.getInstance().getReference("DataBerita");


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Register(); }
        });

        laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Laporan(); }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                getNotification();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void Register(){
        Intent intent = new Intent(this, RegisterUser.class);
        startActivity(intent);
    }

    public void Laporan(){
        Intent intent = new Intent(this, CekLaporan.class);
        startActivity(intent);
    }

    public void Logout(){
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
                Intent intent = new Intent(KoordinatorForm.this, LoginKoordinator.class);
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

    private void getNotification(){
        SharedPreferences sharedPref = getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
        int n = sharedPref.getInt("logged", 0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("NotifBeritaMasuk", "Notif Input Berita", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        if(n > 0 ){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "NotifBeritaMasuk");
        builder.setContentTitle("Ada Laporan Berita Terbaru Yang Sudah Masuk!!");
        builder.setContentText("Hello Koordinator, Silahkan Cek Laporan Berita Harian Yang Baru - Baru Masuk");
        builder.setSmallIcon(R.drawable.profil_karyawan);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1, builder.build());
        }
    }
}