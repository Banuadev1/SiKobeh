package com.example.sikobeh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DataBeritaAdapter extends FirebaseRecyclerAdapter<Berita, DataBeritaAdapter.MyViewHolder> {
    public static final int GALLERY_REQUEST_CODE = 105;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DataBeritaAdapter(@NonNull FirebaseRecyclerOptions<Berita> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Berita model) {
        String keyberita = model.getKey();
        holder.judul.setText(model.getJudul());
        holder.deskripsi.setText(model.getDesc());
        holder.lokasi.setText(model.getLoc());
        holder.tanggal.setText(" " + model.getTimeupload() + " ");
        holder.ubahB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.ubahB.getContext(), UbahDataBerita.class);
                intent.putExtra("key", keyberita);
                holder.ubahB.getContext().startActivity(intent);
            }
        });

        Glide.with(holder.fotoBerita.getContext()).load(model.getBeritaurl())
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.fotoBerita.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            })
            .error(R.drawable.ic_baseline_image_24_berita).into(holder.fotoBerita);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_berita_wartawan, parent, false);
        return new MyViewHolder(v);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView judul, deskripsi, lokasi, tanggal;
        Button ubahB;
        ImageView fotoBerita;
        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.judultext);
            deskripsi = itemView.findViewById(R.id.descberita);
            ubahB = itemView.findViewById(R.id.ubahBerita);
            lokasi = itemView.findViewById(R.id.locberita);
            fotoBerita = itemView.findViewById(R.id.fotoberita);
            tanggal = itemView.findViewById(R.id.tanggal);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);

        }
    }
}
