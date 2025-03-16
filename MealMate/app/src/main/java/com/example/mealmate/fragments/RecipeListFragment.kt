package com.example.mealmate.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.CreateRecipeActivity
import com.example.mealmate.R
import com.example.mealmate.adapters.RecipeAdapter
import com.example.mealmate.data.DatabaseHelper
import com.example.mealmate.data.Recipe
import com.example.mealmate.databinding.FragmentRecipeListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RecipeListFragment : Fragment() {
    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupDatabase()
        setupRecyclerView()
        setupFab()
        loadRecipes()
    }

    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(requireContext())
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onItemClick = { recipe -> showRecipeDetails(recipe) },
            onEditClick = { recipe -> editRecipe(recipe) },
            onDeleteClick = { recipe -> showDeleteDialog(recipe) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeAdapter
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), CreateRecipeActivity::class.java))
        }
    }

    private fun loadRecipes() {
        try {
            val recipes = databaseHelper.getAllRecipes()
            if (recipes.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                recipeAdapter.submitList(recipes)
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.error_loading_recipes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRecipeDetails(recipe: Recipe) {
        // TODO: Implement recipe details view
        val intent = Intent(requireContext(), CreateRecipeActivity::class.java).apply {
            putExtra("recipe", recipe)
            putExtra("viewOnly", true)
        }
        startActivity(intent)
    }

    private fun editRecipe(recipe: Recipe) {
        val intent = Intent(requireContext(), CreateRecipeActivity::class.java).apply {
            putExtra("recipe", recipe)
        }
        startActivity(intent)
    }

    private fun showDeleteDialog(recipe: Recipe) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_recipe)
            .setMessage(R.string.msg_confirm_delete)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteRecipe(recipe)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteRecipe(recipe: Recipe) {
        try {
            databaseHelper.deleteRecipe(recipe.id)
            Toast.makeText(context, R.string.msg_recipe_deleted, Toast.LENGTH_SHORT).show()
            loadRecipes() // Refresh the list
        } catch (e: Exception) {
            Toast.makeText(context, R.string.error_deleting_item, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecipes() // Refresh list when returning to fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
