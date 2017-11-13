package com.example.kidus11.bookinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kidus11.bookinventory.data.BookContract.BookEntry;

/**
 * Created by kidus11 on 10/30/17.
 */

public class BookSQLHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME= "books.db";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;

    String SQL_CREATE_AN_ENTRY = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
            + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BookEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, "
            + BookEntry.COLUMN_NAME_AUTHOR + " TEXT NOT NULL, "
            + BookEntry.COLUMN_NAME_CATEGORY + " INTEGER NOT NULL, "
            + BookEntry.COLUMN_NAME_PUBLISHER + " TEXT NOT NULL,"
            + BookEntry.COLUMN_NAME_EDITION +  " INTEGER NOT NULL DEFAULT 0, "
            + BookEntry.COLUMN_NAME_PAGES + " TEXT NOT NULL, "
            + BookEntry.COLUMN_NAME_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + BookEntry.COLUMN_NAME_PRICE + " INTEGER NOT NULL DEFAULT 0,"
            + BookEntry.COLUMN_PHOTO + " TEXT,"
            + BookEntry.COLUMN_NAME_PHONE + " INTEGER NOT NULL);";


    public BookSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_AN_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
