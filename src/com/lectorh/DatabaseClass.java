package com.lectorh;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseClass extends SQLiteOpenHelper {

	// All Static variables
    // Database Version
    private static final int versionDB = 1;
 
    // Database Name
    private static final String nameDB = "Huellas";
 
    // Contacts table name
    private static final String nameTable = "Users";
 
    // Contacts Table Columns names
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String HUELLA = "huella";
 
    public DatabaseClass(Context context) {
        super(context, nameDB, null, versionDB);
    }
    
 // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + nameTable + "("
                + ID + " INTEGER PRIMARY KEY," + NAME + " TEXT,"
                + HUELLA + " BLOB" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + nameTable);
 
        // Create tables again
        onCreate(db);
       
    }
    
 
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(NAME, user.getName()); // Contact Name
        values.put(HUELLA, user.getHuella()); // Contact Phone Number
     
        // Inserting Row

        db.insert(nameTable, null, values);
        db.close(); // Closing database connection
    }
    
    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
     
       /* Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);*/
                
        String consult = "SELECT * FROM Users WHERE id ="+id;
        Cursor cursor = db.rawQuery(consult, null);
        
        if (cursor != null)
            cursor.moveToFirst();
     
        /*User user = new User(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getBlob(2));*/
        
        User user = new User(cursor.getInt(0),
                cursor.getString(1), cursor.getBlob(2));
        return user;
    }
    
    
    
    public String getCount(byte[] huella){
		
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String consult = "SELECT COUNT(*) FROM users WHERE name = '"+huella+"'";
        Cursor cursor = db.rawQuery(consult, null);
        
        if (cursor != null)
            cursor.moveToFirst();
     
        // return contact
        return cursor.getString(0);
    	
    }
    
    public List<User> getAllUsers() {
        List<User> contactList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + nameTable;
     
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
              //  user.setID(Integer.parseInt(cursor.getString(0)));
                user.setID(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setHuella(cursor.getBlob(2));
                // Adding contact to list
                contactList.add(user);
            } while (cursor.moveToNext());
        }
     
        // return contact list
        return contactList;
    }
    
}
