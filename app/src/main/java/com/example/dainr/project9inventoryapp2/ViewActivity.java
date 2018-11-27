package com.example.dainr.project9inventoryapp2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.dainr.project9inventoryapp2.data.InventoryContract;

public class ViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_product_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_product_supplier_phone_number);
        mQualitySpinner = findViewById(R.id.spinner_quality);

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
            getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
        }

        Log.d("message", "onCreate ViewActivity");
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @NonNull
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY

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
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
            final int qualityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUALITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            final int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            int supplierName = cursor.getInt(supplierNameColumnIndex);
            final int supplierPhoneNumber = cursor.getInt(supplierPhoneNumberColumnIndex);

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
            int mQuality = InventoryContract.ProductEntry.QUALITY_NEW;
            switch (mQuality) {
                case InventoryContract.ProductEntry.QUALITY_NEW:
                    mQualitySpinner.setSelection(1);
                    break;
                case InventoryContract.ProductEntry.QUALITY_USED:
                    mQualitySpinner.setSelection(2);
                    break;
                case InventoryContract.ProductEntry.QUALITY_REFURBISHED:
                    mQualitySpinner.setSelection(3);
                    break;
                default:
                    mQualitySpinner.setSelection(0);
                    break;
            }
            Button productDecreaseButton = findViewById(R.id.decrease_button);
            productDecreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseCount(quantityColumnIndex, quantity);
                }
            });

            Button productIncreaseButton = findViewById(R.id.increase_button);
            productIncreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseCount(qualityColumnIndex, quantity);
                }
            });

            Button productDeleteButton = findViewById(R.id.delete_button);
            productDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            Button phoneButton = findViewById(R.id.phone_button);
            phoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = String.valueOf(supplierPhoneNumber);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void decreaseCount(int productID, int productQuantity) {
        productQuantity = productQuantity - 1;
        if (productQuantity >= 0) {
            updateProduct(productQuantity);
            Toast.makeText(this, getString(R.string.quantity_change_msg), Toast.LENGTH_SHORT).show();

            Log.d("Log msg", " - productID " + productID + " - quantity " + productQuantity + " , decreaseCount has been called.");
        } else {
            Toast.makeText(this, getString(R.string.quantity_finish_msg), Toast.LENGTH_SHORT).show();
        }
    }

    public void increaseCount(int productID, int productQuantity) {
        productQuantity = productQuantity + 1;
        if (productQuantity >= 0) {
            updateProduct(productQuantity);
            Toast.makeText(this, getString(R.string.quantity_change_msg), Toast.LENGTH_SHORT).show();

            Log.d("Log msg", " - productID " + productID + " - quantity " + productQuantity + " , decreaseCount has been called.");
        }
    }

    private void updateProduct(int productQuantity) {
        Log.d("message", "updateProduct at ViewActivity");

        if (currentProductUri == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);

        if (currentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            // Otherwise this is an EXISTING product, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_product_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_product_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteProduct() {
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}