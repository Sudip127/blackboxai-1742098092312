package com.example.mealmate

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealmate.data.DatabaseHelper
import com.example.mealmate.data.Recipe
import com.example.mealmate.databinding.ActivityCreateRecipeBinding

class CreateRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRecipeBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var editingRecipe: Recipe? = null
    private var viewOnly: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDatabase()
        handleIntent()
        setupSaveButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = when {
                viewOnly -> getString(R.string.view_recipe)
                editingRecipe != null -> getString(R.string.edit_recipe)
                else -> getString(R.string.add_recipe)
            }
        }
    }

    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }

    private fun handleIntent() {
        editingRecipe = intent.getParcelableExtra("recipe")
        viewOnly = intent.getBooleanExtra("viewOnly", false)

        editingRecipe?.let { recipe ->
            binding.apply {
                etTitle.setText(recipe.title)
                etIngredients.setText(recipe.ingredients)
                etInstructions.setText(recipe.instructions)

                if (viewOnly) {
                    etTitle.isEnabled = false
                    etIngredients.isEnabled = false
                    etInstructions.isEnabled = false
                    btnSave.hide()
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                saveRecipe()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        binding.apply {
            if (etTitle.text.isNullOrBlank()) {
                tilTitle.error = getString(R.string.error_empty_fields)
                isValid = false
            } else {
                tilTitle.error = null
            }

            if (etIngredients.text.isNullOrBlank()) {
                tilIngredients.error = getString(R.string.error_empty_fields)
                isValid = false
            } else {
                tilIngredients.error = null
            }

            if (etInstructions.text.isNullOrBlank()) {
                tilInstructions.error = getString(R.string.error_empty_fields)
                isValid = false
            } else {
                tilInstructions.error = null
            }
        }
        return isValid
    }

    private fun saveRecipe() {
        val title = binding.etTitle.text.toString().trim()
        val ingredients = binding.etIngredients.text.toString().trim()
        val instructions = binding.etInstructions.text.toString().trim()

        try {
            if (editingRecipe != null) {
                // Update existing recipe
                val result = databaseHelper.updateRecipe(
                    id = editingRecipe!!.id,
                    title = title,
                    ingredients = ingredients,
                    instructions = instructions
                )
                if (result > 0) {
                    Toast.makeText(this, R.string.msg_recipe_updated, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, R.string.error_updating_recipe, Toast.LENGTH_SHORT).show()
                }
            } else {
                // Add new recipe
                val result = databaseHelper.addRecipe(title, ingredients, instructions)
                if (result > -1) {
                    Toast.makeText(this, R.string.msg_recipe_added, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, R.string.error_adding_recipe, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                if (editingRecipe != null) R.string.error_updating_recipe else R.string.error_adding_recipe,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
