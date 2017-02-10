package udacity.android.inventory.activities;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import udacity.android.inventory.data.InventoryDbHelper;

/**
 * This class is a custom Loader that queries the database
 * on a background thread to prevent performance issues on
 * the UI thread. It queries the database for all Items and
 * returns the result set as a Cursor.
 *
 * @author Joseph Stewart
 * @version 2.1
 */
public class CursorLoader extends AsyncTaskLoader<Cursor> {

    /** Database helper object. */
    InventoryDbHelper dbHelper;

    /**
     * Constructor.
     *
     * @param context The context.
     * @param dbHelper A database helper object.
     */
    public CursorLoader(Context context, InventoryDbHelper dbHelper) {
        super(context);
        this.dbHelper = dbHelper;
    }

    /**
     * This method forces the load when the loader starts.
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * The method loads the result set on a background
     * thread and returns a cursor.
     *
     * @return The result set from the database query.
     */
    @Override
    public Cursor loadInBackground() {
        if(dbHelper == null) {
            return null;
        }

        // Retrieve items from database
        return dbHelper.query();
    }
}
