package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.mealmate.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNavigationDrawer()
        setupFab()
        displayUsername()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun setupNavigationDrawer() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            // The FAB action will change based on the current fragment
            when (supportFragmentManager.findFragmentById(R.id.contentFrame)) {
                is GroceryListFragment -> startActivity(Intent(this, ManageItemsActivity::class.java))
                is RecipeListFragment -> startActivity(Intent(this, CreateRecipeActivity::class.java))
                else -> startActivity(Intent(this, ManageItemsActivity::class.java))
            }
        }
    }

    private fun displayUsername() {
        val username = getSharedPreferences("MealMatePrefs", MODE_PRIVATE)
            .getString("username", "User") ?: "User"
        
        val headerView = binding.navigationView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.tvUsername).text = username
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_grocery_list -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.contentFrame, GroceryListFragment())
                    .commit()
                binding.toolbar.title = getString(R.string.nav_grocery_list)
            }
            R.id.nav_recipes -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.contentFrame, RecipeListFragment())
                    .commit()
                binding.toolbar.title = getString(R.string.nav_recipes)
            }
            R.id.nav_send_list -> {
                startActivity(Intent(this, DelegateListActivity::class.java))
            }
            R.id.nav_settings -> {
                // TODO: Implement settings
            }
            R.id.nav_logout -> {
                logout()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        // Clear user session
        getSharedPreferences("MealMatePrefs", MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Navigate to login screen
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
