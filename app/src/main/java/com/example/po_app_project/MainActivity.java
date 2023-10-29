package com.example.po_app_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this code will pause the app for 1.5 secs and then any thing in run method will run.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
                String username = userPref.getString("username","");
                if (new DBHelper(getApplicationContext()).checkIfUserExists("", username)){
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                    finish();
                }
            }
        },1500);
    }


}