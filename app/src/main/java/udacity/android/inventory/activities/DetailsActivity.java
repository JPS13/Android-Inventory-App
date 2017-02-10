package udacity.android.inventory.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;

import udacity.android.inventory.R;
import udacity.android.inventory.data.InventoryDbHelper;
import udacity.android.inventory.model.Item;

/**
 * This is the DetailsActivity which shows the details of
 * a selected Item object. The user can update the quantity
 * on hand, delete the item, or send an email to the supplier
 * to more of the item.
 *
 * @author Joseph Stewart
 * @version 1.2
 */
public class DetailsActivity extends AppCompatActivity {

    /** The log tag for this class. */
    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    /** Buttons */
    private Button deleteButton;
    private Button orderButton;
    private Button updateButton;
    private EditText editText;
    private RadioGroup radioGroup;

    /** Provides access to database operations */
    InventoryDbHelper dbHelper;

    /** The item currently being displayed */
    private Item currentItem;

    /** Error text if the input is incorrect. */
    private TextView quantityInputError;

    /**
     * This method is called when the Activity is created. It
     * initializes the data fields and sets the displays.
     *
     * @param savedInstanceState The savedInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize dbHelper
        dbHelper = new InventoryDbHelper(this);

        // Initialize buttons
        deleteButton = (Button) findViewById(R.id.delete_button);
        orderButton = (Button) findViewById(R.id.order_button);
        updateButton = (Button) findViewById(R.id.update_button);
        editText = (EditText) findViewById(R.id.quantity_edit_text_view);
        radioGroup = (RadioGroup) findViewById(R.id.modify_quantity_radiogroup);

        // Hide the keyboard when the activity starts
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        quantityInputError = (TextView) findViewById(R.id.error_text_view);
        quantityInputError.setVisibility(View.GONE);

        setOnClickListeners();

        currentItem = (Item) getIntent().getExtras().get("Item");

        // TextView for the description
        TextView descriptionTextView = (TextView) findViewById(R.id.description_text_view);
        descriptionTextView.setText(currentItem.getDescription());

        // TextView for the price
        TextView priceTextView = (TextView) findViewById(R.id.price_text_view);

        String price = getString(R.string.item_price) + ": "
                + getString(R.string.dollar_symbol)
                + String.valueOf(currentItem.getPrice());
        priceTextView.setText(price);

        // TextView for the quantity
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);

        String quantity = getString(R.string.item_quantity) + ": "
                + String.valueOf(currentItem.getQuantity());
        quantityTextView.setText(quantity);

        // TextView for the supplier email
        TextView emailTextView = (TextView) findViewById(R.id.email_text_view);

        String email = getString(R.string.supplier_email) + ": "
                + currentItem.getSupplierEmail();
        emailTextView.setText(email);

        if(currentItem.getImage() != null) {
            ImageView itemImage = (ImageView) findViewById(R.id.item_image_view);
            itemImage.setImageBitmap(getItemImage(Uri.parse(currentItem.getImage())));
        }
    }

    /**
     * This method searches for the item image based on the passed in uri
     * and returns it.
     *
     * @param uri The location of the image.
     * @return The image.
     */
    private Bitmap getItemImage(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }

    /**
     * This method sets the onClickListeners for the buttons.
     */
    private void setOnClickListeners() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prompt user to confirm delete
                confirmDeletion();
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send email to supplier to resupply product
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", currentItem.getSupplierEmail(), null));

                // Adds the subject line
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Product Order For " + currentItem.getDescription());

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredText = editText.getText().toString().trim();

                // The amount the current quantity will be adjusted
                int modifyQuantityAmount = 0;

                // Ensure entered value is actually an integer
                if (enteredText.length() > 0) {
                    try {
                        modifyQuantityAmount = Integer.parseInt(enteredText);
                    } catch (NumberFormatException ex) {
                        Log.e(LOG_TAG, "Could not parse entered text to an integer.", ex);

                        // Reset the text
                        editText.setText("");
                    }
                } else {
                    return;
                }

                // Get radio buttons to determine how to modify the quantity
                RadioButton soldButton = (RadioButton) radioGroup.getChildAt(0);

                // Get the current quantity of selected items in inventory
                int currentQuantity = currentItem.getQuantity();

                // Determine which radio button is selected and adjust current quantity
                if (soldButton.isChecked()) {
                    if (modifyQuantityAmount <= currentQuantity) {
                        currentQuantity -= modifyQuantityAmount;

                        if (quantityInputError.getVisibility() == View.VISIBLE) {
                            quantityInputError.setVisibility(View.GONE);
                        }
                    } else {
                        quantityInputError.setVisibility(View.VISIBLE);
                    }
                } else {
                    currentQuantity += modifyQuantityAmount;

                    if (quantityInputError.getVisibility() == View.VISIBLE) {
                        quantityInputError.setVisibility(View.GONE);
                    }
                }

                // Update the current item
                currentItem.setQuantity(currentQuantity);
                TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
                String quantity = getString(R.string.item_quantity) + ": "
                        + String.valueOf(currentItem.getQuantity());
                quantityTextView.setText(quantity);

                // Reset the views
                radioGroup.clearCheck();
                editText.setText("");

                // Update data in database
                dbHelper.update(currentItem);
            }
        });
    }

    /**
     * This method creates and shows an AlertDialog to prompt the
     * user to confirm deletion.
     */
    private void confirmDeletion() {
        // Create the alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.confirm_title);
        alertDialogBuilder.setMessage(R.string.prompt_text);

        // Set action for the delete button being pressed
        alertDialogBuilder.setPositiveButton(R.string.delete_button_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete item from database
                dbHelper.delete(currentItem);

                // Go back to the MainActivity
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set action for cancel button - just close dialog
        alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);

        // Show the dialog
        alertDialogBuilder.show();
    }

}
