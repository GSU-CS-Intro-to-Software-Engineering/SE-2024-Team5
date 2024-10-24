package com.example.crystalballtaxes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Assigning id's to Sign Up UI
        nameTxtF = findViewById(R.id.newAccUserName);
        emailTxtF = findViewById(R.id.newAccEmail);
        passwordTxtF = findViewById(R.id.newAccPasswrd);
        phoneTxtF = findViewById(R.id.newAccPhoneNum);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);

        createAccBtn =findViewById(R.id.createNewAccBtn);

        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpProgressBar.setVisibility(View.VISIBLE);
                String email = emailTxtF.getText().toString();
                String password = passwordTxtF.getText().toString();

                // Check if email and password are not empty
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignUpActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //firebase template for creating new account
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                signUpProgressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    //Takes user back to login page when back arrow button is clicked
    public void goBack(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);

    }
}