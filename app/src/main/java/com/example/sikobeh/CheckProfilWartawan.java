package com.example.sikobeh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class CheckProfilWartawan extends AppCompatActivity {

    TextView fullName,email,phone;
    FirebaseAuth fAuth;
    String userId;
    Button resetPassLocal,changeProfileImage, logOut;
    FirebaseUser user;
    DatabaseReference reff;
    ImageView profileImage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_profil_wartawan);

        phone = findViewById(R.id.profilePhone);
        fullName = findViewById(R.id.profileName);
        email    = findViewById(R.id.profileEmail);
        resetPassLocal = findViewById(R.id.resetPasswordLocal);

        profileImage = findViewById(R.id.profileImage);
        changeProfileImage = findViewById(R.id.changeProfile);
        logOut = findViewById(R.id.buttonLogOut);


        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImage));

        reff = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Fname= snapshot.child("fullname").getValue().toString();
                String Email= snapshot.child("email").getValue().toString();
                String Age= snapshot.child("pnumber").getValue().toString();

                fullName.setText(Fname);
                email.setText(Email);
                phone.setText(Age);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logOut.setOnClickListener(v -> {

        });

        changeProfileImage.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), UpdateDataWartawan.class));
            finish();
        });

        resetPassLocal.setOnClickListener(v -> {
            final EditText resetPassword = new EditText(v.getContext());

            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Ubah Password?");
            passwordResetDialog.setMessage("Masukkan Password Baru > Minimal 6 Karakter!!");
            passwordResetDialog.setView(resetPassword);

            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
                // extract the email and send reset link
                String newPassword = resetPassword.getText().toString();
                user.updatePassword(newPassword).addOnSuccessListener(aVoid -> Toast.makeText(CheckProfilWartawan.this, "Password Berhasil Di Ubah", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(CheckProfilWartawan.this, "Gagal Merubah Password", Toast.LENGTH_SHORT).show());
            });

            passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
                // close
            });
            passwordResetDialog.create().show();
        });

        logOut.setOnClickListener(v -> {
            AlertDialog.Builder logoutAkun = new AlertDialog.Builder(v.getContext());
            logoutAkun.setTitle("LogOut Akun");
            logoutAkun.setMessage("Apakah Anda Yakin Ingin Logout?");
            logoutAkun.setPositiveButton("Yes", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(), LoginWartawan.class));
                finish();
            });
            logoutAkun.setNegativeButton("No", (dialog, which) -> {

            });
            logoutAkun.create().show();
        });

    }

}