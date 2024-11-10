package com.example.crystalballtaxes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class FilingStatusActivity extends AppCompatActivity {
    private RadioButton singleRBtn;
    private RadioButton marriedJointBtn;
    private RadioButton marriedFiledSepRBtn;
    private RadioButton headOfHouseRBtn;
    private Button nextBtn;
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filing_status);

        db = new DatabaseHelper(this);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filingStatus = "";

                // Get the selected radio button
                if (singleRBtn.isChecked()) {
                    filingStatus = "Single";
                } else if (marriedJointBtn.isChecked()) {
                    filingStatus = "Married Filing Jointly";
                } else if (marriedFiledSepRBtn.isChecked()) {
                    filingStatus = "Married Filing Separately";
                } else if (headOfHouseRBtn.isChecked()) {
                    filingStatus = "Head of Household";
                }

                if (!filingStatus.isEmpty()) {
                    // get current Firebase user's email
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null && currentUser.getEmail() != null) {
                        String userEmail = currentUser.getEmail();

                        // get user ID from local database using email
                        long userId = db.getUserIdFromEmail(userEmail);

                        if (userId != -1) {
                            // check if tax record exists, if not initialize it
                            if (db.taxRecordExists(userId)) {
                                long taxRecordId = db.initializeTaxRecord(userId);
                                if (taxRecordId == -1) {
                                    Toast.makeText(FilingStatusActivity.this,
                                            "Error initializing tax record", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            // update filing status
                            boolean success = db.updateFilingStatus(userId, filingStatus);

                            if (success) {
                                // successfully saved to database
                                Toast.makeText(FilingStatusActivity.this,
                                        "Filing status saved", Toast.LENGTH_SHORT).show();

                                // navigate to next screen
                                Intent intent = new Intent(FilingStatusActivity.this,
                                        DependentsActivity.class);
                                intent.putExtra("USER_ID", userId);  // Pass the SQLite user ID
                                startActivity(intent);
                                finish();
                            } else {
                                // error saving to database
                                Toast.makeText(FilingStatusActivity.this,
                                        "Error saving filing status", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FilingStatusActivity.this,
                                    "User not found in local database", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    //set way to tell user to select an option
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilingStatusActivity.this, DependentsActivity.class);
                startActivity(intent);
            }
        });
    }

}