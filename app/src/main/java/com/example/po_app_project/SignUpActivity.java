package com.example.po_app_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class SignUpActivity extends AppCompatActivity {

    DBHelper databaseHelper;
    private TextInputLayout layoutEmail,layoutPassword,layoutConfirm;
    private TextInputEditText txtEmail,txtPassword,txtConfirm;
    private TextView txtSignIn;
    private Button btnSignUp;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        init();

    }

    private void init() {
        layoutPassword = findViewById(R.id.txtLayoutPasswordSignUp);
        layoutEmail = findViewById(R.id.txtLayoutEmailSignUp);
        layoutConfirm = findViewById(R.id.txtLayoutConfrimSignUp);
        txtPassword = findViewById(R.id.txtPasswordSignUp);
        txtConfirm = findViewById(R.id.txtConfirmSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);
        txtEmail = findViewById(R.id.txtEmailSignUp);
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

            register(txtEmail.getText().toString(), txtPassword.getText().toString(), txtConfirm.getText().toString());

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


    private void register (String email, String password, String confirmPassword){
        if(email.equals("")||password.equals("")||confirmPassword.equals(""))
            Toast.makeText(SignUpActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        else{
            if(password.equals(confirmPassword)){
                Boolean checkUserEmail = databaseHelper.checkEmail(email);
                if(checkUserEmail == false){
                    Boolean insert = databaseHelper.insertData(email, password);
                    if(insert == true){
                        Toast.makeText(SignUpActivity.this, "Signup Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(SignUpActivity.this, "Signup Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SignUpActivity.this, "User already exists! Please login", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(SignUpActivity.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}