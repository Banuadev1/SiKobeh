package com.example.sikobeh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class WartawanInputB extends AppCompatActivity {
    EditText judul, desc, loc;
    Button submit, back;
    ImageView pBerita;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wartawan_input_berita);

        judul = (EditText)findViewById(R.id.add_judul);
        desc = (EditText)findViewById(R.id.add_desc);
        loc = (EditText)findViewById(R.id.add_loc);
        pBerita = (ImageView)findViewById(R.id.inputGambar);

        back = (Button)findViewById(R.id.add_back);
        back.setOnClickListener((view) ->{
            startActivity(new Intent(getApplicationContext(), WartawanForm.class));
        });

        submit = (Button)findViewById(R.id.add_submit);
        submit.setOnClickListener((view)-> processinsert());
    }

    private void processinsert(){
        Map<String, Object> map = new HashMap<>();
        map.put("judul", judul.getText().toString());
        map.put("desc", desc.getText().toString());
        map.put("loc", loc.getText().toString());
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("DataBerita").push()
                .setValue(map)
                .addOnSuccessListener(aVoid ->{
                    judul.setText("");
                    desc.setText("");
                    loc.setText("");
                    Toast.makeText(getApplicationContext(), "Berhasil Ditambahkan", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText( getApplicationContext(), "Terdapat Kesalahan", Toast.LENGTH_SHORT).show());
    }
}
