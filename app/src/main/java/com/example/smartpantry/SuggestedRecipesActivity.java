package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setTitle("Suggested Recipes");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseHelper db = new DatabaseHelper(this);
        List<Recipe> suggested =
                RecipeMatcher.getSuggested(db.getAllItems(), db.getAllRecipes());

        RecyclerView rv = findViewById(R.id.rvSuggested);
        TextView tvEmpty = findViewById(R.id.tvNoMatch);
        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(new RecipeAdapter(suggested, recipe -> {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra("recipe_id", recipe.getId());
            startActivity(i);
        }));

        tvEmpty.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
