package com.example.crystalballtaxes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.crystalballtaxes.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {
    private EditText nameTxtF, emailTxtF, passwordTxtF, phoneTxtF;
    private Button createAccBtn, backBtn;
    private ProgressBar signUpProgressBar;
    private FirebaseAuth mAuth;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = new DatabaseHelper(this);

        nameTxtF = findViewById(R.id.newAccUserName);
        emailTxtF = findViewById(R.id.newAccEmail);
        passwordTxtF = findViewById(R.id.newAccPasswrd);
        phoneTxtF = findViewById(R.id.newAccPhoneNum);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);
        createAccBtn = findViewById(R.id.createNewAccBtn);

        signUpProgressBar.setVisibility(View.INVISIBLE);

        createAccBtn.setOnClickListener(view -> {
            signUpProgressBar.setVisibility(View.VISIBLE);
            createUser();
        });
    }

    private void createUser() {
        String email = emailTxtF.getText().toString().trim();
        String password = passwordTxtF.getText().toString().trim();
        String name = nameTxtF.getText().toString().trim();
        String phone = phoneTxtF.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            // handle empty fields
            signUpProgressBar.setVisibility(View.INVISIBLE);
            return;
        }

        // first verify database is initialized and log
        if (!db.isDatabaseInitialized()) {
            Log.e("SignUpActivity", "Database not properly initialized!");
            Toast.makeText(this, "Database initialization error", Toast.LENGTH_LONG).show();
            signUpProgressBar.setVisibility(View.INVISIBLE);
            return;
        }

        //firebase authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // add to SQLite with logging
                            Log.d("SignUpActivity", "Firebase auth successful, adding to SQLite...");
                            long userId = db.addUser(name, email, password, phone);

                            if (userId != -1) {
                                Log.d("SignUpActivity", "Successfully added user to SQLite with ID: " + userId);

                                // verify user was added
                                long verifyId = db.getUserIdFromEmail(email);
                                if (verifyId != -1) {
                                    Log.d("SignUpActivity", "Verified user exists in SQLite with ID: " + verifyId);

                                    // initialize tax record
                                    long taxRecordId = db.initializeTaxRecord(userId);
                                    Log.d("SignUpActivity", "Initialized tax record: " + taxRecordId);

                                    Toast.makeText(SignUpActivity.this,
                                            "User registered successfully", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    intent.putExtra("userID", userId);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.e("SignUpActivity", "User added but verification failed!");
                                    handleRegistrationError("Database verification failed");
                                }
                            } else {
                                handleRegistrationError("Failed to create local user record");
                            }
                        } else {
                            handleRegistrationError(task.getException().getMessage());
                        }
                        signUpProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void handleRegistrationError(String error) {
        Log.e("SignUpActivity", "Registration error: " + error);
        Toast.makeText(SignUpActivity.this, "Registration Error: " + error,
                Toast.LENGTH_SHORT).show();
        // delete user from Firebase auth if registration fails
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete();
        }
    }


    //takes user back to login page when back arrow button is clicked
    public void goBack(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);

    }
}