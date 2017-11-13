package com.example.kidus11.bookinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kidus11.bookinventory.data.BookSQLHelper;
import com.example.kidus11.bookinventory.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int PET_LOADER = 1;
    BookCursorAdapter mBookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFromFab = new Intent(MainActivity.this, EditorActivity.class);
                String title_from_fab = "New Book";
                intentFromFab.putExtra("title_from_fab",title_from_fab);
                startActivity(intentFromFab);
            }
        });
        // Find the ListView which will be populated with the pet data
        ListView listView = (ListView) findViewById(R.id.bookList);

        //Set up the cursorAdapter so it can use our ListView. There is no data yet, until the cursor finishes Loading,
        //So pass in null.
        mBookCursorAdapter = new BookCursorAdapter(this, null);
        listView.setAdapter(mBookCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                /**
                 * This will create the URI of the specific item that was clicked
                 */
                Uri currnetUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currnetUri);
                startActivity(intent);
            }
        });

        View empty = findViewById(R.id.emptyView);
        listView.setEmptyView(empty);

        //Start the loader
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_insert_sample_book) {
           // insertBook();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String [] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_NAME_TITLE,
                BookEntry.COLUMN_NAME_QUANTITY,
                BookEntry.COLUMN_NAME_AUTHOR,
                BookEntry.COLUMN_PHOTO,
        };

        return new CursorLoader(
                MainActivity.this,      // Parent activity context
                BookEntry.CONTENT_URI,  // URI for Table to query
                projection,             // Projection to return
                null,                   // No selection clause
                null,                    // No selection arguments
                null);                   // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mBookCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookCursorAdapter.swapCursor(null);
    }
}
