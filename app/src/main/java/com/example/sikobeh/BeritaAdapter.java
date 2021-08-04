package com.example.sikobeh;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.MyViewHolder> {

    Context context;
    ArrayList<Berita> list;

    public BeritaAdapter(Context context, ArrayList<Berita> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_berita_wartawan,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Berita berita = list.get(position);
        holder.judul.setText(berita.getJudul());
        holder.loc.setText(berita.getLoc());
        holder.desc.setText(berita.getDesc());
        holder.tanggal.setText(" " + berita.getTimeupload() + " ");
        holder.vUrl = berita.getBeritaurl();
        Glide.with(holder.fotoBerita.getContext()).load(holder.vUrl)
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
            .into(holder.fotoBerita);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView judul, loc, desc, tanggal;
        ImageView fotoBerita;
        Button ubah;
        ProgressBar progressBar;
        String vUrl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.judultext);
            ubah = itemView.findViewById(R.id.ubahBerita);
            ubah.setVisibility(View.GONE);
            loc = itemView.findViewById(R.id.locberita);
            desc = itemView.findViewById(R.id.descberita);
            tanggal = itemView.findViewById(R.id.tanggal);
            fotoBerita = itemView.findViewById(R.id.fotoberita);
            progressBar = itemView.findViewById(R.id.progressBar1);
            vUrl = null;
        }
    }
}
