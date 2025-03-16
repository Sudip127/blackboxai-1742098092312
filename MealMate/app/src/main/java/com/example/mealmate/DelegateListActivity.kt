package com.example.mealmate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mealmate.data.DatabaseHelper
import com.example.mealmate.databinding.ActivityDelegateListBinding

class DelegateListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDelegateListBinding
    private lateinit var databaseHelper: DatabaseHelper
    private val SMS_PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDelegateListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDatabase()
        loadGroceryList()
        setupSendButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = getString(R.string.send_list)
        }
    }

    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }

    private fun loadGroceryList() {
        try {
            val items = databaseHelper.getAllGroceryItems()
            val groceryListText = buildGroceryListText(items)
            binding.tvGroceryList.text = groceryListText
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_loading_items, Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildGroceryListText(items: List<GroceryItem>): String {
        if (items.isEmpty()) return getString(R.string.msg_no_items)

        return buildString {
            appendLine(getString(R.string.grocery_list_header))
            appendLine()
            items.forEachIndexed { index, item ->
                appendLine("${index + 1}. ${item.name} - ${item.quantity}")
            }
        }
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            if (validatePhoneNumber(phoneNumber)) {
                checkSmsPermissionAndSend(phoneNumber)
            }
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        return if (phoneNumber.isEmpty()) {
            binding.tilPhoneNumber.error = getString(R.string.error_empty_phone)
            false
        } else {
            binding.tilPhoneNumber.error = null
            true
        }
    }

    private fun checkSmsPermissionAndSend(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                SMS_PERMISSION_REQUEST_CODE
            )
        } else {
            sendSms(phoneNumber)
        }
    }

    private fun sendSms(phoneNumber: String) {
        try {
            val smsManager = SmsManager.getDefault()
            val message = binding.tvGroceryList.text.toString()
            
            // Split message into parts if it's too long
            val messageParts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(
                phoneNumber,
                null,
                messageParts,
                null,
                null
            )
            
            Toast.makeText(this, R.string.msg_list_sent, Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_sending_list, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val phoneNumber = binding.etPhoneNumber.text.toString().trim()
                sendSms(phoneNumber)
            } else {
                Toast.makeText(this, R.string.error_permission_sms, Toast.LENGTH_SHORT).show()
            }
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
