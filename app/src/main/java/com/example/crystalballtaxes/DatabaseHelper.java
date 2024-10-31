package com.example.crystalballtaxes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper{

    //setting up for the databaseHelper constructor
    static final String DATABASE_NAME = "users.db";
    static final int DATABASE_VERSION = 1;

    //initalize database table column names
    static final String DATABASE_TABLE = "USERS";
    static final String USER_ID = "_ID";
    static final String USER_NAME = "NAME";
    static final String USER_EMAIL = "EMAIL";
    static final String USER_PASSWORD = "PASSWORD";
    static final String USER_PHONE = "PHONE";

    //query to create the database with SQLite
    private static final String CREATE_DB_QUERY = "CREATE TABLE " + DATABASE_TABLE + " (" +
            USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + //adds an autoincrementing ID
            USER_NAME + " TEXT, " +
            USER_EMAIL + " TEXT, " +
            USER_PASSWORD + " TEXT, " +
            USER_PHONE + " TEXT)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creates the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_QUERY);
    }

    //updates the database if there is a change in the version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
    }
}
