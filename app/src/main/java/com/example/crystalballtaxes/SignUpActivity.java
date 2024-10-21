package com.example.crystalballtaxes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    //Components of Sign Up UI
    private EditText nameTxtF, emailTxtF, passwordTxtF, phoneTxtF;
    private Button createAccBtn, backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Assigning id's to Sign Up UI
        nameTxtF = findViewById(R.id.newAccUserName);
        emailTxtF = findViewById(R.id.newAccEmail);
        passwordTxtF = findViewById(R.id.newAccPasswrd);
        phoneTxtF = findViewById(R.id.newAccPhoneNum);

        createAccBtn =findViewById(R.id.createNewAccBtn);
    }

    //Takes user back to login page when back arrow button is clicked
    public void goBack(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);

    }
}

/*
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
*/