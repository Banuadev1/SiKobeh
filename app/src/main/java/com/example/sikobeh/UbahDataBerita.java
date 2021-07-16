package com.example.sikobeh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UbahDataBerita extends AppCompatActivity {

    EditText judulberita, deskripsiberita, lokasiberita;
    ImageView gambarberita;
    Button submit, cancel;
    StorageReference storageReference;
    String getUrl;
    String imageFilename;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_data_berita);

        judulberita = findViewById(R.id.judulB);
        deskripsiberita = findViewById(R.id.deskB);
        lokasiberita = findViewById(R.id.locB);
        gambarberita = findViewById(R.id.ubahGambar);
        submit = findViewById(R.id.usubmit);
        cancel = findViewById(R.id.btnback);

        String valueKey = getIntent().getStringExtra("key");

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference("DataBerita")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(valueKey);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String jBerita = snapshot.child("judul").getValue().toString();
                String dBerita = snapshot.child("desc").getValue().toString();
                String lBerita = snapshot.child("loc").getValue().toString();
                String gBerita = snapshot.child("beritaurl").getValue().toString();
                getUrl = snapshot.child("beritaurl").getValue().toString();

                judulberita.setText(jBerita);
                deskripsiberita.setText(dBerita);
                lokasiberita.setText(lBerita);
                Picasso.get().load(gBerita).into(gambarberita);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cancel.setOnClickListener(v -> {
            onBackPressed();
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("judul", judulberita.getText().toString());
                map.put("desc", deskripsiberita.getText().toString());
                map.put("loc", lokasiberita.getText().toString());
                map.put("beritaurl", getUrl);

                reference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UbahDataBerita.this, "Berita Berhasil Di Ubah!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UbahDataBerita.this, "Terdapat Kesalahan!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        gambarberita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFilename = "JPEG_" + timeStamp +"."+getFileExt(imageUri);

                //profileImage.setImageURI(imageUri);

                uploadDataToFirebase(imageFilename, imageUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            startActivity(new Intent(UbahDataBerita.this, LoginWartawan.class));
            finish();
        }
    }

    private void uploadDataToFirebase(String Name, Uri imageUri){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Sedang Mengganti Gambar Berita..");;
        pd.show();
        final StorageReference fileRef = storageReference.child("databerita/"+auth.getCurrentUser()
                .getUid()).child(Name);
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> downloadURL = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                getUrl = task.getResult().toString();
            }).addOnSuccessListener(uri -> {
                Picasso.get().load(imageUri).into(gambarberita);
                pd.dismiss();
            });
        }).addOnProgressListener(snapshot -> {
            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            pd.setMessage("Mohon Tunggu.." + (int) progressPercent + "%");
        })
                .addOnFailureListener(e -> {
                    Toast.makeText(UbahDataBerita.this, "Terdapat Kesalahan!!", Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }
}