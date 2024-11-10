package com.example.crystalballtaxes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.Cursor;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
*
* User table will have the following columns:
* user_id(primary key), user_name, user_email, user_password(may use a hash), user_phone
*
* Tax table will have the following columns:
* tax_info_id(primary key), user_id (foreign key), filing_status, income, (will add more as tax credits are added)
*
*
* TODO add more comments as i have to go to work
* */
public class DatabaseHelper extends SQLiteOpenHelper{

    //setting up for the databaseHelper constructor
    private static final String TAG = "DatabaseHelper";
    static final String DATABASE_NAME = "crystalBallTaxes.db";
    static final int DATABASE_VERSION = 4;

    //initialize table names
    private static String USER_TABLE = "USERS";
    private static String TAX_INFO_TABLE = "TAX_INFO";
    private static String DEPENDENT_INFO_TABLE = "DEPENDENTS";

    //initialize id column since both tables use it
    private static String KEY_ID = "ID";

    //initialize user table column names
    private static String USER_NAME = "NAME";
    private static String USER_EMAIL = "EMAIL";
    private static String USER_PASSWORD = "PASSWORD";
    private static String USER_PHONE = "PHONE";

    //initialize tax table column names
    private static String TAX_INFO_ID = "TAX_INFO_ID";
    private static String USER_ID = "USER_ID";
    private static String FILING_STATUS = "FILING_STATUS";
    private static String INCOME = "INCOME";
    private static String TAX_CREDITS = "TAX_CREDITS";
    private static String ABOVE_LINE_DEDUCTIONS = "ABOVE_LINE_DEDUCTIONS";
    private static String ITEMIZED_DEDUCTIONS = "ITEMIZED_DEDUCTIONS";
    private static String DEPENDENTS = "DEPENDENTS";

    //Initialize dependent table column names
    private static String DEPENDENT_ID = "DEPENDENT_ID";
    private static String DEPENDENT_FNAME = "DEPENDENT_FIRST_NAME";
    private static String DEPENDENT_LNAME = "DEPENDENT_LAST_NAME";
    private static String DEPENDENT_SSN = "DEPENDENT_SSN";
    private static String DEPENDENT_DOB = "DEPENDENT_DOB";
    private static String DEPENDENT_RELATION = "DEPENDENT_RELATION";

    //string query for creating the user table
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_NAME + " TEXT, " +
            USER_EMAIL + " TEXT, " +
            USER_PASSWORD + " TEXT, " +
            USER_PHONE + " TEXT);";

    //string query for creating the tax table
    //uses foreign key to link the two tables together
    private static final String CREATE_TABLE_TAX_INFO = "CREATE TABLE " + TAX_INFO_TABLE + "(" +
            TAX_INFO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            USER_ID + " INTEGER," +
            INCOME + " TEXT NOT NULL," +
            FILING_STATUS + " TEXT NOT NULL," +
            TAX_CREDITS + " TEXT," +
            ABOVE_LINE_DEDUCTIONS + " TEXT," +
            ITEMIZED_DEDUCTIONS + " TEXT," +
            DEPENDENTS + " TEXT," +
            "FOREIGN KEY (" + USER_ID + ") REFERENCES " + USER_TABLE + "(" + KEY_ID + ")" +
            ")";

    //create dependent table
    private static final String CREATE_TABLE_DEPENDENTS = "CREATE TABLE " + DEPENDENT_INFO_TABLE + "("
            + USER_ID + " INTEGER,"
            + DEPENDENT_ID + " INTEGER NOT NULL,"
            + DEPENDENT_FNAME + " TEXT,"
            + DEPENDENT_LNAME + " TEXT,"
            + DEPENDENT_SSN + " TEXT,"
            + DEPENDENT_DOB + " TEXT,"
            + DEPENDENT_RELATION + " TEXT,"
            + "PRIMARY KEY (" + DEPENDENT_ID + "," + USER_ID + ")," //composite primary key to ensure unique dependent id
            + "FOREIGN KEY (" + USER_ID + ") REFERENCES " + USER_TABLE + "(" + KEY_ID + ")" +
            ")";

    //constructor
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TABLE_TAX_INFO);
        db.execSQL(CREATE_TABLE_DEPENDENTS);
    }

    //onupgrade function to wipe tables when version updates
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TAX_INFO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DEPENDENT_INFO_TABLE);
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
    public String[] getUser(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] user = new String[4];
        //set up a cursor pointer to start reading the table from the beginning
        Cursor cursor = db.query(USER_TABLE, new String[]{KEY_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, USER_PHONE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            Log.d(TAG, "id: " + cursor.getString(0)
                    + " name: " + cursor.getString(1)
                    + " email: " + cursor.getString(2));
            user[0] = cursor.getString(1); //name
            user[1] = cursor.getString(2); //email
            user[2] = cursor.getString(3); //password
            user[3] = cursor.getString(4); //phone
        } else {
            Log.d(TAG, "no user found with id " + id);
        }
        if (cursor != null){
            cursor.close();
        }
        return user;
    }

    public long getUserIdFromEmail(String userEmail) {
        SQLiteDatabase database = this.getReadableDatabase();
        long userId = -1; //ensure return -1 if does not exist

        Cursor cursor = database.query(
                "USERS",  // table name
                new String[]{"ID"},  // columns to return
                "EMAIL = ?",  // selection criteria
                new String[]{userEmail},  // selection arguments
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

    //tax table operations
    public long initializeTaxRecord(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER_ID, userId);
        values.put(INCOME, "0");
        values.put(FILING_STATUS, "");
        values.put(TAX_CREDITS, "0");
        values.put(ABOVE_LINE_DEDUCTIONS, "0");
        values.put(ITEMIZED_DEDUCTIONS, "0");

        long id = db.insert(TAX_INFO_TABLE, null, values);
        Log.d(TAG, "Initialized tax record: " + id + " for user: " + userId);
        return id;
    }

    public boolean taxRecordExists(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TAX_INFO_TABLE,
                new String[]{USER_ID},
                USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        //had a warning saying it needs to be inverted
        return !exists;
    }

    @SuppressLint("Range")
    public List<Map<String, String>> getTaxTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Map<String, String>> taxRecords = new ArrayList<>();

        Cursor taxCursor = db.query(TAX_INFO_TABLE, null, null, null, null, null, null, null);

        if (taxCursor != null && taxCursor.moveToFirst()) {
            do {
                Map<String, String> taxInfo = new HashMap<>();
                taxInfo.put("tax_info_id", taxCursor.getString(taxCursor.getColumnIndex(TAX_INFO_ID)));
                taxInfo.put("income", taxCursor.getString(taxCursor.getColumnIndex(INCOME)));
                taxInfo.put("filing_status", taxCursor.getString(taxCursor.getColumnIndex(FILING_STATUS)));
                taxInfo.put("user_id", taxCursor.getString(taxCursor.getColumnIndex(USER_ID)));
                taxInfo.put("tax_credits", taxCursor.getString(taxCursor.getColumnIndex(TAX_CREDITS)));
                taxInfo.put("above_line_deductions", taxCursor.getString(taxCursor.getColumnIndex(ABOVE_LINE_DEDUCTIONS)));
                taxInfo.put("itemized_deductions", taxCursor.getString(taxCursor.getColumnIndex(ITEMIZED_DEDUCTIONS)));
                taxInfo.put("dependents", taxCursor.getString(taxCursor.getColumnIndex(DEPENDENTS)));

                taxRecords.add(taxInfo);

                //log for debugging
                Log.d(TAG, "Tax Info: " +
                        "\nTax Info ID: " + taxInfo.get("tax_info_id") +
                        "\nIncome: " + taxInfo.get("income") +
                        "\nFiling Status: " + taxInfo.get("filing_status") +
                        "\nCustomer ID: " + taxInfo.get("user_id") +
                        "\nTax Credits: " + taxInfo.get("tax_credits") +
                        "\nAbove Line Deductions: " + taxInfo.get("above_line_deductions") +
                        "\nItemized Deductions: " + taxInfo.get("itemized_deductions") +
                        "\nDependents: " + taxInfo.get("dependents"));

            } while (taxCursor.moveToNext());
        }

        if (taxCursor != null) {
            taxCursor.close();
        }

        return taxRecords;
    }

    public boolean updateFilingStatus(long userId, String filingStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILING_STATUS, filingStatus);

        int rowsAffected = db.update(TAX_INFO_TABLE,
                values,
                USER_ID + "=?",
                new String[]{String.valueOf(userId)});

        Log.d(TAG, "Updated filing status for user " + userId + ": " + filingStatus);
        return rowsAffected > 0;
    }

    public boolean updateIncome(long userId, String income) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(INCOME, income);

        int rowsAffected = db.update(TAX_INFO_TABLE,
                values,
                USER_ID + "=?",
                new String[]{String.valueOf(userId)});

        Log.d(TAG, "Updated income for user " + userId + ": " + income);
        return rowsAffected > 0;
    }

    public boolean updateAboveLineDeductions(long userId, String deductions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ABOVE_LINE_DEDUCTIONS, deductions);

        int rowsAffected = db.update(TAX_INFO_TABLE,
                values,
                USER_ID + "=?",
                new String[]{String.valueOf(userId)});

        Log.d(TAG, "Updated above-line deductions for user " + userId + ": " + deductions);
        return rowsAffected > 0;
    }

    public boolean updateItemizedDeductions(long userId, String deductions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEMIZED_DEDUCTIONS, deductions);

        int rowsAffected = db.update(TAX_INFO_TABLE,
                values,
                USER_ID + "=?",
                new String[]{String.valueOf(userId)});

        Log.d(TAG, "Updated itemized deductions for user " + userId + ": " + deductions);
        return rowsAffected > 0;
    }

    public boolean updateTaxCredits(long userId, int credits) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TAX_CREDITS, String.valueOf(credits));

        int rowsAffected = db.update(TAX_INFO_TABLE,
                values,
                USER_ID + "=?",
                new String[]{String.valueOf(userId)});

        Log.d(TAG, "Updated tax credits for user " + userId + ": " + credits);
        return rowsAffected > 0;
    }


    //standardizing get methods to return a data structure instead of logging to tag
    @SuppressLint("Range")
    public Map<String, String> getUserTaxInfo(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, String> taxInfo = new HashMap<>();

        Cursor cursor = db.query(TAX_INFO_TABLE, null,
                USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            taxInfo.put("filing_status", cursor.getString(cursor.getColumnIndex(FILING_STATUS)));
            taxInfo.put("income", cursor.getString(cursor.getColumnIndex(INCOME)));
            taxInfo.put("above_line_deductions", cursor.getString(cursor.getColumnIndex(ABOVE_LINE_DEDUCTIONS)));
            taxInfo.put("itemized_deductions", cursor.getString(cursor.getColumnIndex(ITEMIZED_DEDUCTIONS)));
            taxInfo.put("tax_credits", cursor.getString(cursor.getColumnIndex(TAX_CREDITS)));
            cursor.close();
        }

        return taxInfo;
    }

    //get all users in the database
    //range suppress for get column index since it is possible for the db to not be initalized
    // when calling the method which would throw an error
    @SuppressLint("Range")
    public List<Map<String, String>> getUserTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Map<String, String>> users = new ArrayList<>();

        Cursor userCursor = db.query(USER_TABLE, null, null, null, null, null, null, null);

        if (userCursor != null && userCursor.moveToFirst()) {
            do {
                Map<String, String> user = new HashMap<>();
                user.put("id", userCursor.getString(userCursor.getColumnIndex(USER_ID)));
                user.put("name", userCursor.getString(userCursor.getColumnIndex(USER_NAME)));
                user.put("email", userCursor.getString(userCursor.getColumnIndex(USER_EMAIL)));
                user.put("phone", userCursor.getString(userCursor.getColumnIndex(USER_PHONE)));
                users.add(user);

                // Keep logging for debugging purposes
                Log.d(TAG, "User: " +
                        "\nID: " + user.get("id") +
                        "\nName: " + user.get("name") +
                        "\nEmail: " + user.get("email") +
                        "\nPhone: " + user.get("phone"));
            } while (userCursor.moveToNext());
        }

        if (userCursor != null) {
            userCursor.close();
        }

        return users;
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
                cursor.close();
                return true;
            }
        }

        return false;
    }

    /*
    * this will allow the primary key of dependents to find the next available int
    * for the dependents using the user id
     */
    public int getNextDependentId(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(dependent_id) FROM dependents WHERE user_id = ?", new String[]{String.valueOf(userId)});
        int nextId = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            nextId = cursor.getInt(0) + 1;
        }
        cursor.close();
        return nextId;
    }

    public int addDependent(String firstName, String lastName, String ssn, String dob, String relation, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            // get the next available dependent id for this user
            int dependentId = getNextDependentId(userId);

            // populate the values for insertion
            values.put(USER_ID, userId);
            values.put(DEPENDENT_ID, dependentId);
            values.put(DEPENDENT_FNAME, firstName);
            values.put(DEPENDENT_LNAME, lastName);
            values.put(DEPENDENT_SSN, ssn);
            values.put(DEPENDENT_DOB, dob);
            values.put(DEPENDENT_RELATION, relation);

            // insert the dependent record
            long result = db.insert(DEPENDENT_INFO_TABLE, null, values);

            if (result == -1) {
                Log.e(TAG, "Failed to add dependent for user: " + userId);
                return -1;
            }

            Log.d(TAG, "Successfully added dependent " + dependentId + " for user " + userId);
            return dependentId;

        } catch (Exception e) {
            Log.e(TAG, "Error adding dependent: " + e.getMessage());
            return -1;
        }
    }

    @SuppressLint("Range")
    public List<Map<String, String>> getUserDependents(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        List<Map<String, String>> dependents = new ArrayList<>();

        String[] columns = {
                DEPENDENT_ID,
                DEPENDENT_FNAME,
                DEPENDENT_LNAME,
                DEPENDENT_SSN,
                DEPENDENT_DOB,
                DEPENDENT_RELATION
        };

        String selection = USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(DEPENDENT_INFO_TABLE, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, String> dependent = new HashMap<>();
                dependent.put("id", cursor.getString(cursor.getColumnIndex(DEPENDENT_ID)));
                dependent.put("firstName", cursor.getString(cursor.getColumnIndex(DEPENDENT_FNAME)));
                dependent.put("lastName", cursor.getString(cursor.getColumnIndex(DEPENDENT_LNAME)));
                dependent.put("ssn", cursor.getString(cursor.getColumnIndex(DEPENDENT_SSN)));
                dependent.put("dob", cursor.getString(cursor.getColumnIndex(DEPENDENT_DOB)));
                dependent.put("relation", cursor.getString(cursor.getColumnIndex(DEPENDENT_RELATION)));
                dependents.add(dependent);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return dependents;
    }

}
