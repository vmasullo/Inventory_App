package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.inventoryapp.data.ProductContract;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    ProductCursorAdapter mCursorAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    View emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        mLayoutManager = new LinearLayoutManager(CatalogActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        emptyView = findViewById(R.id.empty_view);

        mCursorAdapter = new ProductCursorAdapter(this, null);
        mRecyclerView.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    public void onItemClick (long id) {
        Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

        Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
        intent.setData(currentProductUri);

        startActivity(intent);
    }

    public void onBuyClick(long id, int quantità) {
        Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
        Log.v("CatalogActivity", "Uri: " + currentProductUri);
        quantità--;
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COL_QUANTITA_ARTICOLO, quantità);
        int rowsEffected = getContentResolver().update(currentProductUri, values, null, null);
    }

    private void insertProduct() {

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COL_NOME_ARTICOLO, getString(R.string.nome_finto));
        values.put(ProductContract.ProductEntry.COL_PREZZO_ARTICOLO, getString(R.string.Prezzo_finto));
        values.put(ProductContract.ProductEntry.COL_QUANTITA_ARTICOLO, 0);
        values.put(ProductContract.ProductEntry.COL_IMMAGINE_ARTICOLO, getString(R.string.Immagine_finta_Uri));
        values.put(ProductContract.ProductEntry.COL_NOME_FORNITORE, getString(R.string.Fornitore_finto));
        values.put(ProductContract.ProductEntry.COL_EMAIL_FORNITORE, getString(R.string.Email_fornitore_finto));

        Uri uri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
        Log.v("CatalogActivity", "Uri of new product: " + uri);

    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
        Log.v("Catalogo", rowsDeleted + " righe cancellate dall'articolo");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            case R.id.action_delete_all_products:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                ProductContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}

