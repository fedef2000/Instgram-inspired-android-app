package com.example.po_app_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.po_app_project.Models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    DBHelper databaseHelper;
    private TextInputLayout layoutEmail,layoutPassword;
    private TextInputEditText txtEmail,txtPassword;
    private TextView txtSignUp;
    private Button btnSignIn;
    private ProgressDialog dialog;

    public LoginActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        layoutPassword = findViewById(R.id.txtLayoutPasswordSignIn);
        layoutEmail = findViewById(R.id.txtLayoutEmailSignIn);
        txtPassword = findViewById(R.id.txtPasswordSignIn);
        txtSignUp = findViewById(R.id.txtSignUp);
        txtEmail = findViewById(R.id.txtEmailSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        databaseHelper = new DBHelper(this);

        txtSignUp.setOnClickListener(v->{
            //change fragments
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        btnSignIn.setOnClickListener(v->{
            //validate fields first
            login(txtEmail.getText().toString(), txtPassword.getText().toString());
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
    }

    private void login (String username, String password) {
        dialog.setMessage("Login in corso");
        dialog.show();

        if(username.equals("")||password.equals("")){
            Toast.makeText(LoginActivity.this, "Inserisci tutti i campi", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }else{
            Boolean checkCredentials = databaseHelper.checkCredentials(username, password);
            if(checkCredentials){
                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userPref.edit();
                editor.putString("username", username);
                editor.apply();

                Intent intent  = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(LoginActivity.this, "Dati inseriti non corretti", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }

    }
}