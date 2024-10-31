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

public class SignUpActivity extends AppCompatActivity {
    //Components of Sign Up UI
    private EditText nameTxtF, emailTxtF, passwordTxtF, phoneTxtF;
    private Button createAccBtn, backBtn;
    private ProgressBar signUpProgressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Assigning id's to Sign Up UI
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

    //currently this does create an account properly
    //but it does not have a way to log the user out or choose what activity to go to on launch once logged in
    //TODO add a way to log the user out and go to main activity4
    private void createUser() {
        String email = emailTxtF.getText().toString();
        String password = passwordTxtF.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailTxtF.setError("Email cannot be empty");
            emailTxtF.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordTxtF.setError("Password cannot be empty");
            passwordTxtF.requestFocus();
        } else {
            //firebase template for creating a new user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                //takes to login activity after registration
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //Takes user back to login page when back arrow button is clicked
    public void goBack(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);

    }
}