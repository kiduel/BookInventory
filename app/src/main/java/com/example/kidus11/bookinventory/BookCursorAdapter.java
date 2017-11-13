package com.example.kidus11.bookinventory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kidus11.bookinventory.R;
import com.example.kidus11.bookinventory.data.BookContract.BookEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kidus11 on 11/1/17.
 */

public class BookCursorAdapter extends CursorAdapter {
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }
    public ImageView bookCover;
    //public InputStream input = null;


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item , parent , false);

    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView title = (TextView) view.findViewById(R.id.title_tv);
        TextView author = (TextView) view.findViewById(R.id.author_tv);
        TextView quantity = (TextView) view.findViewById(R.id.quantity_tv);
        ImageView cart = (ImageView) view.findViewById(R.id.cart);
        bookCover = (ImageView) view.findViewById(R.id.book_cover_tv);


        // Extract properties from cursor
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_TITLE);
        String currentTitle = cursor.getString(titleColumnIndex);

        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_AUTHOR);
        final String currentAuthor = cursor.getString(authorColumnIndex);

        int bookCoverColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PHOTO);
        String currentBookString = cursor.getString(bookCoverColumnIndex);
        Uri currentBookUri = Uri.parse(currentBookString);


        bookCover.setImageURI(currentBookUri);

        final int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_QUANTITY);
        String currentQuantity = cursor.getString(quantityColumnIndex);
        final int QuantityInt = Integer.valueOf(currentQuantity);

        final int productId = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));


        // Populate fields with extracted properties
        title.setText(currentTitle);
        author.setText(currentAuthor);
        quantity.setText(String.valueOf(currentQuantity));


        cart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (QuantityInt > 0) {
                    int newQuantity = QuantityInt - 1;
                    Uri quantitytUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI,productId );

                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_NAME_QUANTITY, newQuantity);
                    context.getContentResolver().update(quantitytUri, values, null, null);
                } else {
                    Toast.makeText(context, "Sold out!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private Bitmap getBitmapFromUri(Uri imageUri) {
//        if (imageUri == null || imageUri.toString().isEmpty())
//            return null;
//
//        // Get the dimensions of the View
//        int targetW = bookCover.getWidth();
//        int targetH = bookCover.getHeight();
//
////        InputStream input = null;
//        try {
//
//
//            // Get the dimensions of the bitmap
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            bmOptions.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(input, null, bmOptions);
//            input.close();
//
//            int photoW = bmOptions.outWidth;
//            int photoH = bmOptions.outHeight;
//
//            // Determine how much to scale down the image
//            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//
//            // Decode the image file into a Bitmap sized to fill the View
//            bmOptions.inJustDecodeBounds = false;
//            bmOptions.inSampleSize = scaleFactor;
//            bmOptions.inPurgeable = true;
//
//            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
//            input.close();
//            return bitmap;
//
//        } catch (FileNotFoundException fne) {
//            Log.e("Kidus", "Failed to load image.", fne);
//            return null;
//        } catch (Exception e) {
//            Log.e("Kidus", "Failed to load image.", e);
//            return null;
//        } finally {
//            try {
//                input.close();
//            } catch (IOException ioe) {
//
//            }
//        }
//    }

}

