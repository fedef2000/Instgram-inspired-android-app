package com.example.po_app_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;


public class SignUpActivity extends AppCompatActivity {

    DBHelper databaseHelper;
    private TextInputLayout layoutEmail,layoutUsername,layoutPassword,layoutConfirm;
    private TextInputEditText txtEmail,txtUsername,txtPassword,txtConfirm;
    private TextView txtSignIn;
    private Button btnSignUp;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();

    }

    private void init() {
        layoutEmail = findViewById(R.id.txtLayoutEmailSignUp);
        layoutUsername = findViewById(R.id.txtLayoutUsernameSignUp);
        layoutPassword = findViewById(R.id.txtLayoutPasswordSignUp);
        layoutConfirm = findViewById(R.id.txtLayoutConfrimSignUp);
        txtEmail = findViewById(R.id.txtEmailSignUp);
        txtUsername = findViewById(R.id.txtUsernameSignUp);
        txtPassword = findViewById(R.id.txtPasswordSignUp);
        txtConfirm = findViewById(R.id.txtConfirmSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        databaseHelper = new DBHelper(this);



        txtSignIn.setOnClickListener(v->{
            //change activity
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        btnSignUp.setOnClickListener(v->{

            register(txtEmail.getText().toString(), txtUsername.getText().toString(), txtPassword.getText().toString(), txtConfirm.getText().toString());

        });


        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtEmail.getText().toString().isEmpty()){
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtUsername.getText().toString().isEmpty()){
                    layoutUsername.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtPassword.getText().toString().length()>7){
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
                    layoutConfirm.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void register (String email, String username, String password, String confirmPassword){

        if(email.equals("")|| username.equals("") || password.equals("")||confirmPassword.equals(""))
            Toast.makeText(SignUpActivity.this, "Inserisci tutti i campi", Toast.LENGTH_SHORT).show();
        else{
            if(password.equals(confirmPassword)){
                Boolean checkUserEmail = databaseHelper.checkIfUserExists(email,username);
                if(!checkUserEmail){
                    Boolean insert = databaseHelper.insertUser(email, username, password, getDefaultImage());
                    if(insert){
                        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = userPref.edit();
                        editor.putString("username", username);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(SignUpActivity.this, "Si è verificato un errore!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SignUpActivity.this, "Esiste già un utente con la mail inserita! Effettua il login", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(SignUpActivity.this, "Le due password sono diverse!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] getDefaultImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

}