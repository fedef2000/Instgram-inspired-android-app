package com.example.po_app_project.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.po_app_project.Adapters.PostsAdapter;
import com.example.po_app_project.DBHelper;
import com.example.po_app_project.LoginActivity;
import com.example.po_app_project.Models.User;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.po_app_project.EditUserInfoActivity;
import com.example.po_app_project.HomeActivity;
import com.example.po_app_project.Models.Post;
import com.example.po_app_project.R;

import java.util.ArrayList;

public class AccountFragment extends Fragment {

    private View view;
    private MaterialToolbar toolbar;
    private ImageView imgProfile;
    private TextView txtName,txtPostsCount;
    private Button btnEditAccount;
    private RecyclerView recyclerView;
    private ArrayList<Post> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private SharedPreferences preferences;
    private PostsAdapter adapter;
    DBHelper databaseHelper;

    public AccountFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_account,container,false);
        init();
        return view;
    }

    private void init() {
       toolbar = view.findViewById(R.id.toolbarAccount);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        imgProfile = view.findViewById(R.id.imgAccountProfile);
        txtName = view.findViewById(R.id.txtAccountName);
        txtPostsCount = view.findViewById(R.id.txtAccountPostCount);
        recyclerView = view.findViewById(R.id.recyclerAccount);
        refreshLayout = view.findViewById(R.id.swipeAccount);
        btnEditAccount = view.findViewById(R.id.btnEditAccount);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseHelper = new DBHelper(getActivity());


        btnEditAccount.setOnClickListener(v->{
            startActivity(new Intent(getActivity(), EditUserInfoActivity.class));
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

    }

    private void getData() {
        refreshLayout.setRefreshing(true);

        preferences = getContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        String username = preferences.getString("username","");
        User user = databaseHelper.getUserByUsername(username);

        ArrayList<Post> posts = databaseHelper.getPostsByUsernameId(user.getId(), user.getId());
        txtName.setText(username);
        imgProfile.setImageBitmap(user.getPhoto());
        txtPostsCount.setText(Integer.toString(posts.size()));
        adapter = new PostsAdapter(getContext(),posts);
        recyclerView.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_account,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Sei sicuro di voler uscire?");
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });
            builder.setNegativeButton("Torna indietro", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout(){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(((HomeActivity)getContext()), LoginActivity.class));
                    ((HomeActivity)getContext()).finish();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (!hidden){
            getData();
        }

        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}