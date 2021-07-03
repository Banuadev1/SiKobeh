package com.example.sikobeh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class DataBeritaAdapter extends FirebaseRecyclerAdapter<Berita, DataBeritaAdapter.MyViewHolder> {


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
    protected void onBindViewHolder(@NonNull DataBeritaAdapter.MyViewHolder holder, int position, @NonNull Berita model) {
        holder.judul.setText(model.getJudul());
        holder.deskripsi.setText(model.getDesc());
        holder.lokasi.setText(model.getLoc());

        Glide.with(holder.fotoBerita.getContext()).load(model.getBeritaurl())
                .placeholder(R.drawable.ic_baseline_add_photo_alternate_24)
                .error(R.drawable.ic_baseline_account_circle_24).into(holder.fotoBerita);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_berita_wartawan, parent, false);
        return new MyViewHolder(v);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView judul, deskripsi, lokasi;
        ImageView fotoBerita;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.judultext);
            deskripsi = itemView.findViewById(R.id.descberita);
            lokasi = itemView.findViewById(R.id.locberita);
            fotoBerita = itemView.findViewById(R.id.fotoberita);
        }
    }

}
