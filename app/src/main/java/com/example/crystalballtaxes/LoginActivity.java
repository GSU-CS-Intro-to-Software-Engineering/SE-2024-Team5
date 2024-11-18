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



public class LoginActivity extends AppCompatActivity {
    private EditText loginEmailInput, loginPassInput;
    private TextView forgetPass;
    private Button loginBtn, signUpBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseHelper db;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.loginProgressBar);
        loginEmailInput = findViewById(R.id.emailEditTxt);
        loginPassInput = findViewById(R.id.passwordEditTxt);
        forgetPass = findViewById(R.id.forgotPassTxt);
        loginBtn = findViewById(R.id.loginBtn);
        signUpBtn = findViewById(R.id.signUpBtn);

        progressBar.setVisibility(View.GONE);

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
        String email = loginEmailInput.getText().toString().trim();
        String password = loginPassInput.getText().toString().trim();
        long userID = db.getUserIdFromEmail(email);

        if (TextUtils.isEmpty(email)) {
            loginEmailInput.setError("Email cannot be empty");
            loginEmailInput.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        } else if (TextUtils.isEmpty(password)) {
            loginPassInput.setError("Password cannot be empty");
            loginPassInput.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // First check SQLite
        if (db != null && db.checkUser(email, password)) {
            // If SQLite check passes, then verify with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Both SQLite and Firebase authentication successful
                                Toast.makeText(LoginActivity.this, "Login successful",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                                finish();
                            } else {
                                // Firebase authentication failed
                                Toast.makeText(LoginActivity.this,
                                        "Firebase authentication failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Invalid email or password",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void forgotPassword() {
        String email = loginEmailInput.getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            // Check if user exists in SQLite first
            long userId = db.getUserIdFromEmail(email);
            if (userId != -1) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,
                                            "Password reset email sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Failed to send reset email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(LoginActivity.this,
                        "No account found with this email", Toast.LENGTH_SHORT).show();
            }
        } else {
            loginEmailInput.setError("Enter email to reset password");
            loginEmailInput.requestFocus();
        }
    }
}