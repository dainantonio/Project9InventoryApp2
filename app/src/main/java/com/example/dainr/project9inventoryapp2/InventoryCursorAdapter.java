package com.example.dainr.project9inventoryapp2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.dainr.project9inventoryapp2.data.InventoryContract;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */


public class InventoryCursorAdapter extends CursorAdapter {
    //Global variables for book quantity amounts that are updated via button
    private int product_ID;
    private int quantityValue;
    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify the list item layout
        TextView nameTextView = view.findViewById(R.id.product_name_text_view);
        TextView priceTextView = view.findViewById(R.id.product_price_text_view);
        TextView quantityTextView = view.findViewById(R.id.product_quantity_text_view);
        Button soldButton = view.findViewById(R.id.item_sold_button);

        // Find the columns of product attributes that we are interested in

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the product attributes from the cursor for the current product

        String name = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        // If the item price is empty string or null, then use some default text
        // that says "Unknown price", so the TextView isn't blank.
        if (TextUtils.isEmpty(price)) {
            price = context.getString(R.string.price_unknown);
        }

        // If the item quantity is empty string or null, then use some default text
        // that says "Unknown quantity", so the TextView isn't blank.
        if (TextUtils.isEmpty(quantity)) {
            quantity = context.getString(R.string.quantity_unknown);
        }


        //Update the TextViews with attributes for the current product
        nameTextView.setText(name);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);

        final int position = cursor.getPosition();

        // Set an onClickListener for the Sold button to decrease quantity
        soldButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set the cursor to the position of the button clicked
                cursor.moveToPosition(position);
                //Get the item ID of the current row
                product_ID = cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntry._ID));
                quantityValue = cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
                // If quantity is greater than 0, decrease the quantity by 1 and update, the db and swap the cursor
                if (quantityValue > 0) {
                    quantityValue = quantityValue - 1;
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityValue);

                    Uri updateUri = ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, product_ID);
                    context.getContentResolver().update(updateUri, values, null, null);
                    swapCursor(cursor);
                }
            }
        });
    }
}
