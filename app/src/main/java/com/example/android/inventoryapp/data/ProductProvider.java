package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;



/**
 * Created by Enzo on the 20/07/2017.
 */

public class ProductProvider extends ContentProvider {

    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,  String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + " =? ";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("NON VA CON UN URI SCONOSCIUTO " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.v("DareArticolo", "Cursor: " + cursor);

        return cursor;
    }

    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(" URI SCONOSCIUTO " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("INSERZIONE NON SUPPORTATA " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(ProductContract.ProductEntry.COL_NOME_ARTICOLO);
        if (name == null) {
            throw new IllegalArgumentException("L'ARTCIOLO HA BISOGNO DI UN NOME");
        }

        String price = values.getAsString(ProductContract.ProductEntry.COL_PREZZO_ARTICOLO);
        if (price == null ) {
            throw new IllegalArgumentException("L'ARTICOLO DEVE ESSERE PREZZATO");
        }

        String imageURi = values.getAsString(ProductContract.ProductEntry.COL_IMMAGINE_ARTICOLO);
        if (imageURi == null) {
            throw new IllegalArgumentException("L'ARTICOLO NECESSITA DI UNA FOTO");
        }

        if (values.containsKey(ProductContract.ProductEntry.COL_NOME_FORNITORE)) {
            String sName = values.getAsString(ProductContract.ProductEntry.COL_NOME_FORNITORE);
            if (sName == null) {
                throw new IllegalArgumentException("L'ARTICOLO HA BISOGNO DI UN FORNITORE");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COL_EMAIL_FORNITORE)) {
            String email = values.getAsString(ProductContract.ProductEntry.COL_EMAIL_FORNITORE);
            if (email == null) {
                throw new IllegalArgumentException("L'ARTICOLO NECESSITA DELL'EMAIL DEL FORNITORE");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, null, null);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("CANCELLAZIONE NON AUTORIZZATA " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:

                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("AGGIORNAMENTO NON AUTORIZZATO " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductContract.ProductEntry.COL_NOME_ARTICOLO)) {
            String name = values.getAsString(ProductContract.ProductEntry.COL_NOME_ARTICOLO);
            if (name == null) {
                throw new IllegalArgumentException("L'ARTCIOLO HA BISOGNO DI UN NOME");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COL_PREZZO_ARTICOLO)) {
            String price = values.getAsString(ProductContract.ProductEntry.COL_PREZZO_ARTICOLO);
            if (price == null) {
                throw new IllegalArgumentException("L'ARTICOLO DEVE ESSERE PREZZATO");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COL_NOME_FORNITORE)) {
            String sName = values.getAsString(ProductContract.ProductEntry.COL_NOME_FORNITORE);
            if (sName == null) {
                throw new IllegalArgumentException("L'ARTICOLO HA BISOGNO DI UN FORNITORE");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COL_EMAIL_FORNITORE)) {
            String email = values.getAsString(ProductContract.ProductEntry.COL_EMAIL_FORNITORE);
            if (email == null) {
                throw new IllegalArgumentException("L'ARTICOLO NECESSITA DELL'EMAIL DEL FORNITORE");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private static final int PRODUCTS = 200;

    private static final int PRODUCT_ID = 201;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }
}
