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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;

public class DataBeritaAdapter extends FirebaseRecyclerAdapter<Berita, DataBeritaAdapter.MyViewHolder> {
    public static final int GALLERY_REQUEST_CODE = 105;
    Context context;
    Bitmap bitmap;
    private SharedPreferences phoneNumber;
    private File filePath = null, folder = null, gambar = null;
    String auth = FirebaseAuth.getInstance().getCurrentUser().getUid(), catchError;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DataBeritaAdapter(Context context, @NonNull FirebaseRecyclerOptions<Berita> options) {
        super(options);
        this.context = context;
        phoneNumber = context.getSharedPreferences("phoneNumber", MODE_PRIVATE);
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
            String[] data = {model.getJudul(), model.getLoc(), model.getDesc(), model.getTimeupload()};

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
                        Toast.makeText(context, "Terjadi kesalahan saat membuat folder Sikobeh", Toast.LENGTH_SHORT).show();
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_berita_wartawan, parent, false);
        return new MyViewHolder(v);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView judul, deskripsi, lokasi, tanggal;
        Button ubahB;
        ImageButton pilihanB;
        ImageView fotoBerita;
        ProgressBar progressBar;
        LayoutInflater li;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            li = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            judul = itemView.findViewById(R.id.judultext);
            deskripsi = itemView.findViewById(R.id.descberita);
            ubahB = itemView.findViewById(R.id.ubahBerita);
            pilihanB = itemView.findViewById(R.id.pilihan);
            lokasi = itemView.findViewById(R.id.locberita);
            fotoBerita = itemView.findViewById(R.id.fotoberita);
            tanggal = itemView.findViewById(R.id.tanggal);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
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
        String phone = phoneNumber.getString(auth, "false");
        if (!phone.equals("false")){
            edNP.setText(phone);
        }
        builder2.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String data = edNP.getText().toString();
                phoneNumber.edit().putString(auth, data).apply();
                if (!data.isEmpty()){
                    if (cekWAInstalled()){
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "https://api.whatsapp.com/send?phone="+convertToCodeNumber(data)+
                                        "&text="+convertToTextWa(dataBerita)));
                        context.startActivity(i);
                        edNP.getText().clear();
                    } else {
                        Toast.makeText(context, "Whatsapp tidak terinstal", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Masukkan nomor telepon terlebih dahulu", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(context, catchError, Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "Terjadi kesalahan saat penentuan penyimpanan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Terjadi kesalahan saat membuat penyimpanan File Text", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void simpanFoto(){
        try {
            OutputStream fo = new FileOutputStream(gambar);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);
            fo.flush();
            fo.close();
            Toast.makeText(context, "File tersimpan di "+folder.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
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
