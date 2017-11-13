package com.example.kidus11.bookinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.kidus11.bookinventory.data.BookContract.BookEntry;

/**
 * Created by kidus11 on 11/1/17.
 */

public class BookProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static final int BOOKS = 1;
    private static final int BOOK_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //This matches the URI to its correct path
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    private BookSQLHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookSQLHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // selection and selections - "_id=?" , a number, example 3 is the answer
        // projection is the name of the columns that needs to be returned
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor c;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                c = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                //do Something else
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.

                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                c = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //set the notification Uri on the cursor.
        //so we know what content URI the Cursor was created for
        //if the data at this URI changes, we know when we will need to update the Cursor.
        c.setNotificationUri(getContext().getContentResolver(),uri);

        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertBook(Uri uri, ContentValues values) {
        String title = values.getAsString(BookEntry.COLUMN_NAME_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book requires a title");
        }

        Integer category = values.getAsInteger(BookEntry.COLUMN_NAME_CATEGORY);
        if (category == null) {
            throw new IllegalArgumentException("Book requires a category");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //     TODO: Insert a new pet into the pets database table with the given ContentValues
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //notify the listeners that the data has changed
        getContext().getContentResolver().notifyChange(uri,null);

        // Once we know the ID of the new row in the table,
        //  return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
// Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                getContext().getContentResolver().notifyChange(uri,null);
//                return
//                        updatePet(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
        /**
         * Update pets in the database with the given content values. Apply the changes to the rows
         * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
         * Return the number of rows that were successfully updated.
         */
        private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
            // check that the name value is not null.
            if (values.containsKey(BookEntry.COLUMN_NAME_TITLE)) {
                String title = values.getAsString(BookEntry.COLUMN_NAME_TITLE);
                if (title == null) {
                    throw new IllegalArgumentException("book requires a Title");
                }
            }

            // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
            // check that the gender value is valid.
            if (values.containsKey(BookEntry.COLUMN_NAME_CATEGORY)) {
                Integer category = values.getAsInteger(BookEntry.COLUMN_NAME_CATEGORY);
                if (category == null) {
                    throw new IllegalArgumentException("book requires valid genre");
                }
            }


            // If there are no values to update, then don't try to update the database
            if (values.size() == 0) {
                return 0;
            }

            // Get writeable database
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Perform the update on the database and get the number of rows affected
            int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows updated
            return rowsUpdated;
        }
    }
