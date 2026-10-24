package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        Recipe r = new DatabaseHelper(this).getRecipe(recipeId);
        if (r == null) { finish(); return; }

        setTitle(r.getName());
        ((TextView) findViewById(R.id.tvDetailName)).setText(r.getName());

        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : r.getIngredients()) {
            sb.append("• ").append(ing.getName()).append(" - ")
                    .append(RecipeMatcher.formatQty(ing.getQuantity()))
                    .append(" ").append(ing.getUnit()).append("\n");
        }
        ((TextView) findViewById(R.id.tvDetailIngredients)).setText(sb.toString());
        ((TextView) findViewById(R.id.tvDetailSteps)).setText(r.getSteps());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
