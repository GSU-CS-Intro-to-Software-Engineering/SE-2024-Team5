package com.example.crystalballtaxes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.example.crystalballtaxes.DatabaseHelper;
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
        Intent intent = getIntent();
        long userID = intent.getLongExtra("userID", -1);

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
                Log.d("MainActivity", "Starting FilingStatusActivity");
                db.initializeTaxRecord(userID);
                Intent i = new Intent(this, FilingStatusActivity.class);
                i.putExtra("userID", userID);
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