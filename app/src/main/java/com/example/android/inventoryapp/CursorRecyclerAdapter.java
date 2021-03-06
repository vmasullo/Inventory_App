package com.example.android.inventoryapp;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Enzo on the 20/07/2017.
 */

public abstract class CursorRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Cursor cur;
    private CatalogActivity ac;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;

    public CursorRecyclerAdapter(CatalogActivity context, Cursor c) {
        this.ac = context;
        this.cur = c;
        this.mDataValid = cur != null;
        mRowIdColumn = mDataValid ? cur.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (cur != null) {
            cur.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCursor() {
        return cur;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && cur != null) {
            return cur.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int posizione) {
        if (mDataValid && cur != null && cur.moveToPosition(posizione)) {
            return cur.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }


    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH holder, int posizione) {
        if (!mDataValid) {
            throw new IllegalStateException("funziona solo quando ci passa il mouse sopra");
        }
        if (!cur.moveToPosition(posizione)) {
            throw new IllegalStateException("non puoi muovere il mouse in posizione " + posizione);
        }

        onBindViewHolder(holder, cur);



    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cur) {
            return null;
        }
        final Cursor oldCursor = cur;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        cur = newCursor;
        if (cur != null) {
            if (mDataSetObserver != null) {
                cur.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
        }
    }

}

