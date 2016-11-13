package com.example.saiprashanth.myinventory;

/**
 * Created by saiprashanth on 08/11/16.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saiprashanth.myinventory.Data.ItemContract.ItemEntry;

/**
 * {@link ItemCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class ItemCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItemCursorAdapter(Context context, Cursor c) {
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
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        TextView price = (TextView) view.findViewById(R.id.price);
        // Extract properties from cursor
        final String itemName = cursor.getString(cursor.getColumnIndexOrThrow
                (ItemEntry.COLUMN_ITEM_NAME));
        final int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow
                (ItemEntry.COLUMN_ITEM_QUANTITY));
        final int itemPrice = cursor.getInt(cursor.getColumnIndexOrThrow
                (ItemEntry.COLUMN_ITEM_PRICE));
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(ItemEntry._ID));

        name.setText(itemName);
        quantity.setText(Integer.toString(itemQuantity));
        price.setText(Integer.toString(itemPrice));

        Button sale = (Button) view.findViewById(R.id.sale);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View saleView) {
                if (itemQuantity > 0) {
                    ContentValues values = new ContentValues();
                    int quantity = itemQuantity;
                    quantity--;
                    values.put(ItemEntry.COLUMN_ITEM_NAME, itemName);
                    values.put(ItemEntry.COLUMN_ITEM_PRICE, itemPrice);
                    values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
                    Uri newUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
                    int rowUpdated = saleView.getContext().getContentResolver().
                            update(newUri, values, null, null);

                } else {
                    Toast.makeText(saleView.getContext(), R.string.no_sale,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}

