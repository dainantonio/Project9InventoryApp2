package com.example.dainr.project9inventoryapp2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.dainr.project9inventoryapp2.data.InventoryContract;

/**
 * Displays list of products that were entered and stored in the app.
 */

public abstract class AddInventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private static final int INVENTORY_LOADER = 0;

    InventoryCursorAdapter adapter;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddInventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data
        ListView productListView = findViewById(R.id.list);

        //setup the below empty view when there are no products to display in the ListView.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        /*
        Set up an Adapter to create a list item for each row of item data in the Cursor.
        There is no item data yet (until the loader finishes) so pass in null for the Cursor.
        */
        adapter = new InventoryCursorAdapter(this, null);
        productListView.setAdapter(adapter);

        //set up onclick listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, final long id) {

                // use getIntent() and getData() to get the associated URI
                // set the title of the EditorActivity on which situation we have
                // if the EditorActivity was opened using the "ListView item, then we will
                // have uri of product, so change app bar to say "Edit Product
                // otherwise if tis is a new product , uri is null so change app bar to say Ädd a Product
                Intent intent = new Intent(AddInventoryActivity.this, ViewActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, id);

                //set the URI on the data field of the intent
                intent.setData(currentProductUri);

                //Launch the {@link EditorActivity} to display the data for the current product
                startActivity(intent);
            }
        });

        /* kick off the loader */
        getLoaderManager().initLoader(INVENTORY_LOADER, null, (android.app.LoaderManager.LoaderCallbacks<Object>) this);
    }

    /**
     * Helper method to insert hardcoded data into the database. For debugging purposes only.
     */
    private void insertProduct() {
        // Create a ContentValues object where column names are the keys,
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME, "Kindle Fire");
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUALITY, InventoryContract.ProductEntry.QUALITY_NEW);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE, 100);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, 20);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Amazon");
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, 1-800-123-4567);


        // Insert a new row for a product in the database, returning the ID of that new row.
        // The first argument for db.insert() is the product table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.

        Uri newUri = getContentResolver().insert(InventoryContract.ProductEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all items in the database.
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(InventoryContract.ProductEntry.CONTENT_URI, null, null);
        Toast.makeText(this, rowsDeleted + " " + getString(R.string.deleted_all_products_message), Toast.LENGTH_SHORT).show();
        Log.v("AddInventoryActivity", rowsDeleted + " rows deleted from item database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,

        };

        // Perform a query on the product table
        //The single read method uses a Cursor from the database to perform a query on the table to retrieve at least one column of data.
        // Also the method should close the Cursor after it's done reading from it.

        return new CursorLoader(this,
                InventoryContract.ProductEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows

    }


}

