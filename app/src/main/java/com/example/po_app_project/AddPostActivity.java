package com.example.po_app_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.po_app_project.Models.User;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {
    DBHelper databaseHelper;
    private Button btnPost;
    private ImageView imgPost;
    private EditText txtDesc;
    private Bitmap bitmap = null;
    private static final  int GALLERY_CHANGE_POST = 3;
    private ProgressDialog dialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        init();

    }


    private void init() {
        btnPost = findViewById(R.id.btnAddPost);
        imgPost = findViewById(R.id.imgAddPost);
        txtDesc = findViewById(R.id.txtDescAddPost);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        databaseHelper = new DBHelper(this);

        Uri uri = getIntent().getData();
        try {
            Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imgPost.setImageBitmap(b);
            if (b.getHeight() > 1000 || b.getWidth() > 2000){
                if(b.getWidth() > b.getHeight()){
                    int n = b.getWidth()/1000;
                    if (n == 1) n = 2;
                    bitmap = Bitmap.createScaledBitmap(b, b.getWidth() / n, b.getHeight()/n, true);
                    imgPost.setImageBitmap(bitmap);
                }else {
                    int n = b.getHeight()/1000;
                    if (n == 1) n = 2;
                    bitmap = Bitmap.createScaledBitmap(b, b.getWidth() / n, b.getHeight()/n, true);
                    imgPost.setImageBitmap(bitmap);
                }
            }
            else {
                bitmap = b;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }



        btnPost.setOnClickListener(v->{
            if(!txtDesc.getText().toString().isEmpty()){
                post();
            }else {
                Toast.makeText(this, "Inserisci una descrizione", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void post(){
        dialog.setMessage("Salvataggio in corso");
        dialog.show();
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String username= preferences.getString("username", "");
        User user = databaseHelper.getUserByUsername(username);

        databaseHelper.insertPost(getByteStream(bitmap), txtDesc.getText().toString(), new SimpleDateFormat("dd-MM-yyyy").format(new Date()), 0, user.getId());
        Toast.makeText(this, "Post salvato", Toast.LENGTH_SHORT).show();
        finish();

    }

    public void cancelPost(View view) {
        super.onBackPressed();
    }

    public void changePhoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALLERY_CHANGE_POST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CHANGE_POST && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            imgPost.setImageURI(imgUri);
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                if (b.getHeight() > 1000 || b.getWidth() > 2000){
                    if(b.getWidth() > b.getHeight()){
                        int n = b.getWidth()/1000;
                        if (n == 1) n = 2;
                        bitmap = Bitmap.createScaledBitmap(b, b.getWidth() / n, b.getHeight()/n, true);
                        imgPost.setImageBitmap(bitmap);
                    }else {
                        int n = b.getHeight()/1000;
                        if (n == 1) n = 2;
                        bitmap = Bitmap.createScaledBitmap(b, b.getWidth() / n, b.getHeight()/n, true);
                        imgPost.setImageBitmap(bitmap);
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

    private byte[] getByteStream(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

}