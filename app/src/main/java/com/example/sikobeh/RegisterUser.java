package com.example.sikobeh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import es.dmoral.toasty.Toasty;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registeruser;
    private EditText editTextfullname, editTextage, editTextemail, editTextpassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    public static String AES = "AES";
    public static String encryptedPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        
        mAuth = FirebaseAuth.getInstance();
        banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);
        registeruser = findViewById(R.id.registerUser);
        registeruser.setOnClickListener(this);

        editTextfullname = findViewById(R.id.fullname);
        editTextage = findViewById(R.id.pnumber);
        editTextemail = findViewById(R.id.email);
        editTextpassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }
    private void registerUser() {
        String email = editTextemail.getText().toString().trim();
        String password = editTextpassword.getText().toString().trim();
        String fullname = editTextfullname.getText().toString().trim();
        String pnumber = editTextage.getText().toString().trim();

        if (fullname.isEmpty()) {
            editTextfullname.setError("Nama Lengkap harus di isi");
            editTextfullname.requestFocus();
            return;
        }
        if (pnumber.isEmpty()) {
            editTextage.setError("Umur harus di isi");
            editTextage.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextemail.setError("Email Harus di isi");
            editTextemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextemail.setError("Harap masukan email yang benar!");
            editTextemail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextpassword.setError("Password dibutuhkan");
            editTextpassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextpassword.setError("Password minimal 6 karakter");
            editTextpassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    try {
                        encryptedPass = encrypt(password, "rasul19");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String pass = encryptedPass;
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    User user = new User(fullname, pnumber, email, pass, uid);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toasty.success(RegisterUser.this, "Akun Berhasil Di Daftarkan!", Toast.LENGTH_LONG, true).show();
                                progressBar.setVisibility(View.GONE);
                                editTextfullname.setText("");
                                editTextemail.setText("");
                                editTextage.setText("");
                                editTextpassword.setText("");
                                FirebaseAuth.getInstance().signOut();
                            }else{
                                Toasty.error(RegisterUser.this, "Akun Gagal Terdaftar!!", Toast.LENGTH_LONG, true).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }else {
                    Toasty.error(RegisterUser.this, task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public static String decrypt(String data, String pass) throws Exception {
        SecretKeySpec key = generateKey(pass);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(data, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    public String encrypt(String data, String pass) throws Exception {
        SecretKeySpec key = generateKey(pass);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    public static SecretKeySpec generateKey(String pass) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = pass.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
}