package com.example.crystalballtaxes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.Cursor;


import androidx.annotation.Nullable;
/*
*
* User table will have the following columns:
* user_id(primary key), user_name, user_email, user_password(may use a hash), user_phone
*
* Tax table will have the following columns:
* tax_info_id(primary key), user_id (foreign key), filing_status, income, (will add more as tax credits are added (potentially a JSON))
*
* TODO update signup to send info to the db as well
* */
public class DatabaseHelper extends SQLiteOpenHelper{

    //setting up for the databaseHelper constructor
    private static final String TAG = "DatabaseHelper";
    static final String DATABASE_NAME = "crystalBallTaxes.db";
    static final int DATABASE_VERSION = 3;

    //initialize table names
    private static String USER_TABLE = "USERS";
    private static String TAX_INFO_TABLE = "TAX_INFO";

    //initialize id column since both tables use it
    private static String KEY_ID = "ID";

    //initialize user table column names
    private static String USER_NAME = "NAME";
    private static String USER_EMAIL = "EMAIL";
    private static String USER_PASSWORD = "PASSWORD";
    private static String USER_PHONE = "PHONE";

    //initialize tax table column names
    private static String TAX_INFO_ID = "TAX_INFO_ID";
    private static String FILING_STATUS = "FILING_STATUS";
    private static String INCOME = "INCOME";
    private static String USER_ID = "USER_ID";

    //string query for creating the user table
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + USER_NAME + " TEXT, "
            + USER_EMAIL + " TEXT, "
            + USER_PASSWORD + " TEXT, "
            + USER_PHONE + " TEXT);";

    //string query for creating the tax table
    //uses foreign key to link the two tables together
    private static final String CREATE_TABLE_TAX_INFO = "CREATE TABLE " + TAX_INFO_TABLE + "("
            + TAX_INFO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + INCOME + " REAL NOT NULL,"
            + FILING_STATUS + " TEXT NOT NULL,"
            + USER_ID + " INTEGER,"
            + "FOREIGN KEY (" + USER_ID + ") REFERENCES " + USER_TABLE + "(" + KEY_ID + ")"
            + ")";

    //constructor
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TABLE_TAX_INFO);
    }

    //onupgrade function to wipe tables when version updates
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TAX_INFO_TABLE);
        onCreate(db);
    }

    //add user to the database
    public long addUser(String name, String email, String password, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //add values to the table
        values.put(USER_NAME, name);
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, password);
        values.put(USER_PHONE, phone);

        //insert row
        long id = db.insert(USER_TABLE, null, values);
        Log.d(TAG, "added User: " + id);
        return id;
    }

    public boolean updateUser(long id, String name, String email, String password, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //add values to the table
        values.put(USER_NAME, name);
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, password);
        values.put(USER_PHONE, phone);

        //update row and return true if any changes are made
        int rowsAffected = db.update(USER_TABLE, values, KEY_ID + "=" + id, null);
        Log.d(TAG, "updated User: " + rowsAffected);
        return rowsAffected > 0;
    }

    public boolean deleteUser(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        //delete row same as updateUser method
        int rowsAffected = db.delete(USER_TABLE, KEY_ID + "=" + id, null);
        Log.d(TAG, "deleted User: " + rowsAffected);
        return rowsAffected > 0;
    }

    //prints the user table to logcat with tag of DatabaseHelper
    public String[] getCustomer(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] customer = new String[4];
        //set up a cursor pointer to start reading the table from the beginning
        Cursor cursor = db.query(USER_TABLE, new String[]{KEY_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, USER_PHONE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            Log.d(TAG, "id: " + cursor.getString(0)
                    + " name: " + cursor.getString(1)
                    + " email: " + cursor.getString(2));
            customer[0] = cursor.getString(1); //name
            customer[1] = cursor.getString(2); //email
            customer[2] = cursor.getString(3); //password
            customer[3] = cursor.getString(4); //phone
        } else {
            Log.d(TAG, "no user found with id " + id);
        }
        if (cursor != null){
            cursor.close();
        }
        return customer;
    }

    //tax table operations
    public long addTaxInfo(String filingStatus, String income, long userId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(INCOME, income);
        values.put(FILING_STATUS, filingStatus);
        values.put(USER_ID, userId);

        long id = db.insert(TAX_INFO_TABLE, null, values);
        Log.d(TAG, "added Tax Info: " + id);
        return id;
    }

    public boolean updateTaxInfo(long id, String filingStatus, String income, long userId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(INCOME, income);
        values.put(FILING_STATUS, filingStatus);

        int rowsAffected = db.update(TAX_INFO_TABLE, values, KEY_ID + "=" + id, null);
        Log.d(TAG, "updated Tax Info: " + rowsAffected);
        return rowsAffected > 0;
    }

    public boolean deleteTaxInfo(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsAffected = db.delete(TAX_INFO_TABLE, TAX_INFO_ID + "= ?",
                new String[]{String.valueOf(id)});
        Log.d(TAG, "deleted Tax Info: " + rowsAffected);
        return rowsAffected > 0;
    }

    public void getTaxInfo(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TAX_INFO_TABLE, new String[]{KEY_ID, INCOME, FILING_STATUS, USER_ID}, KEY_ID + "= ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            Log.d(TAG, "id: " + cursor.getString(0)
                    + " income: " + cursor.getString(1)
                    + " filing status: " + cursor.getString(2)
                    + " user id: " + cursor.getString(3));
        } else {
            Log.d(TAG, "no tax info found with id " + id);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    //get all users in the database
    //range suppress for get column index since it is possible for the db to not be initalized
    // when calling the method which would throw an error
    @SuppressLint("Range")
    public void logALlData(){
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "------ALL USERS-------: ");
        Cursor userCursor = db.query(USER_TABLE,null, null, null, null, null, null, null);
        if (userCursor != null && userCursor.moveToFirst()) {
            do {
                Log.d(TAG, "User: " +
                        "\nID: " + userCursor.getLong(userCursor.getColumnIndex(USER_ID)) +
                        "\nName: " + userCursor.getString(userCursor.getColumnIndex(USER_NAME)) +
                        "\nEmail: " + userCursor.getString(userCursor.getColumnIndex(USER_EMAIL)) +
                        "\nPhone: " + userCursor.getString(userCursor.getColumnIndex(USER_PHONE)));
            } while (userCursor.moveToNext());
        }
        userCursor.close();

        //log tax info
        Log.d(TAG, "------ALL TAX INFO-------: ");
        Cursor taxCursor = db.query(TAX_INFO_TABLE,null, null, null, null, null, null, null);
        if (taxCursor != null && taxCursor.moveToFirst()) {
            do {
                Log.d(TAG, "Tax Info: " +
                        "\nTax Info ID: " + taxCursor.getLong(taxCursor.getColumnIndex(TAX_INFO_ID)) +
                        "\nIncome: " + taxCursor.getDouble(taxCursor.getColumnIndex(INCOME)) +
                        "\nFiling Status: " + taxCursor.getString(taxCursor.getColumnIndex(FILING_STATUS)) +
                        "\nCustomer ID: " + taxCursor.getLong(taxCursor.getColumnIndex(USER_ID)));
            } while (taxCursor.moveToNext());
        }
        //assert to avoid nullpointer exception
        assert taxCursor != null;
        taxCursor.close();
    }

    public boolean checkUser(String email, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_EMAIL, USER_PASSWORD};
        String selection = USER_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(USER_TABLE, columns, selection, selectionArgs, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            @SuppressLint("Range") String storedPassword = cursor.getString(cursor.getColumnIndex(USER_PASSWORD));
            @SuppressLint("Range") String storedEmail = cursor.getString(cursor.getColumnIndex(USER_EMAIL));

            if(storedPassword.equals(password) && storedEmail.equals(email)) {
                return true;
            }
        }

        return false;
    }

}
