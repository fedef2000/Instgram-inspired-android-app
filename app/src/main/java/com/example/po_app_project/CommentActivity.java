package com.example.po_app_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.po_app_project.Adapters.CommentsAdapter;
import com.example.po_app_project.Models.Comment;
import com.example.po_app_project.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Comment> list;
    private CommentsAdapter adapter;
    private int postId = 0;
    public  static  int postPosition = 0;
    private SharedPreferences preferences;
    private EditText txtAddComment;
    private ProgressDialog dialog;
    private DBHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        postPosition = getIntent().getIntExtra("postPosition",-1);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtAddComment = findViewById(R.id.txtAddComment);
        postId = getIntent().getIntExtra("postId",0);
        databaseHelper = new DBHelper(getApplicationContext());


        getComments(postId);
    }

    private void getComments(int postId) {
        list = databaseHelper.getCommentsByPostId(postId);
        adapter = new CommentsAdapter(this,list);
        recyclerView.setAdapter(adapter);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    public void addComment(View view) {
        String commentText = txtAddComment.getText().toString();
        preferences = getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        User u = databaseHelper.getUserByUsername(preferences.getString("username",""));
        Comment c = databaseHelper.insertComment(u.getId(),postId,commentText);
        list.add(c);
        recyclerView.getAdapter().notifyDataSetChanged();
        txtAddComment.setText("");
    }



}