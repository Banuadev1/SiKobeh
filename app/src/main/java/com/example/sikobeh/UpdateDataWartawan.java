package com.example.sikobeh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdateDataWartawan extends AppCompatActivity {

    EditText updateFName, updateEmail, updatePhone;
    Uri profilURI;
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
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullName= dataSnapshot.child("fullname").getValue().toString();
                String mail= dataSnapshot.child("email").getValue().toString();
                String phone= dataSnapshot.child("pnumber").getValue().toString();

                updatePhone.setText(phone);
                updateEmail.setText(mail);
                updateFName.setText(fullName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        StorageReference profileRef = storageReference.child("users/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(updatePProfil));

        updatePProfil.setOnClickListener(v -> {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGalleryIntent,1000);
        });

        simpan.setOnClickListener(v -> {
            uploadDataToFirebase(profilURI);
            /*String newEmail = updateEmail.getText().toString();
            Map<String, Object> map = new HashMap<>();
            map.put("fullname", updateFName.getText().toString());
            map.put("email", newEmail);
            map.put("pnumber", updatePhone.getText().toString());
            map.put("imageurl", getUrl);
            user.updateEmail(newEmail);
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(reference.getKey()).updateChildren(map)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(UpdateDataWartawan.this, "Data Berhasil Di Ubah", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(UpdateDataWartawan.this, "Terdapat Kesalahan!!", Toast.LENGTH_SHORT).show());*/
        });

        kembali.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CheckProfilWartawan.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                profilURI = data.getData();

                //profileImage.setImageURI(imageUri);

                updatePProfil.setImageURI(profilURI);

                Picasso.get().load(profilURI).into(updatePProfil);
            }
        }
    }

    private void uploadDataToFirebase(Uri Photouri){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Sedang Mengganti Foto Profil..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final StorageReference fileRef = storageReference.child("users/"+auth.getCurrentUser()
                .getUid()+"/profile.jpg");
        fileRef.putFile(Photouri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> downloadURL = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    String getUrl = task.getResult().toString();
                    String newEmail = updateEmail.getText().toString();
                    Map<String, Object> map = new HashMap<>();
                    map.put("fullname", updateFName.getText().toString());
                    map.put("email", newEmail);
                    map.put("pnumber", updatePhone.getText().toString());
                    map.put("imageurl", getUrl);
                    user.updateEmail(newEmail);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(reference1.getKey()).updateChildren(map)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(UpdateDataWartawan.this, "Data Berhasil Di Ubah", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(UpdateDataWartawan.this, "Terdapat Kesalahan!!", Toast.LENGTH_SHORT).show());
                }
            });
        }).addOnProgressListener(snapshot -> {
            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            pd.setMessage("Mohon Tunggu.." + (int) progressPercent + "%");
        })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdateDataWartawan.this, "Terdapat Kesalahan!!", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            startActivity(new Intent(UpdateDataWartawan.this, LoginWartawan.class));
            finish();
        }
    }
}