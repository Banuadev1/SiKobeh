package com.example.sikobeh;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class WartawanAdapter extends RecyclerView.Adapter<WartawanAdapter.MyViewHolder>  {

    Context context;
    ArrayList<User> list;
    FirebaseAuth fAuth;
    StorageReference storageReference;

    public static Boolean clickBerita = false;

    public WartawanAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);
        holder.fullname.setText(user.getFullname());
        holder.email.setText(user.getEmail());
        holder.pnumber.setText(user.getPnumber());
        String value = user.getUid();
        String email = user.getEmail();
        String pass = user.getPassword();

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+value+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.bimage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        holder.bimage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        holder.bimage.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.bimage.setVisibility(View.VISIBLE);
            }
        });

        String value2 = holder.fullname.getText().toString().trim();

        holder.cardView.setOnClickListener(v -> {
            lihatBerita(value, value2);
        });
        holder.bimage.setOnClickListener(v -> {
            lihatBerita(value, value2);
        });
        holder.arrowBtn.setOnClickListener(v -> {
            if (holder.deleteRl.getVisibility() == View.VISIBLE){
                holder.deleteRl.setVisibility(View.GONE);
            } else {
                holder.deleteRl.setVisibility(View.VISIBLE);
            }
        });
        holder.deleteBtn.setOnClickListener(v -> {
            deleteWartawan(value, email, pass);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fullname, email, pnumber;
        CircleImageView bimage;
        RelativeLayout cardView, deleteRl;
        ProgressBar progressBar;
        Button arrowBtn, deleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.fullname);
            email =  itemView.findViewById(R.id.email);
            pnumber =  itemView.findViewById(R.id.pnumber);
            bimage = itemView.findViewById(R.id.beritaImage);
            cardView = itemView.findViewById(R.id.areaklik);
            progressBar = itemView.findViewById(R.id.progressBar1);
            arrowBtn = itemView.findViewById(R.id.arrow_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            deleteRl = itemView.findViewById(R.id.delete_rl);
        }
    }

    private void lihatBerita(String value, String value2){
        if (clickBerita == false){
            clickBerita = true;
            Intent intent = new Intent(context, CekLaporan2.class);
            intent.putExtra("key2", value2);
            intent.putExtra("key", value);
            context.startActivity(intent);
        }
    }

    private void deleteWartawan(String value, String email, String pass){
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitle("Hapus Wartawan!");
        dialog.setContentText("Apakah Anda yakin ingin menghapus wartawan ini?");
        dialog.setConfirmText("Lanjut!")
                .setCancelText("Batal")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        // Delete Storage
                        // -> Users
                        StorageReference profileRef = storageReference.child("users/"+value+"/profile.jpg");
                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                profileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                            }
                        });
                        // -> DataBerita
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query applesQuery = ref.child("DataBerita").child(value).orderByChild("beritaurl");
                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            String urlGambar = dataSnapshot.child("beritaurl").getValue().toString();
                                            StorageReference ref2 = FirebaseStorage.getInstance().getReferenceFromUrl(urlGambar);
                                            ref2.delete();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        // Delete Authentication
                        FirebaseAuth.getInstance().signOut(); // Hapus jika poin 3 kendala terpenuhi
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(),pass.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                dialog.setTitle("Terhapus!");
                                                dialog.setContentText("Wartawan Berhasil Terhapus!")
                                                       .setConfirmText("Oke")
                                                       .setConfirmClickListener(null)
                                                       .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            } else {
                                                Toasty.error(context, task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                            }
                                        }
                                    });
                                    FirebaseAuth.getInstance().signOut(); // Hapus jika poin 3 kendala terpenuhi
                                }
                                else {
                                    Toasty.error(context, task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                }
                            }
                        });

                        // delete Realtime Database
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(value);
                        db.removeValue();
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toasty.info(context, "Memuat kembali data..", Toast.LENGTH_SHORT, true).show();
                        list.clear();
                        notifyDataSetChanged();
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
             dialog.cancel();
            }
        }).show();

        }

}
