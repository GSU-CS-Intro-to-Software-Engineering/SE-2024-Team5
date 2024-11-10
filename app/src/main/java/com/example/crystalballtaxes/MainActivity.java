package com.example.crystalballtaxes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        Button startBtn = findViewById(R.id.startBtn);
        try {
            startBtn.setOnClickListener(view -> {
                Intent i = new Intent(this, FilingStatusActivity.class);
                startActivity(i);
            });
        }catch (Exception e){
            Log.e("MainActivity", "Error starting FilingStatusActivity", e);
        }
        Button dbBtn = findViewById(R.id.dbBtn);
        try {
            dbBtn.setOnClickListener(view -> {
                Intent i = new Intent(this, DatabaseManagerGuiActivity.class);
                startActivity(i);
            });
            }catch (Exception e){
            Log.e("MainActivity", "Error starting DatabaseManagerGuiActivity", e);
        }


    }

    private void initializeTaxRecord() {
        // get current Firebase user's email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            String userEmail = currentUser.getEmail();

            // get user ID from local database using email
            long userId = getUserIdFromEmail(userEmail);

            if (userId != -1) {
                // check if tax record already exists
                if (db.taxRecordExists(userId)) {
                    // initialize tax record if it doesn't exist
                    long taxRecordId = db.initializeTaxRecord(userId);
                    if (taxRecordId != -1) {
                        Log.d("MainActivity", "Tax record initialized successfully");
                    } else {
                        Log.e("MainActivity", "Failed to initialize tax record");
                        Toast.makeText(this, "Error initializing tax record", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // switch activities
                //put extra allows for a variable to be passed between activities
                //will allow for userID to be passed to each step of the process
                Intent i = new Intent(this, FilingStatusActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
            } else {
                Log.e("MainActivity", "User not found in local database");
                Toast.makeText(this, "User not found in local database", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("MainActivity", "No user currently logged in");
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    private long getUserIdFromEmail(String email) {
        SQLiteDatabase database = db.getReadableDatabase();
        long userId = -1;

        Cursor cursor = database.query(
                "USERS",  // table name
                new String[]{"ID"},  // columns to return
                "EMAIL = ?",  // selection criteria
                new String[]{email},  // selection arguments
                null,  // group by
                null,  // having
                null   // order by
        );

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            long id = cursor.getLong(cursor.getColumnIndex("ID"));
            userId = id;
            cursor.close();
        }

        return userId;
    }

}