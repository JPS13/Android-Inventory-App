package udacity.android.inventory.activities;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import udacity.android.inventory.R;
import udacity.android.inventory.data.InventoryDbHelper;
import udacity.android.inventory.model.Item;

/**
 * This is the MainActivity for the application. It loads the inventory
 * items from the database on a background thread and displays them
 * in a ListView.
 *
 * @author Joseph Stewart
 * @version 2.2
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Log tag to identify class in error logs. */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** The ListView to display the inventory. */
    private ListView listView;

    /** The TextView for when there is no data to display. */
    private TextView emptyStateTextView;

    /** Helper class for database interaction. */
    private InventoryDbHelper dbHelper;

    /** The cursor adapter */
    private InventoryCursorAdapter adapter;

    /**
     * This method is called when the activity is created.
     *
     * @param savedInstanceState The savedInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Provides access to the database
        dbHelper = new InventoryDbHelper(this);

        // Set the empty state view if there is no data to display
        listView = (ListView) findViewById(R.id.list);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(emptyStateTextView);

        // Initializes the loader manager.
        getLoaderManager().initLoader(0, null, this);

        // Declares and initializes add button and sets OnClickListener
        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });
    }

    /**
     * This method brings up an AlertDialog with a custom layout to
     * facilitate adding a new item to the database. The attributes
     * are validated and used to create a new Item object prior to
     * writing to the database.
     *
     */
    private void addNewItem() {
        // Create and set up a dialog
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.add_item_layout)
                .setTitle(R.string.add_product_title)
                .setPositiveButton(R.string.add_button_text, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        // Set an OnShowListener to keep the dialog open until proper values are set or cancel clicked
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // The state of inputs
                        boolean descriptionValid = false;
                        boolean priceValid = false;
                        boolean quantityValid = false;
                        boolean emailValid = false;

                        // Get the edit text fields
                        EditText descText = (EditText) alertDialog.findViewById(R.id.description_edit_text);
                        EditText priceText = (EditText) alertDialog.findViewById(R.id.price_edit_text);
                        EditText quantityText = (EditText) alertDialog.findViewById(R.id.quantity_edit_text);
                        EditText emailText = (EditText) alertDialog.findViewById(R.id.email_edit_text);
                        EditText imageText = (EditText) alertDialog.findViewById(R.id.image_edit_text);

                        // Obtain the entered text
                        String description = descText.getText().toString().trim();
                        String priceString = priceText.getText().toString().trim();
                        String quantityString = quantityText.getText().toString().trim();
                        String email = emailText.getText().toString().trim();
                        String image = imageText.getText().toString().trim();

                        // Verify the form is not blank
                        if(description.length() == 0 && priceString.length() == 0
                                && quantityString.length() == 0 && email.length() == 0
                                && image.length() == 0) {

                            // Notify user that blank form is unacceptable
                            Toast.makeText(getApplicationContext(), "Form Cannot Be Blank.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Price and Quantity can be zero if information about a new product is
                            // not determined yet. Values set to zero if edit texts left blank
                            if(priceString.equals("")) {
                                priceString = "0";
                            }

                            if(quantityString.equals("")) {
                                quantityString = "0";
                            }

                            // Numerical values
                            double price = 0;
                            int quantity = 0;

                            // Parse the values to numbers
                            try {
                                price = Double.parseDouble(priceString);
                                quantity = Integer.parseInt(quantityString);
                            } catch(NumberFormatException ex) {
                                // Log the error
                                Log.e(LOG_TAG, "Price and Quantity are incorrect.", ex);

                                // Notify user that quantity and price must be numbers
                                Toast.makeText(getApplicationContext(), "Price and Quantity must be numbers", Toast.LENGTH_SHORT).show();
                            }

                            if(price < 0) {
                                // Notify user that quantity and price must be >= 0
                                Toast.makeText(getApplicationContext(), "Price and Quantity must be >= 0", Toast.LENGTH_SHORT).show();
                            } else {
                                priceValid = true;
                            }

                            if(quantity < 0) {
                                // Notify user that quantity and price must be >= 0
                                Toast.makeText(getApplicationContext(), "Price and Quantity must be >= 0", Toast.LENGTH_SHORT).show();
                            } else {
                                quantityValid = true;
                            }

                            // User must enter an item description
                            if(description.equals("")) {
                                // Notify user that description and email are required
                                Toast.makeText(getApplicationContext(), "Description and Email are required.", Toast.LENGTH_SHORT).show();
                            } else {
                                descriptionValid = true;
                            }

                            // User must enter a supplier email
                            if(email.equals("")) {
                                // Notify user that description and email are required
                                Toast.makeText(getApplicationContext(), "Description and Email are required.", Toast.LENGTH_SHORT).show();
                            } else {
                                emailValid = true;
                            }

                            if(descriptionValid && priceValid && quantityValid && emailValid) {
                                // Create an Item object
                                Item item = new Item(description, price, quantity, email, image);

                                // Add item to database and refresh view
                                dbHelper.insert(item);
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent);

                                //Dismiss once inputs are validated
                                alertDialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

        // Show the dialog
        alertDialog.show();
    }

    /**
     * This method creates and returns a CursorLoader when the
     * LoaderManager is initialized.
     *
     * @param id The loader id.
     * @param args The bundle.
     * @return The loader.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, dbHelper);
    }

    /**
     * This method is called after the data is loaded. It hides
     * the progressbar and sets the adapter.
     *
     * @param loader The loader.
     * @param cursor The cursor is passed to the adapter.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Hide the loading spinner when the data is finished loading
        findViewById(R.id.loading_spinner).setVisibility(View.GONE);

        // Set the adapter
        adapter = new InventoryCursorAdapter(this, cursor);
        listView.setAdapter(adapter);

        emptyStateTextView.setText(R.string.no_content);
    }

    /**
     * This method clears the loader when it is reset.
     *
     * @param loader The loader.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }

    /**
     * This method is called from the InventoryCursorAdapter class
     * from the sale button action listener. The current Item is
     * passed in and the quantity on hand is decremented if there
     * are > 0 of the item on hand. The display and database are
     * updated.
     *
     * @param item The selected item.
     */
    public void saleButtonClicked(Item item) {
        int currentQuantity = item.getQuantity();

        if(currentQuantity > 0) {
            // Update the object value
            item.setQuantity(item.getQuantity() - 1);

            // Update the database
            new InventoryDbHelper(this).update(item);
        }

        adapter.swapCursor(dbHelper.query());
    }
}
