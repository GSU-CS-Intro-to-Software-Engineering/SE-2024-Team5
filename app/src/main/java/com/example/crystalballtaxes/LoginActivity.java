package com.example.crystalballtaxes;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.crystalballtaxes.DatabaseHelper;

//TODO  welcome user on signin

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailInput, loginPassInput;
    private TextView forgetPass;
    private Button loginBtn, signUpBtn, forgotPassBtn;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseHelper db;

    //checks if user is already signed in also firebase template
    @Override
    public void onStart() {
        super.onStart();
        // If user is already logged in then go to main activity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //this ignores a false progress bar error
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initialize db
        db = new DatabaseHelper(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(View.GONE);

        loginEmailInput = findViewById(R.id.emailEditTxt);
        loginPassInput = findViewById(R.id.passwordEditTxt);

        forgetPass = findViewById(R.id.forgotPassTxt);

        loginBtn = findViewById(R.id.loginBtn);
        signUpBtn = findViewById(R.id.signUpBtn);

        loginBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            signInUser();
        });
        signUpBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
            finish();
        });
        forgetPass.setOnClickListener(view -> {
            forgotPassword();
        });

    }

    private void signInUser() {
        String email = loginEmailInput.getText().toString();
        String password = loginPassInput.getText().toString();

        if (TextUtils.isEmpty(email)) {
            loginEmailInput.setError("Email cannot be empty");
            loginEmailInput.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            loginPassInput.setError("Password cannot be empty");
            loginPassInput.requestFocus();
            //authenticate with db before checking with firebase
        } else // add null check to ensure the db is intialized
            if (db != null && db.checkUser(email, password)) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);  // Hide progress bar after authentication
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                progressBar.setVisibility(View.GONE);  // Hide progress bar if user doesn't exist
                Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
            }
    }


    public void forgotPassword(){

        String email = loginEmailInput.getText().toString();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }
}