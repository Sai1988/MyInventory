package com.example.saiprashanth.myinventory.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by saiprashanth on 08/11/16.
 */
public class ItemContract {
    //Define Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.saiprashanth.myinventory";

    ;
    //Define Base Content Uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Define Data
    public static final String PATH_ITEMS = "items";
    //Make Constructor private such that no one can accidentally create an object of this class
    private ItemContract() {
    }

    public static class ItemEntry implements BaseColumns {
        //Name of the table
        public static final String TABLE_NAME = "items";
        //Define the column names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_PICTURE = "picture";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_PRICE = "price";


        //Content URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;


    }
}
