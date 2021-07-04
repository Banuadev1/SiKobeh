package com.example.sikobeh;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class WartawanAdapter extends RecyclerView.Adapter<WartawanAdapter.MyViewHolder>  {

    Context context;
    ArrayList<User> list;
    FirebaseAuth fAuth;
    StorageReference storageReference;

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

        StorageReference profileRef = storageReference.child("users/"+value+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.bimage));

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CekLaporan2.class);
            intent.putExtra("key", value);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fullname, email, pnumber;
        CircleImageView bimage;
        RelativeLayout cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = (TextView) itemView.findViewById(R.id.fullname);
            email = (TextView) itemView.findViewById(R.id.email);
            pnumber = (TextView) itemView.findViewById(R.id.pnumber);
            bimage = (CircleImageView) itemView.findViewById(R.id.beritaImage);
            cardView = (RelativeLayout) itemView.findViewById(R.id.areaklik);

            fullname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

}
