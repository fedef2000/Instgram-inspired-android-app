package com.example.po_app_project;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.po_app_project.Fragments.HomeFragment;
import com.example.po_app_project.Models.Post;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    private int position =0, id= 0;
    private EditText txtDesc;
    private Button btnSave;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;

    DBHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        init();
    }

    private void init() {
        sharedPreferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        txtDesc = findViewById(R.id.txtDescEditPost);
        btnSave = findViewById(R.id.btnEditPost);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        position = getIntent().getIntExtra("position",0);
        id = getIntent().getIntExtra("postId",0);
        txtDesc.setText(getIntent().getStringExtra("text"));
        databaseHelper = new DBHelper(getApplicationContext());

        btnSave.setOnClickListener(v->{
            if (!txtDesc.getText().toString().isEmpty()){
                savePost();
            }
        });
    }

    private void savePost() {
        dialog.setMessage("Modifiche in corso");
        dialog.show();
        databaseHelper.updatePostDescription(id, txtDesc.getText().toString());
        Post post = HomeFragment.arrayList.get(position);
        post.setDescription(txtDesc.getText().toString());
        HomeFragment.arrayList.set(position,post);
        HomeFragment.recyclerView.getAdapter().notifyItemChanged(position);
        HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
        Toast.makeText(this, "Post salvato", Toast.LENGTH_SHORT).show();
        finish();

    }

    public void cancelEdit(View view){
        super.onBackPressed();
    }
}





















