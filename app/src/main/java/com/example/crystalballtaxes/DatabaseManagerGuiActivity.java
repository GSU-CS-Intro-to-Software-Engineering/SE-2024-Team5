package com.example.crystalballtaxes;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class DatabaseManagerGuiActivity  extends AppCompatActivity {

    EditText editUserID;
    EditText editTextUserName;
    EditText editTextEmail;
    EditText editTextPhone;
    EditText editTextPassword;
    DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_manager_gui);

        editUserID = (EditText) findViewById(R.id.editTextID);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        dbManager = new DatabaseManager(this);
        try{
            dbManager.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void btnInsertPressed(View v){
        dbManager.insert(editTextUserName.getText().toString(), editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextPhone.getText().toString());
    }

    //fetches the data from the database and outputs into logcat window. filter with "DATABASE_TAG" to see the output
    public void btnFetchPressed(View v){
        Cursor cursor = dbManager.fetch();

        //suppress is used to avoid warnings about a value that could be out of range
        if (cursor.moveToFirst()){
            do {
                @SuppressLint("Range") String ID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_NAME));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_EMAIL));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_PASSWORD));
                @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_PHONE));
                Log.i("DATABASE_TAG", "I have read ID : " + ID + " Name : " + name + " Email : " + email + " Password : " + password + " Phone : " + phone);
            }while (cursor.moveToNext());
        }
    }

    //updates the database
    public void btnUpdatePressed(View v){
        dbManager.update(Long.parseLong(editUserID.getText().toString()), editTextUserName.getText().toString(), editTextPassword.getText().toString(), editTextPhone.getText().toString(), editTextEmail.getText().toString());
    }

    //deletes the data from the database
    public void btnDeletePressed(View v){
        dbManager.delete(Long.parseLong(editUserID.getText().toString()));
    }

}
