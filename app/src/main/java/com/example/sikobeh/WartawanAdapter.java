package com.example.sikobeh;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class WartawanAdapter extends RecyclerView.Adapter<WartawanAdapter.MyViewHolder>  {

    Context context;
    ArrayList<User> list;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    public static Boolean perintahDelete = false;
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

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        /*
        Glide.with(holder.bimage.getContext()).load(user.getImageurl()).
            listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    holder.bimage.setVisibility(View.VISIBLE);
                    return false;
                }
            }).error(R.drawable.ic_baseline_account_circle_24_white).into(holder.bimage);
        */

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
        /*
        holder.fullname.setOnClickListener(v -> {
            lihatBerita(value, value2);
        });
        holder.pnumber.setOnClickListener(v -> {
            lihatBerita(value, value2);
        });
        holder.email.setOnClickListener(v -> {
            lihatBerita(value, value2);
        });

         */
        holder.arrowBtn.setOnClickListener(v -> {
            if (holder.deleteRl.getVisibility() == View.VISIBLE){
                holder.deleteRl.setVisibility(View.GONE);
            } else {
                holder.deleteRl.setVisibility(View.VISIBLE);
            }
        });
        holder.deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder logout = new AlertDialog.Builder(context);
            logout.setTitle("Logout Akun");
            logout.setMessage("Apakah Anda yakin ingin menghapus data wartawan tersebut?");
            logout.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteWartawan(value);
                }
            }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            logout.create().show();
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

            fullname = (TextView) itemView.findViewById(R.id.fullname);
            email = (TextView) itemView.findViewById(R.id.email);
            pnumber = (TextView) itemView.findViewById(R.id.pnumber);
            bimage = (CircleImageView) itemView.findViewById(R.id.beritaImage);
            cardView = (RelativeLayout) itemView.findViewById(R.id.areaklik);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
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

    private void deleteWartawan(String value){
        if (perintahDelete == false){
            perintahDelete = true;
            Intent intent = new Intent(context, CekLaporan.class);
            intent.putExtra("deleteKey", value);
            context.startActivity(intent);
        }
    }

}
