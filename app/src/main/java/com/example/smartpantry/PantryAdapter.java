package com.example.smartpantry;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.VH> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private boolean showAlerts = true;
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<PantryItem> newItems, boolean showAlerts) {
        this.items = newItems;
        this.showAlerts = showAlerts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PantryItem item = items.get(position);
        h.tvName.setText(item.getName());
        h.tvQty.setText(String.format(Locale.US, "%s %s", RecipeMatcher.formatQty(item.getQuantity()), item.getUnit()));

        h.tvExpiry.setTextColor(Color.GRAY);
        if (item.getExpiry() == null || item.getExpiry().isEmpty()) {
            h.tvExpiry.setText(R.string.no_expiry_date);
        } else {
            h.tvExpiry.setText(String.format(Locale.US, "Expires: %s", item.getExpiry()));
            if (showAlerts) {
                long days = daysUntil(item.getExpiry());
                if (days < 0) {
                    h.tvExpiry.setText(String.format(Locale.US, "EXPIRED: %s", item.getExpiry()));
                    h.tvExpiry.setTextColor(Color.RED);
                } else if (days <= 3) {
                    h.tvExpiry.setText(String.format(Locale.US, "Expiring soon: %s", item.getExpiry()));
                    h.tvExpiry.setTextColor(Color.RED);
                }
            }
        }

        h.itemView.setOnClickListener(v -> listener.onEdit(item));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private long daysUntil(String date) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date parsedDate = f.parse(date);
            if (parsedDate != null) {
                long diff = parsedDate.getTime() - System.currentTimeMillis();
                return (long) Math.ceil(diff / 86400000.0);
            }
        } catch (ParseException ignored) {
        }
        return Long.MAX_VALUE;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvExpiry;
        ImageButton btnDelete;

        VH(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvItemName);
            tvQty = v.findViewById(R.id.tvItemQty);
            tvExpiry = v.findViewById(R.id.tvItemExpiry);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
