package com.example.crystalballtaxes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class FilingStatusActivity extends AppCompatActivity {
    private RadioButton singleRBtn;
    private RadioButton marriedJointBtn;
    private RadioButton marriedFiledSepRBtn;
    private RadioButton headOfHouseRBtn;
    private Button nextBtn;
    private RadioGroup radioGroup;
    private TextView errorText;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filing_status);

        db = new DatabaseHelper(this);

        radioGroup = findViewById(R.id.radioGroup);
        nextBtn = findViewById(R.id.nextBtn);
        errorText = findViewById(R.id.errorText);

        singleRBtn = findViewById(R.id.singleRBtn);
        marriedJointBtn = findViewById(R.id.marriedJointBtn);
        marriedFiledSepRBtn = findViewById(R.id.marriedFiledSepRBtn);
        headOfHouseRBtn = findViewById(R.id.headOfHouseRBtn);

        //hide error on start
        errorText.setVisibility(View.GONE);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    // show error if no selection
                    errorText.setVisibility(View.VISIBLE);
                    radioGroup.requestFocus();
                    return;
                }

                String filingStatus = "";

                // get the selected radio button
                if (singleRBtn.isChecked()) {
                    filingStatus = "Single";
                    Log.d("FilingStatusActivity", "single");
                } else if (marriedJointBtn.isChecked()) {
                    filingStatus = "Married Filing Jointly";
                    Log.d("FilingStatusActivity", "married");
                } else if (marriedFiledSepRBtn.isChecked()) {
                    filingStatus = "Married Filing Separately";
                    Log.d("FilingStatusActivity", "separated");
                } else if (headOfHouseRBtn.isChecked()) {
                    filingStatus = "Head of Household";
                    Log.d("FilingStatusActivity", "HOH");
                }

                // get current Firebase user's email
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null && currentUser.getEmail() != null) {
                    String userEmail = currentUser.getEmail();

                    // get user ID from local database using email
                    long userId = db.getUserIdFromEmail(userEmail);

                    if (userId != -1) {
                        if (!db.taxRecordExists(userId)) {
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
            }
        });

    }

}