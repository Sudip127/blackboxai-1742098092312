package com.example.mealmate

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealmate.data.DatabaseHelper
import com.example.mealmate.data.GroceryItem
import com.example.mealmate.databinding.ActivityManageItemsBinding

class ManageItemsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageItemsBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var editingItem: GroceryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageItemsBinding.inflate(layoutInflater)
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
            title = if (editingItem == null) {
                getString(R.string.add_item)
            } else {
                getString(R.string.edit_item)
            }
        }
    }

    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }

    private fun handleIntent() {
        // Check if we're editing an existing item
        editingItem = intent.getParcelableExtra("item")
        editingItem?.let { item ->
            binding.apply {
                etItemName.setText(item.name)
                etQuantity.setText(item.quantity)
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                saveItem()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        binding.apply {
            if (etItemName.text.isNullOrBlank()) {
                tilItemName.error = getString(R.string.error_empty_fields)
                isValid = false
            } else {
                tilItemName.error = null
            }

            if (etQuantity.text.isNullOrBlank()) {
                tilQuantity.error = getString(R.string.error_empty_fields)
                isValid = false
            } else {
                tilQuantity.error = null
            }
        }
        return isValid
    }

    private fun saveItem() {
        val name = binding.etItemName.text.toString().trim()
        val quantity = binding.etQuantity.text.toString().trim()

        try {
            if (editingItem != null) {
                // Update existing item
                val result = databaseHelper.updateGroceryItem(
                    id = editingItem!!.id,
                    name = name,
                    quantity = quantity,
                    purchased = editingItem!!.purchased
                )
                if (result > 0) {
                    Toast.makeText(this, R.string.msg_item_updated, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, R.string.error_updating_item, Toast.LENGTH_SHORT).show()
                }
            } else {
                // Add new item
                val result = databaseHelper.addGroceryItem(name, quantity)
                if (result > -1) {
                    Toast.makeText(this, R.string.msg_item_added, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, R.string.error_adding_item, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                if (editingItem != null) R.string.error_updating_item else R.string.error_adding_item,
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
