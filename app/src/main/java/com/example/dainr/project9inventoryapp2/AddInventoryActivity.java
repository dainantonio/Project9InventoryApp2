package com.example.dainr.project9inventoryapp2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.dainr.project9inventoryapp2.data.InventoryContract;

/**
 * Displays list of products that were entered and stored in the app.
 */

public class AddInventoryActivity extends AppCompatActivity {

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

    }

    // after the user hits save in the Editor Activity and the app returns to the Catalog Activity, the info is updated and displayed
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the bookstore database.
     */
    private void displayDatabaseInfo() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_QUALITY,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        // Perform a query on the product table
        //The single read method uses a Cursor from the database to perform a query on the table to retrieve at least one column of data.
        // Also the method should close the Cursor after it's done reading from it.

        Cursor cursor = getContentResolver().query(
                InventoryContract.ProductEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows
        null)// the sort order
        TextView displayView = findViewById(R.id.text_view_inventory);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The product table contains <number of rows in Cursor> different products.
            // _id - name - quality- price-quantity-supplier name- supplier phone number
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.

            displayView.setText("The product table contains " + cursor.getCount() + " products.\n\n");
            displayView.append(InventoryContract.ProductEntry._ID + " - " +
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_QUALITY + " - " +
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE + " - " +
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " - " +
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int qualityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUALITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentQuality = cursor.getInt(qualityColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentSupplierName = cursor.getInt(supplierNameColumnIndex);
                int currentSupplierNumber = cursor.getInt(supplierNumberColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentQuality + " _ " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierNumber));
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

