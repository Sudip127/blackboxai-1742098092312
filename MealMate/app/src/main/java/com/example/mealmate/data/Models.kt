package com.example.mealmate.data

data class GroceryItem(
    val id: Int = 0,
    val name: String,
    val quantity: String,
    val purchased: Boolean = false
)

data class Recipe(
    val id: Int = 0,
    val title: String,
    val ingredients: String,
    val instructions: String
)
