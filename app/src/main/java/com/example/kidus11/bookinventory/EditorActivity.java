package com.example.kidus11.bookinventory;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kidus11.bookinventory.data.BookContract.BookEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Int for the loader ID
     */
    private static final int LOADER_ID = 1;
    private static final int PICK_IMAGE_REQUEST = 1;

    /**
     * EditText field to enter the title of the book
     */
    private EditText mTitleEditText;

    /**
     * EditText field to enter the author of the book
     */
    private EditText mAuthorEditText;

    /**
     * EditText field to enter the pages
     */
    private EditText mPagesEditText;

    /**
     * EditText field to enter the edition
     */
    private EditText mEditionEditText;

    /**
     * EditText field to enter the price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the quanitity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the publisher
     */
    private EditText mPublisherEditText;

    private EditText mSupplierPhoneEditText;


    private Spinner mGenreSpinner;

    private Button mIncrease;

    private Button mDecrease;

    private Button mOrder;

    private Button mUpload;

    public int numforQ = -1;

    private ImageView mbookImageView;

    private int mBookGenre = 0;
    Uri passedUri;
    Uri imageUri;
    Uri imageforEditing;

    String Activitytitle;
    String imageUriString;
    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Activitytitle = getIntent().getStringExtra("title_from_fab");
        mOrder = (Button) findViewById(R.id.order_by_phone);
        // Assume thisActivity is the current activity
        //get the passed URI
        passedUri = getIntent().getData();


        if (passedUri == null) {
            setTitle(Activitytitle);
            mOrder.setVisibility(View.GONE); //We set the order by phone button to be gone, since we only have that in the editor activity

        } else {
            Activitytitle = "Edit Book";
            setTitle(Activitytitle);
            getLoaderManager().initLoader(LOADER_ID, null, this);

            mOrder.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    String PhoneNum = mSupplierPhoneEditText.getText().toString().trim();
                    callIntent.setData(Uri.parse("tel:" + PhoneNum));
                    startActivity(callIntent);
                }
            });
        }


        mTitleEditText = (EditText) findViewById(R.id.book_title_editor);
        mAuthorEditText = (EditText) findViewById(R.id.book_author_editor);
        mPagesEditText = (EditText) findViewById(R.id.page_editor);
        mEditionEditText = (EditText) findViewById(R.id.edition_editor);
        mPriceEditText = (EditText) findViewById(R.id.price_editor);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_editor);
        mPublisherEditText = (EditText) findViewById(R.id.publisher_editor);
        mIncrease = (Button) findViewById(R.id.editor_increase);
        mDecrease = (Button) findViewById(R.id.editor_decrease);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.supplier_phone_edit);
        mbookImageView = (ImageView) findViewById(R.id.bookImage);
        mUpload = (Button) findViewById(R.id.upload_image);
        mGenreSpinner = (Spinner) findViewById(R.id.genre_editor_spinner);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mTitleEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPagesEditText.setOnTouchListener(mTouchListener);
        mEditionEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPublisherEditText.setOnTouchListener(mTouchListener);
        mGenreSpinner.setOnTouchListener(mTouchListener);
        mIncrease.setOnTouchListener(mTouchListener);
        mDecrease.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mUpload.setOnTouchListener(mTouchListener);
        mbookImageView.setOnTouchListener(mTouchListener);


        //To Increase our quantity
        mIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quanititynumber = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quanititynumber)) {

                    Toast.makeText(EditorActivity.this, "Quantity field is Empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    numforQ = Integer.parseInt(quanititynumber);
                    mQuantityEditText.setText(String.valueOf(numforQ + 1));
                }

            }
        });

        //To Decrease our quantity
        mDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quanititynumber = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quanititynumber)) {
                    Toast.makeText(EditorActivity.this, "Quantity field is Empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    numforQ = Integer.parseInt(quanititynumber);
                    //To validate Qunitity is greater than 0
                    if ((numforQ - 1) >= 0) {
                        mQuantityEditText.setText(String.valueOf(numforQ - 1));
                    } else {
                        Toast.makeText(EditorActivity.this, "Quantity can not be less than 0", Toast.LENGTH_SHORT).show();
                        return;

                    }
                }
            }
        });

        setupSpinner();

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });


    }

    private void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    //onActivityResult is used to get a result from an activity, in our case, the photo app
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            if (resultData != null) {
                imageUri = resultData.getData();

                imageUriString = imageUri.toString();
                //Toast.makeText(this, imageUriString, Toast.LENGTH_SHORT).show();
                /**
                 *   You get the uri of the image the user picked, and then pass it to the function (getBitmapFromUri)
                 *   This will use the uri to get the image
                 */

                mbookImageView.setImageBitmap(getBitmapFromUri(imageUri));
                // mbookImageView.invalidate();
            }
        }

    }

    private Bitmap getBitmapFromUri(Uri imageUri) {
        if (imageUri == null || imageUri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mbookImageView.getWidth();
        int targetH = mbookImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(imageUri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e("Kidus", "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e("Kidus", "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }


    //Set up the drop down spinner
    private void setupSpinner() {
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenreSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenreSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selection = (String) parent.getItemAtPosition(position);
                        if (!TextUtils.isEmpty(selection)) {
                            if (selection.equals(getString(R.string.gen_art))) {
                                mBookGenre = BookEntry.CATEGORY_ART; // Male
                            } else if (selection.equals(getString(R.string.gen_biography))) {
                                mBookGenre = BookEntry.CATEGORY_BIOGRAPHY;
                            } else if (selection.equals(getString(R.string.gen_business))) {
                                mBookGenre = BookEntry.CATEGORY_BUSINESS;
                            } else if (selection.equals(getString(R.string.gen_comics))) {
                                mBookGenre = BookEntry.CATEGORY_COMICS;
                            } else if (selection.equals(getString(R.string.gen_cooking))) {
                                mBookGenre = BookEntry.CATEGORY_COOKING;
                            } else if (selection.equals(getString(R.string.gen_fiction))) {
                                mBookGenre = BookEntry.CATEGORY_FICTION;
                            } else if (selection.equals(getString(R.string.gen_health))) {
                                mBookGenre = BookEntry.CATEGORY_HEALTH;
                            } else if (selection.equals(getString(R.string.gen_history))) {
                                mBookGenre = BookEntry.CATEGORY_HISTORY;
                            } else if (selection.equals(getString(R.string.gen_hobby))) {
                                mBookGenre = BookEntry.CATEGORY_HOBBY;
                            } else if (selection.equals(getString(R.string.gen_horror))) {
                                mBookGenre = BookEntry.CATEGORY_HORROR;
                            } else if (selection.equals(getString(R.string.gen_kids))) {
                                mBookGenre = BookEntry.CATEGORY_KIDS; // Female
                            } else if (selection.equals(getString(R.string.gen_tech))) {
                                mBookGenre = BookEntry.CATEGORY_TECH;
                            } else {
                                mBookGenre = BookEntry.CATEGORY_DEFAULT; // Unknown
                            }
                        }
                    }

                    // Because AdapterView is an abstract class, onNothingSelected must be defined
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        mBookGenre = 0; // Unknown
                    }
                });
    }

    //Saving Book
    private void saveBook() {

        String Title = mTitleEditText.getText().toString().trim();
        if (TextUtils.isEmpty(Title)) {
            mTitleEditText.setError("What is the title of the book?");
            return;
        }

        String Author = mAuthorEditText.getText().toString().trim();
        if (TextUtils.isEmpty(Author)) {
            mAuthorEditText.setError("Who is the author of book?");
            return;
        }
        String Pages = mPagesEditText.getText().toString().trim();
        if (TextUtils.isEmpty(Pages)) {
            mPagesEditText.setError("How many pages does the book have?");
            return;
        }
        String Edition = mEditionEditText.getText().toString().trim();
        if (TextUtils.isEmpty(Edition)) {
            mEditionEditText.setError("What edition is the book?");
            return;
        }
        String Price = mPriceEditText.getText().toString().trim();
        if (TextUtils.isEmpty(Price)) {
            mPriceEditText.setError("How much is the price?");
            return;
        }
        String Quantity = mQuantityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(Quantity)) {
            mQuantityEditText.setError("How many books are in stock?");
            return;
        }
        String Publisher = mPublisherEditText.getText().toString().trim();
        if (TextUtils.isEmpty(Publisher)) {
            mPublisherEditText.setError("Who is the publisher");
            return;
        }
        String PhoneNum = mSupplierPhoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(PhoneNum)) {
            mSupplierPhoneEditText.setError("Publisher phone number?");
            return;
        }
        int pagesInt = Integer.parseInt(Pages);
        int editionInt = Integer.parseInt(Edition);
        long priceLong = Long.parseLong(Price);
        int quantityInt = Integer.parseInt(Quantity);
        long phoneNumLong = Long.parseLong(PhoneNum);
        numforQ = quantityInt;


        //Create the new content
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_NAME_TITLE, Title);
        values.put(BookEntry.COLUMN_NAME_AUTHOR, Author);
        values.put(BookEntry.COLUMN_NAME_PAGES, pagesInt);
        values.put(BookEntry.COLUMN_NAME_EDITION, editionInt);
        values.put(BookEntry.COLUMN_NAME_PRICE, priceLong);
        values.put(BookEntry.COLUMN_NAME_QUANTITY, Integer.toString(numforQ));
        values.put(BookEntry.COLUMN_NAME_PUBLISHER, Publisher);
        values.put(BookEntry.COLUMN_NAME_CATEGORY, mBookGenre);
        values.put(BookEntry.COLUMN_NAME_PHONE, phoneNumLong);
        values.put(BookEntry.COLUMN_PHOTO, imageUriString);


        if (imageUriString == null) {
            Toast.makeText(this, "Please pick the book cover", Toast.LENGTH_SHORT).show();
            return;
        }


        //If there is no passed URI, then we are adding new Book
        if (passedUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Error in Book insertion ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book saved successfully", Toast.LENGTH_SHORT).show();
            }
        }

        //String image = imageUri.toString();
        //values.put(BookEntry.COLUMN_NAME_IMAGE, image);


        //If passedUri is not null, we are going to be editing an already existing book
        if (passedUri != null) {
            int rowsAffected = getContentResolver().update(passedUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error in editing book infro", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book edited successfully", Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // if we are adding a new book, hide the delete menu
        if (passedUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //if the save button is clicked,
            case R.id.action_save_book:
                saveBook();
                return true;
            //if the delete button is clicked,
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirmaiton);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePet();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        if (passedUri != null) {

            //This will return number of the deleted rows
            int rowsDeleted = getContentResolver().delete(passedUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error in deletion of the book", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Deletion completed", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_NAME_TITLE,
                BookEntry.COLUMN_NAME_AUTHOR,
                BookEntry.COLUMN_NAME_CATEGORY,
                BookEntry.COLUMN_NAME_PUBLISHER,
                BookEntry.COLUMN_NAME_EDITION,
                BookEntry.COLUMN_NAME_PAGES,
                BookEntry.COLUMN_NAME_QUANTITY,
                BookEntry.COLUMN_NAME_PRICE,
                BookEntry.COLUMN_NAME_PHONE,
                BookEntry.COLUMN_PHOTO,

        };

        return new CursorLoader(
                EditorActivity.this,      // Parent activity context
                passedUri,                // URI for Table to query
                projection,             // Projection to return
                null,                   // No selection clause
                null,                    // No selection arguments
                null);                   // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            //We are using the following ints to identify the specific rows and column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_AUTHOR);
            int catagoryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_CATEGORY);
            int publisherColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_PUBLISHER);
            int editionColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_EDITION);
            int pagesColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_PAGES);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_PRICE);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_PHONE);
            int photoColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PHOTO);


            //  int pictureColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String bookTitleString = cursor.getString(titleColumnIndex);
            String bookAuthorString = cursor.getString(authorColumnIndex);
            int genre = cursor.getInt(catagoryColumnIndex);
            String bookPublisherString = cursor.getString(publisherColumnIndex);
            int bookEditionString = cursor.getInt(editionColumnIndex);
            int bookPagesString = cursor.getInt(pagesColumnIndex);
            int bookQuantityString = cursor.getInt(quantityColumnIndex);
            int bookPriceString = cursor.getInt(priceColumnIndex);
            int supplierPhone = cursor.getInt(phoneColumnIndex);
            String picture = cursor.getString(photoColumnIndex);

            if (picture != null) {
                imageforEditing = Uri.parse(picture);
                imageUriString = imageforEditing.toString();
            }


            // Update the views on the screen with the values from the database
            mTitleEditText.setText(bookTitleString);
            mAuthorEditText.setText(bookAuthorString);
            mPublisherEditText.setText(bookPublisherString);

            mPagesEditText.setText(Integer.toString(bookPagesString));
            mQuantityEditText.setText(Integer.toString(bookQuantityString));
            mPriceEditText.setText(Integer.toString(bookPriceString));
            mEditionEditText.setText(Integer.toString(bookEditionString));
            mSupplierPhoneEditText.setText(Integer.toString(supplierPhone));

            if (imageforEditing != null) {
                mbookImageView.setImageBitmap(getBitmapFromUri(imageforEditing));
            }

            //Now we set tge drop down menu
            switch (genre) {
                case BookEntry.CATEGORY_ART:
                    mGenreSpinner.setSelection(1);
                    break;
                case BookEntry.CATEGORY_BIOGRAPHY:
                    mGenreSpinner.setSelection(2);
                    break;

                case BookEntry.CATEGORY_BUSINESS:
                    mGenreSpinner.setSelection(3);
                    break;

                case BookEntry.CATEGORY_COMICS:
                    mGenreSpinner.setSelection(4);
                    break;

                case BookEntry.CATEGORY_COOKING:
                    mGenreSpinner.setSelection(5);
                    break;

                case BookEntry.CATEGORY_FICTION:
                    mGenreSpinner.setSelection(6);
                    break;

                case BookEntry.CATEGORY_HEALTH:
                    mGenreSpinner.setSelection(7);
                    break;

                case BookEntry.CATEGORY_HISTORY:
                    mGenreSpinner.setSelection(8);
                    break;

                case BookEntry.CATEGORY_HOBBY:
                    mGenreSpinner.setSelection(9);
                    break;

                case BookEntry.CATEGORY_HORROR:
                    mGenreSpinner.setSelection(10);
                    break;

                case BookEntry.CATEGORY_KIDS:
                    mGenreSpinner.setSelection(11);
                    break;

                case BookEntry.CATEGORY_TECH:
                    mGenreSpinner.setSelection(12);
                    break;

                default:
                    mGenreSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleEditText.setText("");
        mAuthorEditText.setText("");
        mPublisherEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mEditionEditText.setText("");
        mPagesEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mGenreSpinner.setSelection(0); // Select "Unknown"

    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}

