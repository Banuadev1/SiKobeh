package com.example.sikobeh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.MyViewHolder> {
    // Semoga teada Bug yg meresahkan, Amin
    Context context;
    ArrayList<Berita> list;
    Bitmap bitmap;
    private SharedPreferences phoneNumber;
    private File filePath = null, folder = null, gambar = null;
    String catchError;;

    public BeritaAdapter(Context context, ArrayList<Berita> list) {
        this.context = context;
        this.list = list;
        phoneNumber = context.getSharedPreferences("phoneNumber", MODE_PRIVATE);
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
        holder.pilihanB.setOnClickListener(v -> {
            bitmap = ((BitmapDrawable)holder.fotoBerita.getDrawable()).getBitmap();
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PackageManager.PERMISSION_GRANTED);
            final View customLayout2 = holder.li.inflate(R.layout.pilihan_lanjutan_berita, null);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setView(customLayout2);
            builder2.setTitle("Pilihan Lanjutan");

            Button bagikanWAB = customLayout2.findViewById(R.id.bagikanWA);
            Button buatFileTextB = customLayout2.findViewById(R.id.buatFileText);
            String[] data = {berita.getJudul(), berita.getLoc(), berita.getDesc(), berita.getTimeupload()};

            folder = new File(Environment.getExternalStorageDirectory() + File.separator +
                    "Sikobeh");
            if (!folder.exists()){
                folder.mkdirs();
            }

            bagikanWAB.setOnClickListener(v1 -> {
                bagikanKeWA(holder.li, data);
            });
            buatFileTextB.setOnClickListener(v1 -> {
                folder = new File(Environment.getExternalStorageDirectory() + File.separator +
                        "Sikobeh" + File.separator + "Text File");
                if (!folder.exists()){
                    if (folder.mkdirs()){
                        simpanFileText(data, holder.li);
                    } else {
                        Toasty.error(context, "Terjadi kesalahan saat membuat folder Sikobeh", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    simpanFileText(data, holder.li);
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
        Button ubah;
        ImageButton pilihanB;
        ProgressBar progressBar;
        String vUrl;
        LayoutInflater li;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            li = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            judul = itemView.findViewById(R.id.judultext);
            ubah = itemView.findViewById(R.id.ubahBerita);
            pilihanB = itemView.findViewById(R.id.pilihan);
            ubah.setVisibility(View.GONE);
            loc = itemView.findViewById(R.id.locberita);
            desc = itemView.findViewById(R.id.descberita);
            tanggal = itemView.findViewById(R.id.tanggal);
            fotoBerita = itemView.findViewById(R.id.fotoberita);
            progressBar = itemView.findViewById(R.id.progressBar1);
            vUrl = null;
        }
    }

    private boolean cekWAInstalled(){
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    private String convertToCodeNumber(String pn){
        String str = pn;
        if (pn.startsWith("0")){
            str = "62" + pn.substring(1, str.length());
        }
        return str;
    }

    private String convertToTextWa(String[] data){
        String s = "*Aplikasi SiKobeh*" + "%0a%0a" +
                "Judul :%0a" + data[0] + "%0a%0a" +
                "Lokasi :%0a" + data[1] + "%0a%0a" +
                "Deskripsi :%0a" + data[2] + "%0a%0a" +
                "Waktu Mengunggah :%0a" + data[3];
        return s;
    }

    private void bagikanKeWA(LayoutInflater li, String[] dataBerita){
        ActivityCompat.requestPermissions((Activity) context, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PackageManager.PERMISSION_GRANTED);
        final View customLayout2 = li.inflate(R.layout.edit_nomor_wa, null);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
        builder2.setView(customLayout2);
        builder2.setTitle("Masukkan nomor telepon Anda");

        EditText edNP = customLayout2.findViewById(R.id.edNomorTelp);
        String phone = phoneNumber.getString("koor", "false");
        if (!phone.equals("false")){
            edNP.setText(phone);
        }
        builder2.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String data = edNP.getText().toString();
                phoneNumber.edit().putString("koor", data).apply();
                if (!data.isEmpty()){
                    if (cekWAInstalled()){
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "https://api.whatsapp.com/send?phone="+convertToCodeNumber(data)+
                                        "&text="+convertToTextWa(dataBerita)));
                        context.startActivity(i);
                        edNP.getText().clear();
                    } else {
                        Toasty.info(context, "Whatsapp tidak terinstal", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    Toasty.error(context, "Masukkan nomor telepon terlebih dahulu", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        builder2.setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog1 = builder2.create();
        dialog1.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        dialog1.show();
    }

    private boolean prepareFolder(String str){
        File env = Environment.getExternalStorageDirectory();
        // can switch with this : getExternalFilesDir(null) -> its gonna save to com.example.sikobeh
        filePath = new File(env + File.separator + "Sikobeh" + File.separator + "Text File" +
                File.separator + str, str+".txt");
        gambar = new File(env + File.separator + "Sikobeh" + File.separator + "Text File" +
                File.separator + str, str+".JPEG");
        try {
            if (!gambar.exists()){
                gambar.createNewFile();
            }
            if (!filePath.exists()){
                filePath.createNewFile();
            }
            return true;
        } catch (IOException e){
            e.printStackTrace();
            catchError = e.toString();
            return false;
        }
    }

    private void simpanFileText(String[] data, LayoutInflater li){
        ActivityCompat.requestPermissions((Activity) context, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PackageManager.PERMISSION_GRANTED);
        final View customLayout2 = li.inflate(R.layout.edit_nama_file, null);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
        builder2.setView(customLayout2);
        builder2.setTitle("Nama File Teks (*.txt)");

        EditText edNamaFile = customLayout2.findViewById(R.id.edNamaDokumen);
        edNamaFile.setText("Berita-"+data[0]);
        builder2.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String edT = edNamaFile.getText().toString();
                if (makeDirTextBerita(edT)){
                    if (prepareFolder(edT)){
                        writeFile(data);
                    } else {
                        //Toasty.error(context, catchError, Toast.LENGTH_LONG).show();
                        Toasty.error(context, "Terjadi kesalahan saat penentuan penyimpanan", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    Toasty.error(context, "Terjadi kesalahan saat membuat penyimpanan File Text", Toast.LENGTH_SHORT, true).show();
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
    }

    private String convertToFileText(String[] data){
        String s = "*Aplikasi SiKobeh*" + "\n\n" +
                "Judul :\n" + data[0] + "\n\n" +
                "Lokasi :\n" + data[1] + "\n\n" +
                "Deskripsi :\n" + data[2] + "\n\n" +
                "Waktu Mengunggah :\n" + data[3];
        return s;
    }

    private void writeFile(String[] data){
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(convertToFileText(data).getBytes());
            fos.close();
            simpanFoto();
        } catch (IOException e) {
            Toasty.error(context, e.toString(), Toast.LENGTH_SHORT, true).show();
        }
    }

    private void simpanFoto(){
        try {
            OutputStream fo = new FileOutputStream(gambar);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);
            fo.flush();
            fo.close();
            Toasty.error(context, "File tersimpan di "+folder.toString(), Toast.LENGTH_SHORT, true).show();
        } catch (IOException ex) {
            Toasty.error(context, ex.getMessage(), Toast.LENGTH_LONG, true).show();
        }
    }

    private Boolean makeDirTextBerita(String str){
        folder = new File(Environment.getExternalStorageDirectory() + File.separator +
                "Sikobeh" + File.separator + "Text File" + File.separator + str);
        if (!folder.exists()){
            if (folder.mkdirs()){
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
}
