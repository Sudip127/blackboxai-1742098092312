package com.example.mealmate.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MealMateDB"
        private const val DATABASE_VERSION = 1

        // Grocery Items Table
        private const val TABLE_GROCERY_ITEMS = "grocery_items"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_QUANTITY = "quantity"
        private const val COLUMN_PURCHASED = "purchased"
        private const val COLUMN_CREATED_AT = "created_at"

        // Recipes Table
        private const val TABLE_RECIPES = "recipes"
        private const val COLUMN_RECIPE_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_INGREDIENTS = "ingredients"
        private const val COLUMN_INSTRUCTIONS = "instructions"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Grocery Items table
        val createGroceryTable = """
            CREATE TABLE $TABLE_GROCERY_ITEMS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_QUANTITY TEXT NOT NULL,
                $COLUMN_PURCHASED INTEGER DEFAULT 0,
                $COLUMN_CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        // Create Recipes table
        val createRecipesTable = """
            CREATE TABLE $TABLE_RECIPES (
                $COLUMN_RECIPE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_INGREDIENTS TEXT NOT NULL,
                $COLUMN_INSTRUCTIONS TEXT NOT NULL
            )
        """.trimIndent()

        try {
            db.execSQL(createGroceryTable)
            db.execSQL(createRecipesTable)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error creating tables: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GROCERY_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPES")
        // Create tables again
        onCreate(db)
    }

    // Grocery Items CRUD Operations
    fun addGroceryItem(name: String, quantity: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_QUANTITY, quantity)
        }
        return try {
            db.insert(TABLE_GROCERY_ITEMS, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error adding grocery item: ${e.message}")
            -1
        } finally {
            db.close()
        }
    }

    fun updateGroceryItem(id: Int, name: String, quantity: String, purchased: Boolean): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_QUANTITY, quantity)
            put(COLUMN_PURCHASED, if (purchased) 1 else 0)
        }
        return try {
            db.update(TABLE_GROCERY_ITEMS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error updating grocery item: ${e.message}")
            0
        } finally {
            db.close()
        }
    }

    fun deleteGroceryItem(id: Int): Int {
        val db = this.writableDatabase
        return try {
            db.delete(TABLE_GROCERY_ITEMS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting grocery item: ${e.message}")
            0
        } finally {
            db.close()
        }
    }

    fun getAllGroceryItems(): List<GroceryItem> {
        val items = mutableListOf<GroceryItem>()
        val selectQuery = "SELECT * FROM $TABLE_GROCERY_ITEMS ORDER BY $COLUMN_CREATED_AT DESC"
        val db = this.readableDatabase

        try {
            val cursor = db.rawQuery(selectQuery, null)
            cursor?.use {
                while (cursor.moveToNext()) {
                    val item = GroceryItem(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        quantity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                        purchased = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PURCHASED)) == 1
                    )
                    items.add(item)
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching grocery items: ${e.message}")
        } finally {
            db.close()
        }
        return items
    }

    // Recipe CRUD Operations
    fun addRecipe(title: String, ingredients: String, instructions: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_INGREDIENTS, ingredients)
            put(COLUMN_INSTRUCTIONS, instructions)
        }
        return try {
            db.insert(TABLE_RECIPES, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error adding recipe: ${e.message}")
            -1
        } finally {
            db.close()
        }
    }

    fun updateRecipe(id: Int, title: String, ingredients: String, instructions: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_INGREDIENTS, ingredients)
            put(COLUMN_INSTRUCTIONS, instructions)
        }
        return try {
            db.update(TABLE_RECIPES, values, "$COLUMN_RECIPE_ID = ?", arrayOf(id.toString()))
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error updating recipe: ${e.message}")
            0
        } finally {
            db.close()
        }
    }

    fun deleteRecipe(id: Int): Int {
        val db = this.writableDatabase
        return try {
            db.delete(TABLE_RECIPES, "$COLUMN_RECIPE_ID = ?", arrayOf(id.toString()))
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting recipe: ${e.message}")
            0
        } finally {
            db.close()
        }
    }

    fun getAllRecipes(): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val selectQuery = "SELECT * FROM $TABLE_RECIPES"
        val db = this.readableDatabase

        try {
            val cursor = db.rawQuery(selectQuery, null)
            cursor?.use {
                while (cursor.moveToNext()) {
                    val recipe = Recipe(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)),
                        title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS)),
                        instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS))
                    )
                    recipes.add(recipe)
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching recipes: ${e.message}")
        } finally {
            db.close()
        }
        return recipes
    }
}
