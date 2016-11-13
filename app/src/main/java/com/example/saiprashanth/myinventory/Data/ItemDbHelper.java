package com.example.saiprashanth.myinventory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.saiprashanth.myinventory.Data.ItemContract.ItemEntry;

/**
 * Created by saiprashanth on 09/11/16.
 */
public class ItemDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "store.db";

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME +
                " (" + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_PICTURE + " TEXT, " +
                ItemEntry.COLUMN_ITEM_QUANTITY +
                " INTEGER NOT NULL DEFAULT 0, " +
                ItemEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0" +
                ");";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //Currently no upgrade

    }
}
