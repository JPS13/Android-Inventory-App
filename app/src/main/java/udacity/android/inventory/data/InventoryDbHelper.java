package udacity.android.inventory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import udacity.android.inventory.data.InventoryContract.InventoryEntry;
import udacity.android.inventory.model.Item;

public class InventoryDbHelper extends SQLiteOpenHelper {

    // Log tag for this class
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    /**
     * Constructor.
     *
     * @param context The calling context.
     */
    public InventoryDbHelper(Context context) {
        super(context, InventoryContract.DATABASE_NAME, null, InventoryContract.DATABASE_VERSION);
    }

    /**
     * This method is called when the class is instantiated. It creates the
     * tables if they do not already exist.
     *
     * @param db The current database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(InventoryEntry.CREATE_INVENTORY_TABLE);
    }

    /**
     * This method is called to ensure the current version of the database
     * is being utilized.
     *
     * @param db The database to be checked.
     * @param oldVersion The old version of the database.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Only executed if there exists a new version of the database
        if(oldVersion != newVersion) {
            String dropString = "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;
            db.execSQL(dropString);

            onCreate(db);
        }
    }

    /** CRUD Operations */

    /**
     * This method receives an Item object and writes it to the database.
     *
     * @param item The item to be written.
     * @return The inserted item with a set id.
     */
    public Item insert(Item item) {
        // Obtain a database object
        SQLiteDatabase db = this.getWritableDatabase();

        // A container for the values
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_DESCRIPTION, item.getDescription());
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, item.getPrice());
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, item.getQuantity());
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL, item.getSupplierEmail());
        values.put(InventoryContract.InventoryEntry.COLUMN_IMAGE, item.getImage());

        // Obtain the id for the item
        long id = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        item.setId(id);

        return item;
    }

    /**
     * This method queries the database for all of the data in the table.
     *
     * @return The Cursor object holding the result set.
     */
    public Cursor query() {
        // Return a Cursor containing all data from the table
        return getReadableDatabase().query(InventoryEntry.TABLE_NAME, null, null, null, null, null, null);
    }

    /**
     * This method updates the passed in Item.
     *
     * @param item The Item to be updated.
     */
    public void update(Item item) {
        // Obtain a database object
        SQLiteDatabase db = getWritableDatabase();

        // A container for the values
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_DESCRIPTION, item.getDescription());
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, item.getPrice());
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, item.getQuantity());
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL, item.getSupplierEmail());
        values.put(InventoryContract.InventoryEntry.COLUMN_IMAGE, item.getImage());

        // Determine the row to update
        String selection = InventoryEntry._ID + "= ?";
        String[] selectionArgs = new String[] { String.valueOf(item.getId())};

        db.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * This method deletes the passed in item.
     *
     * @param item The item to be deleted.
     */
    public void delete(Item item) {
        // Obtain a database object
        SQLiteDatabase db = getWritableDatabase();

        // Determine the row to update
        String selection = InventoryEntry._ID + "= ?";
        String[] selectionArgs = new String[] { String.valueOf(item.getId())};

        db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
    }

}
