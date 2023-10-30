package com.example.po_app_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.po_app_project.Models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class NewUserActivity extends AppCompatActivity {
    DBHelper databaseHelper;
    private TextView txtSelectPhoto;
    private Button btnContinue;
    private ImageView circleImageView;
    private static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
    }

    private void init() {
        databaseHelper = new DBHelper(this);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);
        btnContinue = findViewById(R.id.btnContinue);
        circleImageView = findViewById(R.id.imgUserInfo);

        SharedPreferences preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");
        User user = databaseHelper.getUserByUsername(username);
        circleImageView.setImageBitmap(user.getPhoto());
        bitmap = user.getPhoto();

        //pick photo from gallery
        txtSelectPhoto.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_PROFILE);
        });


        btnContinue.setOnClickListener(v->{
                saveUserInfo();
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_ADD_PROFILE && resultCode==RESULT_OK){
            Uri uri = data.getData();
            circleImageView.setImageURI(uri);
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
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



    private void saveUserInfo(){
        SharedPreferences preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");
        User user = databaseHelper.getUserByUsername(username);
        databaseHelper.updateUser(user.getId(), username, getByteStream(bitmap));
        startActivity(new Intent(NewUserActivity.this,HomeActivity.class));
        finish();
    }

    private byte[] getByteStream(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }


}