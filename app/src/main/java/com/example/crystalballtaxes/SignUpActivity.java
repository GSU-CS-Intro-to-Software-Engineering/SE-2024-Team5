package com.example.crystalballtaxes;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    EditText emailTextEdit, passwordTextEdit;
    Button signUpBtn;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailTextEdit = findViewById(R.id.emailTextEdit);
        passwordTextEdit = findViewById(R.id.passwordTextEdit);
        signUpBtn = findViewById(R.id.signUp);
        progressBar = findViewById(R.id.progressBar);

        signUpBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = emailTextEdit.getText().toString();
            String password = passwordTextEdit.getText().toString();

            // Check if email and password are not empty
            if(TextUtils.isEmpty(email)){
                Toast.makeText(SignUpActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(password)){
                Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user with email and password using firebase template
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, "Account created.",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
        }
    }
