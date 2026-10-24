package com.example.smartpantry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    // "Tomatoes " -> "tomato", "EGGS" -> "egg"
    public static String normalise(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (s.endsWith("ies") && s.length() > 3) {
            s = s.substring(0, s.length() - 3) + "y";
        } else if (s.endsWith("oes") && s.length() > 3) {
            s = s.substring(0, s.length() - 2);
        } else if (s.endsWith("s") && !s.endsWith("ss") && s.length() > 2) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    // Groups units so grams are never compared with millilitres
    public static String unitFamily(String unit) {
        switch (unit.toLowerCase(Locale.ROOT)) {
            case "g": case "kg":
                return "mass";
            case "ml": case "l": case "tsp": case "tbsp": case "cup":
                return "volume";
            default:
                return "count";
        }
    }

    // Converts to the base unit: g, ml or pcs
    public static double toBase(double qty, String unit) {
        switch (unit.toLowerCase(Locale.ROOT)) {
            case "kg":
            case "l":
                return qty * 1000;
            case "tsp":
                return qty * 5;
            case "tbsp":
                return qty * 15;
            case "cup":
                return qty * 240;
            default:
                return qty;
        }
    }

    // Returns only the recipes that can be made right now
    public static List<Recipe> getSuggested(List<PantryItem> pantry, List<Recipe> allRecipes) {
        Map<String, Double> stock = new HashMap<>();
        for (PantryItem p : pantry) {
            String key = normalise(p.getName()) + "|" + unitFamily(p.getUnit());
            double amount = toBase(p.getQuantity(), p.getUnit());
            stock.merge(key, amount, Double::sum);
        }

        List<Recipe> result = new ArrayList<>();
        for (Recipe r : allRecipes) {
            if (canMake(r, stock)) result.add(r);
        }
        return result;
    }

    private static boolean canMake(Recipe r, Map<String, Double> stock) {
        for (RecipeIngredient ing : r.getIngredients()) {
            String key = normalise(ing.getName()) + "|" + unitFamily(ing.getUnit());
            Double have = stock.get(key);
            double need = toBase(ing.getQuantity(), ing.getUnit());
            if (have == null || have < need) {
                return false; // one missing ingredient rejects the whole recipe
            }
        }
        return true;
    }

    // 2.0 -> "2", 2.5 -> "2.5"
    public static String formatQty(double q) {
        return (q == (long) q) ? String.valueOf((long) q) : String.valueOf(q);
    }
}
