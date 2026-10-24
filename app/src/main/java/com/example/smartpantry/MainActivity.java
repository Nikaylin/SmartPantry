package com.example.smartpantry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("My Pantry");

        db = new DatabaseHelper(this);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvPantry);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    // Runs every time the screen comes back into view, so the list refreshes
    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<PantryItem> items = db.getAllItems();
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        adapter.setItems(items, prefs.getBoolean("expiry_alerts", true));
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent i = new Intent(this, AddEditIngredientActivity.class);
        i.putExtra("item_id", item.getId());
        startActivity(i);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteItem(item.getId());
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_suggested) {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
