package com.example.android.inventoryapp;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;


/**
 * Created by Enzo on the 20/07/2017.
 */

public class ProductCursorAdapter extends CursorRecyclerAdapter<ProductCursorAdapter.ViewHolder> {

    private CatalogActivity activity = new CatalogActivity();

    public ProductCursorAdapter(CatalogActivity context, Cursor c) {
        super(context, c);
        this.activity = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView nome;
        protected TextView prezzo;
        protected TextView quantità;
        protected ImageView compra;
        protected ImageView immagine;

        public ViewHolder(View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.text_product_name);
            prezzo = (TextView) itemView.findViewById(R.id.text_product_price);
            quantità = (TextView) itemView.findViewById(R.id.text_product_quantity);
            compra = (ImageView) itemView.findViewById(R.id.buy);
            immagine = (ImageView) itemView.findViewById(R.id.product_image);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        final long id;
        final int mQuantità;

        id =cursor.getLong(cursor.getColumnIndex(ProductContract.ProductEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_NOME_ARTICOLO);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PREZZO_ARTICOLO);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_QUANTITA_ARTICOLO);
        int pictureColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COL_IMMAGINE_ARTICOLO);

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        String imageUriString = cursor.getString(pictureColumnIndex);
        Uri imageUri = Uri.parse(imageUriString);

        mQuantità = quantity;

        viewHolder.nome.setText(productName);
        viewHolder.prezzo.setText(productPrice);
        viewHolder.quantità.setText(String.valueOf(quantity));
        viewHolder.immagine.setImageURI(imageUri);
        viewHolder.immagine.invalidate();

        viewHolder.nome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onItemClick(id);
            }
        });

        viewHolder.compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mQuantità >0 ) {
                    activity.onBuyClick(id, mQuantità);
                } else {
                    Toast.makeText(activity, "Quantità non disponibile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
