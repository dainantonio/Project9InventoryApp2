package com.example.dainr.project9inventoryapp2.data;


import android.provider.BaseColumns;

/**
 * API Contract for the Cars app.
 */
public final class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {}

    /**
     * Inner class that defines constant values for the database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {

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
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = " supplier_name";


        /**
         * supplier phone number.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";


    }

}


