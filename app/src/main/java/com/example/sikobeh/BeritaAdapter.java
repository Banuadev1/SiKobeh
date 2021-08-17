package com.example.sikobeh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.MyViewHolder> {

    Context context;
    ArrayList<Berita> list;
    private File filePath = null;

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
        holder.buatDokumen.setOnClickListener(v -> {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PackageManager.PERMISSION_GRANTED);
            final View customLayout2 = holder.li.inflate(R.layout.edit_nama_file, null);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setView(customLayout2);
            builder2.setTitle("Nama File Dokumen (*.docx)");

            EditText edNamaFile = customLayout2.findViewById(R.id.edNamaDokumen);
            edNamaFile.setText("Berita-"+berita.getJudul());
            builder2.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Sikobeh");
                    if (!folder.exists()){
                        folder.mkdirs();
                    }
                    String str = edNamaFile.getText().toString();
                    if (str.isEmpty()){
                        edNamaFile.setError("Nama file harus diisi");
                    } else {
                        if (prepareDocx(str)) {
                            String[] data = {berita.getJudul(), berita.getLoc(), berita.getDesc(), berita.getTimeupload()};
                            createDocx(data /*, holder.fotoBerita */);
                        } else {
                            Toast.makeText(context, "Nama file sudah terpakai!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            builder2.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog1 = builder2.create();
            dialog1.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            dialog1.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView judul, loc, desc, tanggal;
        ImageView fotoBerita;
        Button ubah, buatDokumen;
        ProgressBar progressBar;
        String vUrl;
        LayoutInflater li;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            li = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            judul = itemView.findViewById(R.id.judultext);
            ubah = itemView.findViewById(R.id.ubahBerita);
            buatDokumen = itemView.findViewById(R.id.buatDokumen);
            ubah.setVisibility(View.GONE);
            loc = itemView.findViewById(R.id.locberita);
            desc = itemView.findViewById(R.id.descberita);
            tanggal = itemView.findViewById(R.id.tanggal);
            fotoBerita = itemView.findViewById(R.id.fotoberita);
            progressBar = itemView.findViewById(R.id.progressBar1);
            vUrl = null;
        }
    }

    private Boolean prepareDocx(String str){
        // parameter_1 can switch with this : getExternalFilesDir(null) -> its gonna save to com.example.gudangsederhana
        filePath = new File(Environment.getExternalStorageDirectory()+File.separator+"Sikobeh", str+".docx");

        try {
            if (!filePath.exists()){
                filePath.createNewFile();
                return true;
            } else {
                return false;
            }
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    private void createDocx(String[] data){
        try {
            XWPFDocument xwpfDocument = new XWPFDocument();
            XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();
            xwpfParagraph.setSpacingAfterLines(1);
            XWPFRun xwpfRun = xwpfParagraph.createRun();

            xwpfRun.setFontSize(12);
            xwpfRun.setFontFamily("Times New Roman");

            xwpfRun.setBold(true);
            xwpfRun.setText("Sistem Informasi Konsep Berita Harian (SiKobeh");
            xwpfRun.addBreak(); xwpfRun.addBreak();
            xwpfRun.setBold(false);

            xwpfRun.setText("Dari : " + CekLaporan2.value2);
            xwpfRun.addBreak(); xwpfRun.addBreak();

            xwpfRun.setText("Judul : " + data[0]); xwpfRun.addBreak();
            xwpfRun.setText("Lokasi : " + data[1]); xwpfRun.addBreak(); // xwpfRun.addTab();
            xwpfRun.setText("Deskripsi : " + data[2]); xwpfRun.addBreak();
            xwpfRun.setText("Waktu Penyetoran : " + data[3]); xwpfRun.addBreak(); xwpfRun.addBreak();

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            xwpfDocument.write(fileOutputStream);

            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
                String textT = "Berhasil menyimpan dokumen (*.docx)";
                Toast.makeText(context, textT, Toast.LENGTH_LONG).show();
            }
            xwpfDocument.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
