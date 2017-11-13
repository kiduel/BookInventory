package com.example.kidus11.bookinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kidus11 on 10/23/17.
 */

public final class BookContract {

    private BookContract() {}
    public static final String CONTENT_AUTHORITY = "com.example.kidus11.bookinventory";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    public static class BookEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        public static final String TABLE_NAME = "Books";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_PUBLISHER = "publisher";
        public static final String COLUMN_NAME_EDITION = "edition";
        public static final String COLUMN_NAME_PAGES = "pages";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_PHOTO = "photo";

        public static final int CATEGORY_DEFAULT = 0;
        public static final int CATEGORY_ART = 1;
        public static final int CATEGORY_BIOGRAPHY = 2;
        public static final int CATEGORY_BUSINESS = 3;
        public static final int CATEGORY_COMICS = 4;
        public static final int CATEGORY_COOKING = 5;
        public static final int CATEGORY_FICTION = 6;
        public static final int CATEGORY_HEALTH = 7;
        public static final int CATEGORY_HISTORY = 8;
        public static final int CATEGORY_HOBBY = 9;
        public static final int CATEGORY_HORROR = 10;
        public static final int CATEGORY_KIDS = 11;
        public static final int CATEGORY_TECH = 12;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    }

}
