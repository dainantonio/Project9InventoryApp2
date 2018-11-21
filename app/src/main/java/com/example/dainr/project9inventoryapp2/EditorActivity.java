package com.example.dainr.project9inventoryapp2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.example.dainr.project9inventoryapp2.data.InventoryContract.ProductEntry;

/**
 * Allows user to add a new inventory to database or edit an existing one.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri currentProductUri;


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

    private Button decreaseQuantityButton;
    private Button callSupplierButton;
    private Button deleteProductButton;


    /**
     * Boolean flag that keeps track of whether the item has been edited (true) or not (false)
     */
    private boolean mProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductHasChanged boolean to true.
     */
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            Log.d("message", "onTouch");

            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Log.d("message", "onCreate");

        // Examine the intent that was used to launch this activity
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        currentProductUri = intent.getData();

        // If the intent does not contain a product content URI, then we know that we are creating a new product
        if (currentProductUri == null) {

            // This is a new product so change the app bar to say Ädd a Product
            setTitle(getString(R.string.add_product));
            invalidateOptionsMenu();
        } else {
            //otherwise this is an existing product, so change the app bar to say "Edit Product"
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, (android.app.LoaderManager.LoaderCallbacks<Object>) this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_product_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_product_supplier_phone_number);
        mQualitySpinner = findViewById(R.id.spinner_quality);
        setupSpinner();

        // Buttons
        Button addQuantityButton = findViewById(R.id.add_quantity);
        decreaseQuantityButton = findViewById(R.id.decrease_quantity);
        callSupplierButton = findViewById(R.id.contact_supplier);
        deleteProductButton = findViewById(R.id.delete_product_button);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

    }

    /**
     * Setup the dropdown spinner that allows the user to select the car quality.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter<CharSequence> qualitySpinnerAdapter = ArrayAdapter.createFromResource(this,
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
                    if (selection.equals(getString(R.string.quality_unknown))) {
                        mQuality = ProductEntry.QUALITY_UNKNOWN;
                    }
                }
                if (selection.equals(getString(R.string.quality_used))) {
                    mQuality = ProductEntry.QUALITY_USED;
                } else if (selection.equals(getString(R.string.quality_refurbished))) {
                    mQuality = ProductEntry.QUALITY_REFURBISHED;
                } else {
                    mQuality = ProductEntry.QUALITY_NEW;

                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mQuality = ProductEntry.QUALITY_UNKNOWN;
            }
        });
    }

    // Decrease quantity and validate that quantity does not go below zero
    public String decreaseQuantity() {
        int currentQuantity;
        String currentValue = mQuantityEditText.getText().toString();
        currentQuantity = Integer.parseInt(currentValue);
        if (currentQuantity == 0) {
            Toast.makeText(this, "Cannot have negative quantity", Toast.LENGTH_SHORT).show();
        } else {
            currentQuantity = currentQuantity - 1;
        }
        return String.valueOf(currentQuantity);
    }


    /**
     * Helper method to insert hardcoded data into the database. For debugging purposes only.
     */
    private void saveProduct() {
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

        // Logic to check if this is supposed to be a new item and check if all fields in editor are blank

        ContentValues values = null;
        if (currentProductUri == null) {
            if (TextUtils.isEmpty(nameString)) {
                //This is a new product so insert the name
                Toast.makeText(this, getString(R.string.product_name_required), Toast.LENGTH_SHORT).show();
                //returning the content URI for the new product
                return;
            }

            if (TextUtils.isEmpty(priceString)) {
                //This is a new product so insert the price
                Toast.makeText(this, getString(R.string.product_price_required), Toast.LENGTH_SHORT).show();
                //returning the content URI for the new product
                return;
            }
            if (TextUtils.isEmpty(quantityString)) {
                //This is a new product so insert the quantity
                Toast.makeText(this, getString(R.string.product_quantity_required), Toast.LENGTH_SHORT).show();
                //returning the content URI for the new product
                return;
            }
            if (mQuality == ProductEntry.QUALITY_UNKNOWN) {
                //This is a new product so insert the suppliers' name
                Toast.makeText(this, getString(R.string.quality_check_required), Toast.LENGTH_SHORT).show();
                //returning the content URI for the new product
                return;
            }
            if (TextUtils.isEmpty(supplierPhoneNumberString)) {
                //This is a new product so insert the supplier's phone number
                Toast.makeText(this, getString(R.string.supplier_phone_required), Toast.LENGTH_SHORT).show();
                //returning the content URI for the new product
                return;
            }

            // Create a ContentValues object where column names are the keys,
            // and product attributes from the editor are the values.
            values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(ProductEntry.COLUMN_PRODUCT_QUALITY, mQuality);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceInteger);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityInteger);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

            // Insert a new product into the provider, returning the content URI for the new pet.
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
                finish();
            }
        } else {

            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(currentProductUri, null, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_product_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_product_update_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
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

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity (returns to Catalog Activity)
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link AddInventoryActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
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
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
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


    private void deleteProduct() {
        // Only perform the delete if this is an existing item.
        if (currentProductUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (currentProductUri == null) {
            return null;
        }
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUALITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        // This loader will execute the Content Provider's query method ona background thread
        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int qualityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUALITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplierName = cursor.getInt(supplierNameColumnIndex);
            int supplierPhoneNumber = cursor.getInt(supplierPhoneNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(quantity);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhoneNumber);
            mQuantityEditText.setText(quantity);

            // Quality is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is NEW, 2 is USED, 3 is REFURBISHED.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (mQuality) {
                case ProductEntry.QUALITY_UNKNOWN:
                    mQualitySpinner.setSelection(1);
                    break;
                case ProductEntry.QUALITY_NEW:
                    mQualitySpinner.setSelection(2);
                    break;
                case ProductEntry.QUALITY_REFURBISHED:
                    mQualitySpinner.setSelection(3);
                    break;
                default:
                    mQualitySpinner.setSelection(0);
                    break;
            }
            //Format and set the supplier's phone number
            String formattedNumber = PhoneNumberUtils.formatNumber(String.valueOf(supplierPhoneNumber));
            mSupplierPhoneNumberEditText.setText(formattedNumber);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mQualitySpinner.setSelection(0);
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");

    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
