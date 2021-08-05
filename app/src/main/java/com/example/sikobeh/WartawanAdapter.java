package com.example.sikobeh;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class WartawanAdapter extends RecyclerView.Adapter<WartawanAdapter.MyViewHolder>  {

    Context context;
    ArrayList<User> list;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    String decryptedPass;

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
        holder.deleteRl.setVisibility(View.GONE);
        holder.fullname.setText(user.getFullname());
        holder.email.setText(user.getEmail());
        holder.pnumber.setText(user.getPnumber());
        holder.value = user.getUid();
        holder.value2 = holder.fullname.getText().toString().trim();
        holder.vEmail = user.getEmail();
        holder.vUrl = user.getImageurl();
        try {
            decryptedPass = RegisterUser.decrypt(user.getPassword(), "rasul19");
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.vPass = decryptedPass;

        /*
        if (holder.vUrl != null) {
            holder.profileRef = storageReference.child("users/" + holder.value + "/profile.jpg");
            holder.profileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Picasso.get().load(user.getImageurl()).into(holder.bimage);
                    } else {
                        Picasso.get().load(R.drawable.ic_baseline_account_circle_24_white).into(holder.bimage);
                    }
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    holder.bimage.setVisibility(View.VISIBLE);
                }
            });
        } else {
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.bimage.setVisibility(View.VISIBLE);
        }

        holder.profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
         */

        Glide.with(holder.bimage.getContext()).load(holder.vUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.bimage.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.GONE);
                        //Toasty.info(context, "LOL LOL", Toast.LENGTH_SHORT, true).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.bimage.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.ic_baseline_account_circle_24_white)
                .into(holder.bimage);

        holder.cardView.setOnClickListener(v -> {
            lihatBerita(holder.value, holder.value2);
        });
        holder.bimage.setOnClickListener(v -> {
            lihatBerita(holder.value, holder.value2);
        });
        holder.arrowBtn.setOnClickListener(v -> {
            if (holder.deleteRl.getVisibility() == View.VISIBLE){
                holder.deleteRl.setVisibility(View.GONE);
            } else {
                holder.deleteRl.setVisibility(View.VISIBLE);
            }
        });
        holder.deleteBtn.setOnClickListener(v -> {
            deleteDialog(holder.value, holder.vEmail, holder.vPass);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        StorageReference profileRef;
        TextView fullname, email, pnumber;
        CircleImageView bimage;
        RelativeLayout cardView, deleteRl;
        ProgressBar progressBar;
        Button arrowBtn, deleteBtn;
        String value, value2, vEmail, vPass, vUrl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileRef = null;
            fullname = itemView.findViewById(R.id.fullname);
            email =  itemView.findViewById(R.id.email);
            pnumber =  itemView.findViewById(R.id.pnumber);
            bimage = itemView.findViewById(R.id.beritaImage);
            cardView = itemView.findViewById(R.id.areaklik);
            progressBar = itemView.findViewById(R.id.progressBar1);
            arrowBtn = itemView.findViewById(R.id.arrow_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            deleteRl = itemView.findViewById(R.id.delete_rl);
            value = null; value2 = null; vEmail = null; vPass = null; vUrl = null;
        }
    }

    private void lihatBerita(String value, String value2){
        if (!clickBerita){
            clickBerita = true;
            Intent intent = new Intent(context, CekLaporan2.class);
            intent.putExtra("key2", value2);
            intent.putExtra("key", value);
            context.startActivity(intent);
        }
    }

    private void deleteDialog(String value, String email, String pass){
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitle("Hapus Wartawan!");
        dialog.setContentText("Apakah Anda yakin ingin menghapus wartawan ini?");
        dialog.setConfirmText("Lanjut!")
            .setCancelText("Batal")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    deleteStorageUser(value, email, pass, dialog);
                }
            }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dialog.cancel();
                }
            }).show();
    }

    private void deleteStorageUser(String value, String email, String pass, Dialog dialog){
        // -> Users
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+value+"/profile.jpg");
        profileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    DatabaseReference delUrl = FirebaseDatabase.getInstance().getReference().child("Users").child(value).child("imageurl");
                    delUrl.getRef().get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            profileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    delUrl.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            deleteStorageBerita(value, email, pass, dialog);
                                            //Toasty.info(context, "YYYY | deleteStorageUser()", Toast.LENGTH_SHORT, true).show();
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            deleteStorageBerita(value, email, pass, dialog);
                            //Toasty.info(context, "ZZZ | deleteStorageUser()", Toast.LENGTH_SHORT, true).show();
                        }
                    });
                } else {
                    deleteStorageBerita(value, email, pass, dialog);
                    //Toasty.info(context, "0 | deleteStorageUser()", Toast.LENGTH_SHORT, true).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteStorageBerita(value, email, pass, dialog);
                //Toasty.info(context, "3 | deleteStorageUser()", Toast.LENGTH_SHORT, true).show();
            }
        });
        //Toasty.info(context, "SUCCESS | deleteStorageUser()", Toast.LENGTH_SHORT, true).show();
    }

    private void deleteStorageBerita(String value, String email, String pass, Dialog dialog){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("DataBerita").child(value).orderByChild("beritaurl");
        applesQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            String urlGambar = dataSnapshot.child("beritaurl").getValue().toString();
                                            StorageReference ref2 = FirebaseStorage.getInstance().getReferenceFromUrl(urlGambar);
                                            ref2.delete();
                                        } else {
                                            Toasty.error(context, task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //Toasty.info(context, "onCancelled | deleteBerita()", Toast.LENGTH_SHORT, true).show();
                        }
                    });
                    deleteAuth(value, email, pass, dialog);
                    //Toasty.info(context, "1 | deleteStorageBerita()", Toast.LENGTH_SHORT, true).show();
                } else {
                    deleteAuth(value, email, pass, dialog);
                    //Toasty.info(context, "0 | deleteStorageBerita()", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        //Toasty.info(context, "SUCCESS | deleteStorageBerita()", Toast.LENGTH_SHORT, true).show();
    }

    private void deleteAuth(String value, String email, String pass, Dialog dialog) {
       // FirebaseAuth.getInstance().signOut(); // Hapus jika poin 3 kendala terpenuhi
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                           // FirebaseAuth.getInstance().signOut(); // Hapus jika poin 3 kendala terpenuhi
                            notifDeleteBerhasil();
                            Toasty.info(context, "Memuat kembali data..", Toast.LENGTH_SHORT, true).show();
                            deleteRtdb(value, dialog);
                        }
                    });
                    //Toasty.info(context, "1 | deleteAuth()", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(context, task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                    //Toasty.info(context, "0 | deleteAuth()", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        //Toasty.info(context, "SUCCESS | deleteAuth()", Toast.LENGTH_SHORT, true).show();
    }

    private void deleteRtdb(String value, Dialog dialog){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(value);
        db.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Toasty.info(context, "1 | deleteRtdb()", Toast.LENGTH_SHORT, true).show();
                } else {
                    //Toasty.info(context, "0 | deleteRtdb()", Toast.LENGTH_SHORT, true).show();
                }
                dialog.cancel();
            }
        });
        notifDeleteBerhasil();
        //Toasty.info(context, "SUCCESS | deleteRtdb()", Toast.LENGTH_SHORT, true).show();
    }

    private void notifDeleteBerhasil(){
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        dialog.setTitle("Terhapus!");
        dialog.setContentText("Wartawan Berhasil Terhapus!")
                .setConfirmText("Oke")
                .setConfirmClickListener(null);
    }
}
