package com.example.mealmate

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.mealmate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MealMatePrefs", MODE_PRIVATE)

        // Check if user is already logged in and if it's first launch
        if (isLoggedIn()) {
            if (isFirstLaunch()) {
                navigateToOnboarding()
            } else {
                navigateToDashboard()
            }
            return
        }

        setupInputValidation()
        setupClickListeners()
    }

    private fun setupInputValidation() {
        // Clear errors when text changes
        binding.etUsername.addTextChangedListener {
            binding.tilUsername.error = null
        }
        binding.etPassword.addTextChangedListener {
            binding.tilPassword.error = null
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(username, password)) {
                loginUser(username)
            }
        }

        binding.btnSignup.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(username, password)) {
                signupUser(username)
            }
        }
    }

    private fun validateInputs(username: String, password: String): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            binding.tilUsername.error = getString(R.string.error_empty_fields)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_fields)
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_invalid_password)
            isValid = false
        }

        return isValid
    }

    private fun loginUser(username: String) {
        // For demo purposes, we'll use a simple authentication
        // In a real app, this should be replaced with proper authentication
        saveUserSession(username)
        Toast.makeText(this, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
        navigateToDashboard()
    }

    private fun signupUser(username: String) {
        // For demo purposes, we'll just save the username
        // In a real app, this should be replaced with proper user registration
        saveUserSession(username)
        setFirstLaunch(true)
        Toast.makeText(this, getString(R.string.success_signup), Toast.LENGTH_SHORT).show()
        navigateToOnboarding()
    }

    private fun saveUserSession(username: String) {
        sharedPreferences.edit().apply {
            putBoolean("isLoggedIn", true)
            putString("username", username)
            apply()
        }
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean("isFirstLaunch", true)
    }

    private fun setFirstLaunch(isFirst: Boolean) {
        sharedPreferences.edit().putBoolean("isFirstLaunch", isFirst).apply()
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun navigateToOnboarding() {
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }
}
