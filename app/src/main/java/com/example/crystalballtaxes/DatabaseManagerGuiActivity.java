package com.example.crystalballtaxes;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class DatabaseManagerGuiActivity  extends AppCompatActivity {

    EditText editUserID;
    EditText editTextUserName;
    EditText editTextEmail;
    EditText editTextPhone;
    EditText editTextPassword;
    DatabaseHelper dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_manager_gui);

        editUserID = (EditText) findViewById(R.id.editTextID);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        dbManager = new DatabaseHelper(this);

    }

}
