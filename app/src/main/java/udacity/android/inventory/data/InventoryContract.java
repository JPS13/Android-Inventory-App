package udacity.android.inventory.data;

import android.provider.BaseColumns;

/**
 * This class serves as a contract for interacting with the
 * inventory database.
 *
 * @author Joseph Stewart
 * @version 1.0
 */
public final class InventoryContract {

    /** The database name. */
    public final static String DATABASE_NAME = "inventory.db";

    /** The version of the database. */
    public final static int DATABASE_VERSION = 1;

    /**
     * Private constructor throws AssertionError to
     * prevent instantiation.
     */
    private InventoryContract() {
        throw new AssertionError();
    }

    /**
     * Inner class to represent the the inventory table to store
     * the items in stock.
     *
     * @author Joseph Stewart
     * @version 1.0
     */
    public static final class InventoryEntry implements BaseColumns {

        /** Table name for the inventory table */
        public final static String TABLE_NAME = "inventory";

        /** Column names. */

        /**
         * Unique ID number for each item.
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * The description of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_DESCRIPTION = "description";

        /**
         * The price of the item.
         *
         * Type: REAL
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * The quantity of the item.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * The supplier of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_EMAIL = "supplier";

        /**
         * The picture of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_IMAGE = "image";

        /**
         * String to create the table if it does not already exist.
         */
        public static final String CREATE_INVENTORY_TABLE = "CREATE TABLE IF NOT EXISTS " +
                InventoryContract.InventoryEntry.TABLE_NAME + "(" +
                InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryContract.InventoryEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                InventoryContract.InventoryEntry.COLUMN_PRICE + " REAL NOT NULL DEFAULT 0," +
                InventoryContract.InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL," +
                InventoryEntry.COLUMN_IMAGE + " TEXT" + ");";

    }

}
