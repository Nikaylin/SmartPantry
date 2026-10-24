package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Runs ONCE, when the database file is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "steps TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    // ============ PANTRY CRUD ============

    // CREATE
    public long addItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", item.getName());
        v.put("quantity", item.getQuantity());
        v.put("unit", item.getUnit());
        v.put("expiry", item.getExpiry());
        return db.insert("pantry", null, v);
    }

    // READ (all)
    public List<PantryItem> getAllItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("pantry", null, null, null, null, null, "name COLLATE NOCASE ASC");
        while (c.moveToNext()) {
            list.add(cursorToItem(c));
        }
        c.close();
        return list;
    }

    // READ (one)
    public PantryItem getItem(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("pantry", null, "id = ?", new String[]{String.valueOf(id)},
                null, null, null);
        PantryItem item = null;
        if (c.moveToFirst()) item = cursorToItem(c);
        c.close();
        return item;
    }

    // UPDATE
    public int updateItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", item.getName());
        v.put("quantity", item.getQuantity());
        v.put("unit", item.getUnit());
        v.put("expiry", item.getExpiry());
        return db.update("pantry", v, "id = ?", new String[]{String.valueOf(item.getId())});
    }

    // DELETE
    public int deleteItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("pantry", "id = ?", new String[]{String.valueOf(id)});
    }

    private PantryItem cursorToItem(Cursor c) {
        return new PantryItem(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry")));
    }

    // ============ RECIPES (read only) ============

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("recipes", null, null, null, null, null, "name ASC");
        while (c.moveToNext()) {
            Recipe r = new Recipe(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("steps")));
            loadIngredients(db, r);
            recipes.add(r);
        }
        c.close();
        return recipes;
    }

    public Recipe getRecipe(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("recipes", null, "id = ?", new String[]{String.valueOf(id)},
                null, null, null);
        Recipe r = null;
        if (c.moveToFirst()) {
            r = new Recipe(c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("steps")));
            loadIngredients(db, r);
        }
        c.close();
        return r;
    }

    private void loadIngredients(SQLiteDatabase db, Recipe r) {
        Cursor c = db.query("recipe_ingredients", null, "recipe_id = ?",
                new String[]{String.valueOf(r.getId())}, null, null, null);
        while (c.moveToNext()) {
            r.getIngredients().add(new RecipeIngredient(
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getDouble(c.getColumnIndexOrThrow("quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit"))));
        }
        c.close();
    }

    // ============ SEED DATA ============

    // Ingredient format: "name|quantity|unit"
    private void insertRecipe(SQLiteDatabase db, String name, String steps, String... ings) {
        ContentValues rv = new ContentValues();
        rv.put("name", name);
        rv.put("steps", steps);
        long recipeId = db.insert("recipes", null, rv);

        for (String s : ings) {
            String[] p = s.split("\\|");
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", recipeId);
            iv.put("name", p[0]);
            iv.put("quantity", Double.parseDouble(p[1]));
            iv.put("unit", p[2]);
            db.insert("recipe_ingredients", null, iv);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        insertRecipe(db, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Whisk eggs and scramble in melted butter.\n3. Serve on the toast.",
                "egg|2|pcs", "bread|2|pcs", "butter|10|g");
        insertRecipe(db, "Tomato Pasta",
                "1. Boil pasta.\n2. Fry garlic in oil, add chopped tomatoes and simmer.\n3. Toss with pasta.",
                "pasta|200|g", "tomato|3|pcs", "garlic|2|pcs", "olive oil|2|tbsp");
        insertRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Cook in butter, add cheese, fold and serve.",
                "egg|3|pcs", "cheese|50|g", "butter|10|g");
        insertRecipe(db, "Rice and Beans",
                "1. Cook rice.\n2. Fry onion, add beans and heat through.\n3. Serve together.",
                "rice|1|cup", "bean|1|cup", "onion|1|pcs");
        insertRecipe(db, "Mashed Potatoes",
                "1. Boil potatoes until soft.\n2. Mash with butter and warm milk.",
                "potato|4|pcs", "butter|30|g", "milk|100|ml");
        insertRecipe(db, "Pancakes",
                "1. Whisk flour, eggs and milk into a batter.\n2. Fry ladles of batter in butter until golden.",
                "flour|200|g", "egg|2|pcs", "milk|300|ml", "butter|20|g");
        insertRecipe(db, "Vegetable Stir-fry",
                "1. Slice all vegetables.\n2. Stir-fry on high heat.\n3. Add soy sauce and serve.",
                "carrot|2|pcs", "onion|1|pcs", "pepper|1|pcs", "soy sauce|2|tbsp");
        insertRecipe(db, "Cheese Toastie",
                "1. Butter the bread.\n2. Fill with cheese.\n3. Grill in a pan until golden.",
                "bread|2|pcs", "cheese|40|g", "butter|10|g");
        insertRecipe(db, "Tuna Salad",
                "1. Drain the tuna.\n2. Mix with mayonnaise.\n3. Serve on shredded lettuce.",
                "tuna|1|pcs", "mayonnaise|2|tbsp", "lettuce|1|pcs");
        insertRecipe(db, "Chicken and Rice",
                "1. Brown chicken with onion.\n2. Add rice and water.\n3. Cover and simmer until cooked.",
                "chicken|300|g", "rice|1|cup", "onion|1|pcs");
        insertRecipe(db, "French Toast",
                "1. Whisk eggs and milk.\n2. Dip the bread.\n3. Fry until golden on both sides.",
                "bread|4|pcs", "egg|2|pcs", "milk|100|ml");
        insertRecipe(db, "Garlic Bread",
                "1. Mix crushed garlic with butter.\n2. Spread on bread.\n3. Bake until crisp.",
                "bread|4|pcs", "garlic|2|pcs", "butter|40|g");
        insertRecipe(db, "Potato Soup",
                "1. Fry onion in butter.\n2. Add diced potatoes and milk.\n3. Simmer and blend.",
                "potato|3|pcs", "onion|1|pcs", "milk|250|ml", "butter|20|g");
        insertRecipe(db, "Egg Fried Rice",
                "1. Scramble the eggs and set aside.\n2. Stir-fry carrot and rice.\n3. Add eggs and soy sauce.",
                "rice|2|cup", "egg|2|pcs", "carrot|1|pcs", "soy sauce|2|tbsp");
        insertRecipe(db, "Banana Smoothie",
                "1. Peel bananas.\n2. Blend with milk and honey until smooth.",
                "banana|2|pcs", "milk|250|ml", "honey|1|tbsp");
        insertRecipe(db, "Macaroni Cheese",
                "1. Boil macaroni.\n2. Melt butter, add milk and cheese to make a sauce.\n3. Combine and serve.",
                "macaroni|200|g", "cheese|100|g", "milk|200|ml", "butter|20|g");
        insertRecipe(db, "Bean Chilli",
                "1. Fry onion and pepper.\n2. Add tomatoes and beans.\n3. Simmer for 20 minutes.",
                "bean|2|cup", "tomato|3|pcs", "onion|1|pcs", "pepper|1|pcs");
    }
}
