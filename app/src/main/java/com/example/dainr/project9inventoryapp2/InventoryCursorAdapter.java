package com.example.dainr.project9inventoryapp2;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.dainr.project9inventoryapp2.data.InventoryContract.ProductEntry;

import static android.os.Build.ID;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */


public class InventoryCursorAdapter extends CursorAdapter {
    //Global variables for book quantity amounts that are updated via button

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
    public void bindView(final View view, final Context context, final Cursor cursor) {

        Log.d("Position " + cursor.getPosition() + ":", " bindView() has been called.");

        // Find individual views that we want to modify the list item layout
        TextView nameTextView = view.findViewById(R.id.product_name_text_view);
        TextView priceTextView = view.findViewById(R.id.product_price_view_text);
        TextView quantityTextView = view.findViewById(R.id.product_quantity_text_view);
        Button soldButton = view.findViewById(R.id.item_sold_button);

        // Find the columns of product attributes that we are interested in

        final int columnIdIndex = cursor.getColumnIndex(ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the product attributes from the cursor for the current product

        final String product_ID = cursor.getString(columnIdIndex);
        String name = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);


        // Set an onClickListener for the Sold button to decrease quantity
        soldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddInventoryActivity Activity = (AddInventoryActivity) context;
                Activity.productsSold(Integer.valueOf(product_ID), Integer.valueOf(quantity));
            }
        });

        nameTextView.setText(ID + " ) " + name);
        priceTextView.setText(context.getString(R.string.product_price) + " : " + price + "  " + context.getString(R.string.product_price_currency));
        quantityTextView.setText(context.getString(R.string.product_quantity) + " : " + quantity);

        Button productEditButton = view.findViewById(R.id.edit_button);
        productEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, Long.parseLong(product_ID));
                intent.setData(currentProductUri);
                context.startActivity(intent);
            }
        });

    }

}