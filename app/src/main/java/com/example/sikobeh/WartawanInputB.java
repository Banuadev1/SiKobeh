package com.example.sikobeh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class WartawanInputB extends AppCompatActivity {
    public static final int GALLERY_REQUEST_CODE = 105;
    Uri imageUri;
    Calendar calendar;
    String imageFilename;
    EditText judul, desc, loc;
    Button submit, back, gallery;
    ImageView pBerita;
    StorageReference storageReference;
    FirebaseAuth auth;
    DatabaseReference reference;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wartawan_input_berita);

        calendar = Calendar.getInstance();
        judul = findViewById(R.id.add_judul);
        desc = findViewById(R.id.add_desc);
        loc = findViewById(R.id.add_loc);
        pBerita = findViewById(R.id.inputGambar);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("DataBerita").child("FotoBerita");

        gallery = findViewById(R.id.open_gallery);
        back = findViewById(R.id.add_back);
        back.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        submit = findViewById( R.id.add_submit);
        submit.setOnClickListener((view)-> {
            processinsert(imageFilename, imageUri);
        });


        gallery.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);
        });
    }

    public static String getTimeDate(long timestamp){
        try{
            Date timedate = new Date(timestamp);
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(timedate);
        }catch (Exception e){
            return "date";
        }
    }

    private void processinsert(String Name, Uri photoUri){
        String judulB = judul.getText().toString();
        String descB = desc.getText().toString();
        String locB = loc.getText().toString();

        if(judulB.isEmpty()){
            judul.setError("Judul Berita Tidak Boleh Kosong!!");
            judul.requestFocus();
            return;
        }
        if(descB.isEmpty()){
            desc.setError("Deskripsi Berita Tidak Boleh Kosong!!");
            desc.requestFocus();
            return;
        }
        if(locB.isEmpty()){
            loc.setError("Lokasi Berita Tidak Boleh Kosong!!");
            loc.requestFocus();
            return;
        }
        if(imageUri == null){
            Toast.makeText(this, "Gambar Untuk Data Berita Belum Dimasukkan!!", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Sedang Mengupload Data...");
        pd.show();
        final DatabaseReference reference1 = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("DataBerita").push();
        final StorageReference Reference = storageReference
                .child("users/"+auth.getCurrentUser().getUid()).child("photoBerita/"+Name);
        Reference.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadURL = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            String getURL = task.getResult().toString();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
                            String datetime = simpleDateFormat.format(calendar.getTime());
                            String keyB = reference1.getKey();
                            Map<String, Object> map = new HashMap<>();
                            map.put("judul", judulB);
                            map.put("desc", descB);
                            map.put("loc", locB);
                            map.put("beritaurl", getURL);
                            map.put("timeupload", datetime);
                            map.put("month", datetime.substring(3, 5));
                            map.put("key", keyB);
                            reference1.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    judul.setText("");
                                    desc.setText("");
                                    loc.setText("");
                                    pBerita.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
                                    Toast.makeText(WartawanInputB.this, "Upload Berita Berhasil", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            });
                        }
                        else{
                            Toast.makeText(WartawanInputB.this, "Gagal Upload Data Berita!", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WartawanInputB.this, "Terdapat Kesalahan!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Mohon Tunggu.." + (int) progressPercent + "%");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                imageUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFilename = "JPEG_" + timeStamp +"."+getFileExt(imageUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " +  imageFilename);
                pBerita.setImageURI(imageUri);

                Picasso.get().load(imageUri).into(pBerita);
            }

        }
    }

    /*/private void uploadPhotoBerita(String Name, Uri photoUri){
        final StorageReference Reference = storageReference
                .child("users/"+auth.getCurrentUser().getUid()).child("photoBerita/"+Name);
        Reference.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadURL = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Map<String, Object> map = new HashMap<>();
                        String getURL = task.getResult().toString();
                        map.put("beritaurl", getURL);
                        String beritaurl = reference.push().getKey();
                        reference.child(beritaurl).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                pBerita = null;
                                Toast.makeText(WartawanInputB.this, "Upload Berhasil!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(WartawanInputB.this, "Terdapat Kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }*/

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

   /* public void setNotif(){
        KoordinatorForm koordinatorForm = new KoordinatorForm();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(WartawanInputB.this, "NotifBeritaMasuk");
        builder.setContentTitle("Laporan Berita Terbaru Sudah Masuk");
        builder.setContentText("Hello Koordinator, Silahkan Cek Laporan Berita Harian Yang Baru - Baru Masuk");
        builder.setSmallIcon(R.drawable.profil_karyawan);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(koordinatorForm.getApplicationContext());
        managerCompat.notify(1, builder.build());
    }*/

}
