package com.example.mealmate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.R
import com.example.mealmate.adapters.GroceryAdapter
import com.example.mealmate.data.DatabaseHelper
import com.example.mealmate.data.GroceryItem
import com.example.mealmate.databinding.FragmentGroceryListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GroceryListFragment : Fragment() {
    private var _binding: FragmentGroceryListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var groceryAdapter: GroceryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroceryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupDatabase()
        setupRecyclerView()
        loadGroceryItems()
    }

    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(requireContext())
    }

    private fun setupRecyclerView() {
        groceryAdapter = GroceryAdapter(
            onItemClick = { item -> showEditDialog(item) },
            onCheckboxClick = { item, isChecked -> updateItemPurchaseStatus(item, isChecked) },
            onDeleteClick = { item -> showDeleteDialog(item) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groceryAdapter
        }
    }

    private fun loadGroceryItems() {
        try {
            val items = databaseHelper.getAllGroceryItems()
            if (items.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                groceryAdapter.submitList(items)
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.error_loading_items, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditDialog(item: GroceryItem) {
        // TODO: Implement edit dialog
    }

    private fun updateItemPurchaseStatus(item: GroceryItem, isPurchased: Boolean) {
        try {
            databaseHelper.updateGroceryItem(
                id = item.id,
                name = item.name,
                quantity = item.quantity,
                purchased = isPurchased
            )
            loadGroceryItems() // Refresh the list
        } catch (e: Exception) {
            Toast.makeText(context, R.string.error_updating_item, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteDialog(item: GroceryItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_item)
            .setMessage(R.string.msg_confirm_delete)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteItem(item)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteItem(item: GroceryItem) {
        try {
            databaseHelper.deleteGroceryItem(item.id)
            Toast.makeText(context, R.string.msg_item_deleted, Toast.LENGTH_SHORT).show()
            loadGroceryItems() // Refresh the list
        } catch (e: Exception) {
            Toast.makeText(context, R.string.error_deleting_item, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
