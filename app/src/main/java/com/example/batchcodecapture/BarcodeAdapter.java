package com.example.batchcodecapture;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

public class BarcodeAdapter extends RecyclerView.Adapter<BarcodeAdapter.BarcodeViewHolder> {

    private Context context;
    private Cursor cursor;
    private DatabaseHelper db;

    public BarcodeAdapter(Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public BarcodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item, parent, false);
        return new BarcodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String barcode = cursor.getString(cursor.getColumnIndexOrThrow("barcode"));

            holder.barcodeText.setText(barcode);
            holder.editButton.setOnClickListener(l ->  {
                holder.editButton.setOnClickListener(v -> {
                    // Create an AlertDialog for editing
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Edit Barcode");

                    final EditText input = new EditText(context);
                    input.setText(barcode); // Pre-fill with the current barcode
                    builder.setView(input);

                    builder.setPositiveButton("OK", (dialog, which) -> {
                        String newBarcode = input.getText().toString();
                        db.UpdateEntry(id, newBarcode);
                        reloadCursor(); // Refresh the data
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                    builder.show();
                });
            });
        }
    }
    private void reloadCursor() {
        // Refresh the cursor and notify the adapter
        cursor = db.getAllBarcodes();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class BarcodeViewHolder extends RecyclerView.ViewHolder {
        TextView barcodeText;
        Button editButton, deleteButton;

        BarcodeViewHolder(View itemView) {
            super(itemView);
            barcodeText = itemView.findViewById(R.id.barcode_text);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

}
