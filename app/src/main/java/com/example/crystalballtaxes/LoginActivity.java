package com.example.crystalballtaxes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailInput, loginPassInput;
    private TextView forgetPass;
    private Button loginBtn, signUpBtn;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    //checks if user is already signed in also firebase template
    @Override
    public void onStart() {
        super.onStart();
        // If user is already logged in then go to main activity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
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
        });

    }

    private void signInUser(){
        String email = loginEmailInput.getText().toString();
        String password = loginPassInput.getText().toString();

        if (TextUtils.isEmpty(email)) {
            loginEmailInput.setError("Email cannot be empty");
            loginEmailInput.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            loginPassInput.setError("Password cannot be empty");
            loginPassInput.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //can enable updating ui based on user info like a welcome Name on login
                                FirebaseUser user  = mAuth.getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else{
                                Toast.makeText(LoginActivity.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}