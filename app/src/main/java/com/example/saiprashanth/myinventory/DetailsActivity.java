package com.example.saiprashanth.myinventory;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saiprashanth.myinventory.Data.ItemContract.ItemEntry;
import com.example.saiprashanth.myinventory.Data.ItemDbHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.
        LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    private static final int EXISTING_ITEM_LOADER = 0;
    // Request code for Image Intent
    private static final int PICK_IMAGE_REQUEST = 0;
    // Permission to read External Storage
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 102;
    private boolean mItemHasChanged = false;
    /**
     * Content URI for the existing item (null if it's a new item)
     */
    private Uri mCurrentItemUri;
    /**
     * EditText field to enter the item's name
     */
    private EditText mNameEditText;
    /**
     * ImageView field to enter the item's picture
     */
    private ImageView mPictureImageView;
    /**
     * EditText field to enter the item's price
     */
    private EditText mPriceEditText;
    /**
     * TextView field to show the item's quantity
     */
    private TextView mQuantityText;
    /**
     * Button to Add picture
     */
    private Button mPictureButton;
    /**
     * Button to increase quantity
     */
    private Button mPlusButton;
    /**
     * Button to decrease quantity
     */
    private Button mMinusButton;
    /**
     * Button to Order item
     */
    private Button mOrderButton;
    //Member variable for Store Database
    private ItemDbHelper mDbHelper;
    // Uri from Image intent
    private Uri mUri;
    //String to store picture in database
    private String pictureUri;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get the URI from the intent
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();


        if (mCurrentItemUri == null) {
            // Item pet
            setTitle(R.string.editor_activity_title_new_item);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an item that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            //Modify Existing item
            setTitle(R.string.editor_activity_title_edit_item);
            //Call the loader to update the fields
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mPictureImageView = (ImageView) findViewById(R.id.item_image);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mQuantityText = (TextView) findViewById(R.id.edit_item_quantity);
        mPictureButton = (Button) findViewById(R.id.button_addimage);
        mMinusButton = (Button) findViewById(R.id.button_minus);
        mPlusButton = (Button) findViewById(R.id.button_plus);
        mOrderButton = (Button) findViewById(R.id.button_order);

        //Set the OnTouchLister on views and buttons
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mPictureButton.setOnTouchListener(mTouchListener);
        mMinusButton.setOnTouchListener(mTouchListener);
        mPlusButton.setOnTouchListener(mTouchListener);
        mOrderButton.setOnTouchListener(mTouchListener);

        // If it is a new item Quantity field will be blank, set the value to default 0
        if (mCurrentItemUri == null) {
            mQuantityText.setText("0");
        }
        // Minus button handler
        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the quantity value from the TextView
                int quantity = Integer.parseInt(mQuantityText.getText().toString().trim());
                //If quantity is greater than zero , decrement by 1
                if (quantity > 0) {
                    quantity--;
                }
                // convert the integer back to string
                String quantityText = Integer.toString(quantity);
                // Set the new quantity value in the layout
                mQuantityText.setText(quantityText);
            }
        });

        // Plus button handler
        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the quantity value from the TextView
                int quantity = Integer.parseInt(mQuantityText.getText().toString().trim());
                //If quantity is less than 100 , increment by 1. Currently setting upper limit to
                // 100
                if (quantity < 100) {
                    quantity++;
                }
                // convert the integer back to string
                String quantityText = Integer.toString(quantity);
                // Set the new quantity value in the layout
                mQuantityText.setText(quantityText);
            }
        });

        // Order button handler
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setData(Uri.parse("mailto:"));
                //ItemProvider itemType = new ItemProvider();
                email.setType("message/rfc822");
                email.putExtra(Intent.EXTRA_SUBJECT, "New Order of" + mNameEditText.getText().
                        toString());
                email.putExtra(Intent.EXTRA_TEXT, "No of items: " + mQuantityText.getText().
                        toString() + "\nPrice of each item:" + mPriceEditText.getText().toString());
                startActivity(email);
            }
        });

        // Add image button handler
        mPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent image = new Intent();
                image.setType("image/*");
                image.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(image, "Select Picture"),
                        PICK_IMAGE_REQUEST);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // save the file to Store database
                saveItem();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //Call delete confirmation method
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item details hasn't changed, continue with navigating up to parent
                // activity which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item to be added , hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    //Method to store the values in Store Database
    private void saveItem() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        String nameString = mNameEditText.getText().toString().trim();
        String imageString = pictureUri;
        String quantityString = mQuantityText.getText().toString();
        String priceString = mPriceEditText.getText().toString().trim();
        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(imageString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(priceString)) {
            return;
        }
        values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ItemEntry.COLUMN_ITEM_PICTURE, imageString);
        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(ItemEntry.COLUMN_ITEM_PRICE, price);

        int rowsAffected = 0;
        if (mCurrentItemUri == null) {
            // Call ContentResolver insert method
            Uri uri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
            // if uri == null display error message
            if (uri == null)
                Toast.makeText(this, R.string.editor_insert_item_failed,
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.editor_insert_item_successful,
                        Toast.LENGTH_SHORT).show();
        } else {
            // Call ContentResolver insert method
            rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            // if rowsAffected == 0 display error message
            if (rowsAffected == 0)
                Toast.makeText(this, R.string.editor_insert_item_failed,
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.editor_insert_item_successful,
                        Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the item table
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_PICTURE,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_PRICE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Find the columns of item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int pictureColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PICTURE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String picture = cursor.getString(pictureColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            if (!TextUtils.isEmpty(picture)) {
                Log.e(LOG_TAG, "OnLoadFinished: String picture" + picture);
                Uri uri = Uri.parse(picture);
                if ((ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    // user already provided permission
                    // perform function for what you want to achieve
                    mPictureImageView.setImageBitmap(getBitmapFromUri(uri));
                }

            }
            mQuantityText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Call ContentResolver delete method if pet exists
        if (mCurrentItemUri != null) {
            int rowsAffected = getContentResolver().delete(mCurrentItemUri, null, null);
            // if rowsAffected == 0 display error message
            if (rowsAffected == 0)
                Toast.makeText(this, R.string.editor_delete_item_failed,
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.editor_delete_item_successful,
                        Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mUri = data.getData();
                pictureUri = mUri.toString();
                Log.e(LOG_TAG, "onActivityResult: String pictureUri:" + pictureUri);
                mPictureImageView.setImageBitmap(getBitmapFromUri(mUri));
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mPictureImageView.getWidth();
        int targetH = mPictureImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            //   int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            // bmOptions.inSampleSize = scaleFactor;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }

                if (!canUseExternalStorage) {
                    Toast.makeText(this, R.string.permission, Toast.LENGTH_SHORT).show();
                } else {
                    // user now provided permission
                    // perform function for what you want to achieve
                }
            }
        }
    }
}

