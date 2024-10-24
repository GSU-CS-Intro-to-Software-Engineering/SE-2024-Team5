package com.example.crystalballtaxes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLDataException;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;


    public DatabaseManager(Context context) {
        this.context = context;
    }

    //opens the database
    public DatabaseManager open() throws SQLDataException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }

    //closes the database
    public void close() {
        dbHelper.close();
    }

    //inserts values into the database
    public void insert(String name, String email, String password,String phone) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.USER_NAME, name);
        contentValues.put(DatabaseHelper.USER_EMAIL, email);
        contentValues.put(DatabaseHelper.USER_PASSWORD, password);
        contentValues.put(DatabaseHelper.USER_PHONE, phone);
        database.insert(DatabaseHelper.DATABASE_TABLE, null, contentValues);
    }

    //fetches all the values from the database
    //cursor is a pointer to the database
    public Cursor fetch() {
        String[] columns = new String[] {DatabaseHelper.USER_ID, DatabaseHelper.USER_NAME, DatabaseHelper.USER_EMAIL, DatabaseHelper.USER_PASSWORD, DatabaseHelper.USER_PHONE};
        Cursor cursor = database.query(DatabaseHelper.DATABASE_TABLE, columns, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    //updates an id's values
    public int update(long _id, String name, String password, String phone, String email){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.USER_NAME, name);
        contentValues.put(DatabaseHelper.USER_PASSWORD, password);
        contentValues.put(DatabaseHelper.USER_PHONE, phone);
        contentValues.put(DatabaseHelper.USER_EMAIL, email);
        //query to update table according to the id
        int ret = database.update(DatabaseHelper.DATABASE_TABLE, contentValues, DatabaseHelper.USER_ID + "=" + _id, null);
        return ret;
    }

    //deletes an id's values
    public void delete(long _id){
        database.delete(DatabaseHelper.DATABASE_TABLE, DatabaseHelper.USER_ID + "=" + _id, null);
    }
}
