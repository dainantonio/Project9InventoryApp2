package com.example.dainr.project9inventoryapp2.data;


import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Products app.
 */
public final class InventoryContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.dainr.project9inventoryapp2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //This constant stores the path for each of the tables which will be appended to the base content URI.
    public static final String PATH_INVENTORY = "products";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {}

    /**
     * Inner class that defines constant values for the database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {

        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /** Name of database table for product */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="name";

        /**
         * product quality
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUALITY = "quality";

        /**
         * Possible values for the product quality.
         */
        public static final int QUALITY_NEW = 0;
        public static final int QUALITY_USED = 1;
        public static final int QUALITY_REFURBISHED = 2;

        /**
         * product price.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE= "price";


        /**
         * Product quantity.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY= "quantity";


        /**
         * Product supplier name
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";


        /**
         * supplier phone number.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}


