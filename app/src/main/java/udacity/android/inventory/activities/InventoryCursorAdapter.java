package udacity.android.inventory.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Locale;

import udacity.android.inventory.R;
import udacity.android.inventory.data.InventoryContract;
import udacity.android.inventory.model.Item;

/**
 * This is a custom CursorAdapter class to facilitate the display of
 * inventory items. This class makes use of the ViewHolder pattern to
 * recycle the views to minimize calls to the findViewById method.
 *
 * @author Joseph Stewart
 * @version 2.2
 *
 */
public class InventoryCursorAdapter extends CursorAdapter {

    // The context of the MainActivity
    private final MainActivity activity;

    // The ViewHolder to recycle the views
    private ViewHolder viewHolder;

    /**
     * This class holds references to the displayed views to minimize
     * calls to the findViewById method for efficiency.
     */
    private static class ViewHolder {
        // The text views for display
        TextView descriptionTextView;
        TextView priceTextView;
        TextView quantityTextView;
    }

    public InventoryCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        activity = context;
    }

    /**
     * This method assigns resource id's to the relevant fields in the ViewHolder
     * and sets the ViewHolder as a tag for the view to recycle.
     *
     * @param context The context.
     * @param cursor The cursor.
     * @param viewGroup The view group.
     * @return The view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        viewHolder = new ViewHolder();
        viewHolder.descriptionTextView = (TextView) view.findViewById(R.id.description_text_view);
        viewHolder.priceTextView = (TextView) view.findViewById(R.id.price_text_view);
        viewHolder.quantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);
        view.setTag(viewHolder);
        return view;
    }

    /**
     * This method takes action relevant to the current view in the adapter.
     * It sets the display values and action listener for the sale button and
     * view.
     *
     * @param view The current view.
     * @param context The context.
     * @param cursor The current cursor.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Get the viewHolder
        viewHolder = (ViewHolder) view.getTag();

        final Item currentItem = constructItem(cursor);

        // Set the display values
        // TextView for the description
        viewHolder.descriptionTextView.setText(currentItem.getDescription());

        // TextView for the price
        String priceLabel = activity.getString(R.string.item_price) + ": " + activity.getString(R.string.dollar_symbol);
        String formatPrice = String.format(Locale.US, "%.2f", currentItem.getPrice());
        viewHolder.priceTextView.setText(priceLabel + formatPrice);

        // TextView for the quantity
        String quantity = activity.getString(R.string.item_quantity) + ": "
                + String.valueOf(currentItem.getQuantity());
        viewHolder.quantityTextView.setText(quantity);

        // Set the action for clicking the sale button
        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.saleButtonClicked(currentItem);
            }
        });

        // Navigate to item's detail page when clicked
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDetailsActivity(currentItem);
            }
        });
    }

    /**
     * This method navigates to the DetailsActivity for the selected
     * Item object. Item implements Parcelable so it can be transported
     * via putExtra.
     *
     * @param currentItem The selected Item.
     */
    private void goToDetailsActivity(Item currentItem) {
        Intent intent = new Intent(activity, DetailsActivity.class);
        intent.putExtra("Item", currentItem);
        activity.startActivity(intent);
    }

    /**
     * This method constructs an Item object from a cursor.
     *
     * @param cursor The cursor of column data.
     * @return The constructed Item object.
     */
    private Item constructItem(Cursor cursor) {
        // Obtain values from the cursor
        long id = cursor.getLong(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        String description = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_DESCRIPTION));
        double price = cursor.getDouble(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY));
        String email = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL));
        String image = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_IMAGE));

        // Construct and return object from values
        return new Item(id, description, price, quantity, email, image);
    }

}
