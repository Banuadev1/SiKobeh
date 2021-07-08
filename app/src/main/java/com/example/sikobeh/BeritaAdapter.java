package com.example.sikobeh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        holder.tanggal.setText(berita.getBeritaurl());
        Glide.with(holder.fotoBerita.getContext()).load(berita.getBeritaurl())
                .placeholder(R.drawable.ic_baseline_add_photo_alternate_24)
                    .error(R.drawable.ic_baseline_account_circle_24).into(holder.fotoBerita);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView judul, loc, desc, tanggal;
        ImageView fotoBerita;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.judultext);
            loc = itemView.findViewById(R.id.locberita);
            desc = itemView.findViewById(R.id.descberita);
            tanggal = itemView.findViewById(R.id.tanggal);
            fotoBerita = itemView.findViewById(R.id.fotoberita);
        }
    }
}
