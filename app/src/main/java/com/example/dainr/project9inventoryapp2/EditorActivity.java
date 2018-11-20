package com.example.dainr.project9inventoryapp2;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.dainr.project9inventoryapp2.data.InventoryContract.ProductEntry;
import com.example.therussells.project8inventoryapp1.R;

/**
 * Allows user to add a new inventory to database or edit an existing one.
 */

public class EditorActivity extends AppCompatActivity {
    /**
     * EditText field to enter the product name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the supplier name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the supplier phone number
     */
    private EditText mSupplierPhoneNumberEditText;

    /**
     * Spinner field to select the car's quality
     */
    private Spinner mQualitySpinner;

    private int mQuality = ProductEntry.QUALITY_NEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_product_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_product_supplier_phone_number);
        mQualitySpinner = findViewById(R.id.spinner_quality);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the car quality.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter qualitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_quality_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        qualitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mQualitySpinner.setAdapter(qualitySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mQualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.quality_used))) {
                        mQuality = ProductEntry.QUALITY_USED;
                    } else if (selection.equals(getString(R.string.quality_refurbished))) {
                        mQuality = ProductEntry.QUALITY_REFURBISHED;
                    } else {
                        mQuality = ProductEntry.QUALITY_NEW;

                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mQuality = ProductEntry.QUALITY_NEW;
            }
        });
    }

    /**
     * Helper method to insert hardcoded data into the database. For debugging purposes only.
     */
    private void insertProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();

        String priceString = mPriceEditText.getText().toString().trim();
        int priceInteger = Integer.parseInt(priceString);

        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantityInteger = Integer.parseInt(quantityString);

        String supplierNameString = mSupplierNameEditText.getText().toString().trim();

        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();
        int supplierPhoneNumber = Integer.parseInt(supplierPhoneNumberString);

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_QUALITY, mQuality);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceInteger);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityInteger);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        // Insert a new pet into the provider, returning the content URI for the new pet.
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        Log.d("message", "open EditorActivity");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                insertProduct();
                // Exit activity (returns to Catalog Activity)
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
