package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Enzo on the 20/07/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventoryapp.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME +
                "(" +
                ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProductContract.ProductEntry.COL_NOME_ARTICOLO  + " TEXT," +
                ProductContract.ProductEntry.COL_PREZZO_ARTICOLO  + " TEXT NOT NULL DEFAULT 0," +
                ProductContract.ProductEntry.COL_QUANTITA_ARTICOLO + " INTEGER DEFAULT 0," +
                ProductContract.ProductEntry.COL_IMMAGINE_ARTICOLO + " TEXT NOT NULL," +
                ProductContract.ProductEntry.COL_NOME_FORNITORE + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COL_EMAIL_FORNITORE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

