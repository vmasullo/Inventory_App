package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    Uri imageUri;
    private ImageView mImmagine;
    private EditText mNome;
    private EditText mPrezzo;
    private EditText mFornitore;
    private EditText mEmailFornitore;
    private TextView mQuantità;
    private Button mTastoPiù;
    private Button mTastoMeno;
    private TextView mTestoImmagine;
    private int mQuantità_;
    private Uri mArticoloUri;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mArticoloUri = intent.getData();

        mImmagine = (ImageView) findViewById(R.id.product_picture);
        mNome = (EditText) findViewById(R.id.edit_product_name);
        mPrezzo = (EditText) findViewById(R.id.edit_product_price);
        mFornitore = (EditText) findViewById(R.id.supplier_name);
        mEmailFornitore = (EditText) findViewById(R.id.supplier_email);
        mQuantità = (TextView) findViewById(R.id.edit_quantity_text_view);
        mTastoPiù = (Button) findViewById(R.id.button_plus);
        mTastoMeno = (Button) findViewById(R.id.button_minus);
        mTestoImmagine = (TextView) findViewById(R.id.add_photo_text);

        if (mArticoloUri == null) {
            setTitle(getString(R.string.Aggiungere_nome_articolo));
            mTestoImmagine.setText(getString(R.string.Caricare_immagine));
            mFornitore.setEnabled(true);
            mEmailFornitore.setEnabled(true);
            mTastoMeno.setVisibility(View.GONE);
            mTastoPiù.setVisibility(View.GONE);
            mQuantità.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.Cambiare_articolo));
            mTestoImmagine.setText(getString(R.string.Cambiare_foto));
            mTastoMeno.setVisibility(View.VISIBLE);
            mTastoPiù.setVisibility(View.VISIBLE);
            mQuantità.setVisibility(View.VISIBLE);
            mFornitore.setEnabled(false);
            mEmailFornitore.setEnabled(false);
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mNome.setOnTouchListener(mOnTouchListener);
        mPrezzo.setOnTouchListener(mOnTouchListener);
        mFornitore.setOnTouchListener(mOnTouchListener);
        mEmailFornitore.setOnTouchListener(mOnTouchListener);
        mTastoMeno.setOnTouchListener(mOnTouchListener);
        mTastoPiù.setOnTouchListener(mOnTouchListener);
        mImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySelector();
                mProductHasChanged = true;
            }
        });
    }

    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.Digitare));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.Scegliere_immagine)), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                mImmagine.setImageURI(imageUri);
                mImmagine.invalidate();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.Cambi_non_salvati);
        builder.setPositiveButton(R.string.Abbandonare, discardButtonClickListener);
        builder.setNegativeButton(R.string.Continua_a_scrivere, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mArticoloUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (saveProduct()) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.order_more:
                orderMore();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void orderMore() {
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + mEmailFornitore.getText().toString().trim()));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Nuovo Ordine");
        String message = "Nuovo ordine " + mNome.getText().toString().trim();
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }

    private boolean saveProduct() {

        boolean allOk = false;

        String nameString = mNome.getText().toString().trim();
        String priceString = mPrezzo.getText().toString().trim();
        String supplierNameString = mFornitore.getText().toString().trim();
        String supplierEmailString = mEmailFornitore.getText().toString().trim();
        String quantityString = mQuantità.getText().toString();
        if (mArticoloUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierEmailString) &&
                imageUri == null) {
            allOk = true;
            return allOk;
        }

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.Articolo_richiesto), Toast.LENGTH_SHORT).show();
            return allOk;
        }

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COL_NOME_ARTICOLO, nameString);
        values.put(ProductContract.ProductEntry.COL_QUANTITA_ARTICOLO, quantityString);



        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.Articolo_prezzo_richiesto), Toast.LENGTH_SHORT).show();
            return allOk;
        }

        values.put(ProductContract.ProductEntry.COL_PREZZO_ARTICOLO, priceString);

        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.Fornitore_richiesto), Toast.LENGTH_SHORT).show();
            return allOk;
        }

        values.put(ProductContract.ProductEntry.COL_NOME_FORNITORE, supplierNameString);

        if (TextUtils.isEmpty(supplierEmailString)) {
            Toast.makeText(this, getString(R.string.Email_fornitore), Toast.LENGTH_SHORT).show();
            return allOk;
        }

        values.put(ProductContract.ProductEntry.COL_EMAIL_FORNITORE, supplierEmailString);

        if (imageUri == null) {
            Toast.makeText(this, getString(R.string.Immagine_articolo), Toast.LENGTH_SHORT).show();
            return allOk;
        }

        values.put(ProductContract.ProductEntry.COL_IMMAGINE_ARTICOLO, imageUri.toString());

        if (mArticoloUri == null) {
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.Errore_salvataggio),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.Articolo_registrato),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mArticoloUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.Errore_salvataggio),
                        Toast.LENGTH_SHORT).show();
            } else {
                if (mProductHasChanged) {
                    Toast.makeText(this, getString(R.string.Dati_salvati),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        allOk = true;
        return allOk;
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.Cancellare_articolo);
        builder.setPositiveButton(R.string.Cancellare, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.Cancellare, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mArticoloUri != null) {
            int rowsDeleted = getContentResolver().delete(mArticoloUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.Errore_cancellazione_articolo),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.Articolo_cancellato_conm_successo),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COL_NOME_ARTICOLO,
                ProductContract.ProductEntry.COL_PREZZO_ARTICOLO,
                ProductContract.ProductEntry.COL_QUANTITA_ARTICOLO,
                ProductContract.ProductEntry.COL_IMMAGINE_ARTICOLO,
                ProductContract.ProductEntry.COL_NOME_FORNITORE,
                ProductContract.ProductEntry.COL_EMAIL_FORNITORE};

        return new CursorLoader(this,
                mArticoloUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_NOME_ARTICOLO);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PREZZO_ARTICOLO);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_QUANTITA_ARTICOLO);
            int pictureColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_IMMAGINE_ARTICOLO);
            int sNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_NOME_FORNITORE);
            int sEmailColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_EMAIL_FORNITORE);
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sEmail = cursor.getString(sEmailColumnIndex);
            mQuantità_ = cursor.getInt(quantityColumnIndex);
            String imageUriString = cursor.getString(pictureColumnIndex);
            mNome.setText(name);
            mPrezzo.setText(price);
            mFornitore.setText(sName);
            mEmailFornitore.setText(sEmail);
            mQuantità.setText(Integer.toString(mQuantità_));
            imageUri = Uri.parse(imageUriString);
            mImmagine.setImageURI(imageUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNome.setText("");
        mPrezzo.setText("");
        mFornitore.setText("");
        mEmailFornitore.setText("");
        mQuantità.setText("");
    }

    public void plusButtonClicked(View view) {
        mQuantità_++;
        displayQuantity();
    }

    public void minusButtonClicked(View view) {
        if (mQuantità_ == 0) {
            Toast.makeText(this, "Non è possibile diminure la quantità", Toast.LENGTH_SHORT).show();
        } else {
            mQuantità_--;
            displayQuantity();
        }
    }

    public void displayQuantity() {
        mQuantità.setText(String.valueOf(mQuantità_));
    }
}
