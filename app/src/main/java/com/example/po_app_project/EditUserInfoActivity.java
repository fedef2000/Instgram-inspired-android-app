package com.example.po_app_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.po_app_project.Models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserInfoActivity extends AppCompatActivity {

    private TextInputLayout layoutName;
    private TextInputEditText txtName;
    private TextView txtSelectPhoto;
    private Button btnSave;
    private CircleImageView circleImageView;
    private static final int GALLERY_CHANGE_PROFILE = 5;
    private Bitmap bitmap = null;
    private SharedPreferences userPref;
    private ProgressDialog dialog;
    DBHelper databaseHelper;
    private SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutName = findViewById(R.id.txtEditLayoutNameUserInfo);
        txtName = findViewById(R.id.txtEditNameUserInfo);
        txtSelectPhoto = findViewById(R.id.txtEditSelectPhoto);
        btnSave = findViewById(R.id.btnEditSave);
        circleImageView = findViewById(R.id.imgEditUserInfo);
        databaseHelper = new DBHelper(this);
        preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
        String username = preferences.getString("username","");
        User user = databaseHelper.getUserByUsername(username);
        circleImageView.setImageBitmap(user.getPhoto());
        txtName.setText(username);

        txtSelectPhoto.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_CHANGE_PROFILE);
        });

        btnSave.setOnClickListener(v->{
            if (validate(user.getId())){
                if (bitmap == null) {
                    bitmap = user.getPhoto();
                }
                updateProfile(user.getId());
            }
        });
    }


    private void updateProfile(int id){
        dialog.setMessage("Aggiornamento del profilo in corso");
        dialog.show();
        databaseHelper.updateUser(id, txtName.getText().toString().trim(), getByteStream(bitmap));
        SharedPreferences.Editor editor = userPref.edit();
        editor.putString("username", txtName.getText().toString().trim());
        editor.apply();
        Toast.makeText(this, "Profilo aggiornato", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CHANGE_PROFILE && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            circleImageView.setImageURI(imgUri);
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);

                if (b.getHeight() > 1000 || b.getWidth() > 2000){
                    if(b.getWidth() > b.getHeight()){
                        int n = b.getWidth()/1000;
                        if (n == 1) n = 2;
                        bitmap = Bitmap.createScaledBitmap(b, b.getWidth() / n, b.getHeight()/n, true);

                    }else {
                        int n = b.getHeight()/1000;
                        if (n == 1) n = 2;
                        bitmap = Bitmap.createScaledBitmap(b, b.getWidth() / n, b.getHeight()/n, true);

                    }
                }
                else {
                    bitmap = b;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validate(int id){
        if (txtName.getText().toString().isEmpty()){
            layoutName.setErrorEnabled(true);
            layoutName.setError("Inserisci un username");
            return false;
        }
        String newUsername = txtName.getText().toString();
        if(databaseHelper.checkIfUserExists("", newUsername)){
            User u1 = databaseHelper.getUserByUsername(newUsername);
            if(u1.getId() != id){
                layoutName.setErrorEnabled(true);
                layoutName.setError("Il nuovo username è già stato scelto");
                return false;
            }
        }

        return true;
    }

    private byte[] getByteStream(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
}