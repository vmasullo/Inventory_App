package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Enzo on the 20/07/2017.
 */

public class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "prodotti";

    public static abstract class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "prodotti";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_NOME_ARTICOLO = "NOME";
        public static final String COL_PREZZO_ARTICOLO = "PREZZO";
        public static final String COL_IMMAGINE_ARTICOLO = "IMMAGINE";
        public static final String COL_QUANTITA_ARTICOLO = "QUANTITA";
        public static final String COL_NOME_FORNITORE = "FORNITORE";
        public static final String COL_EMAIL_FORNITORE = "EMAIL";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
    }
}

