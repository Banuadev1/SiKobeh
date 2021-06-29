package com.example.sikobeh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UpdateDataWartawan extends AppCompatActivity {

    EditText updateFName, updateEmail, updatePhone;
    ImageView updatePProfil;
    Button simpan, kembali;
    FirebaseAuth auth;
    String UserId;
    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data_wartawan);

        updateFName = findViewById(R.id.profileFullName);
        updateEmail = findViewById(R.id.profileEmailAddress);
        updatePhone = findViewById(R.id.profilePhoneNo);
        updatePProfil = findViewById(R.id.profileImageView);

        simpan = findViewById(R.id.saveProfileInfo);
        kembali = findViewById(R.id.backProfileInfo);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        UserId = auth.getCurrentUser().getUid();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("DataWartawan");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullName= dataSnapshot.child("fullname").getValue().toString();
                String mail= dataSnapshot.child("email").getValue().toString();
                String phone= dataSnapshot.child("age").getValue().toString();

                updatePhone.setText(phone);
                updateEmail.setText(mail);
                updateFName.setText(fullName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("fullname", updateFName.getText().toString());
                map.put("email", updateEmail.getText().toString());
                map.put("age", updatePhone.getText().toString());
                String newEmail = updateEmail.getText().toString();
                user.updateEmail(newEmail);
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(reference.getKey()).updateChildren(map)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(UpdateDataWartawan.this, "Data Berhasil Di Ubah", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(UpdateDataWartawan.this, "Terdapat Kesalahan!!", Toast.LENGTH_SHORT).show());
            }
        });

        kembali.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), CheckProfilWartawan.class)));

    }
}