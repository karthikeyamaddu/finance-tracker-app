package com.expensetracker.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.expensetracker.ExpenseTrackerApplication
import com.expensetracker.R
import com.expensetracker.data.model.TimeFormat
import com.expensetracker.databinding.ActivitySettingsBinding
import com.expensetracker.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Settings screen for managing app preferences and permissions
 * Handles time format, notifications, permissions, and data export
 */
class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    
    // Permission launcher for storage access
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            exportDataToCsv()
        } else {
            showSnackbar(Constants.ERROR_STORAGE_PERMISSION_DENIED)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
        setupClickListeners()
        observeViewModel()
        
        // Load initial data
        viewModel.loadSettings()
        viewModel.checkPermissions()
    }
    
    private fun setupViewModel() {
        val application = application as ExpenseTrackerApplication
        val factory = SettingsViewModelFactory(application.repository, application.settingsManager, this)
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupClickListeners() {
        // Toolbar back button
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Time format toggle
        binding.btnTimeFormatToggle.setOnClickListener {
            viewModel.toggleTimeFormat()
        }
        
        // Notifications switch
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationsEnabled(isChecked)
        }
        
        // Export data
        binding.layoutExportData.setOnClickListener {
            requestStoragePermissionAndExport()
        }
    }
    
    private fun observeViewModel() {
        // Observe time format
        viewModel.timeFormat.observe(this) { timeFormat ->
            updateTimeFormatButton(timeFormat)
        }
        
        // Observe notifications setting
        viewModel.notificationsEnabled.observe(this) { enabled ->
            binding.switchNotifications.isChecked = enabled
        }
        
        // Observe SMS permission status
        viewModel.smsPermissionGranted.observe(this) { granted ->
            updatePermissionStatus(binding.tvSmsPermissionStatus, granted)
        }
        
        // Observe notification permission status
        viewModel.notificationPermissionGranted.observe(this) { granted ->
            updatePermissionStatus(binding.tvNotificationPermissionStatus, granted)
        }
        
        // Observe export success
        viewModel.exportSuccess.observe(this) { success ->
            if (success) {
                showSnackbar(Constants.SUCCESS_DATA_EXPORTED)
                viewModel.clearExportSuccess()
            }
        }
        
        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                showSnackbar(it)
                viewModel.clearErrorMessage()
            }
        }
    }
    
    private fun updateTimeFormatButton(timeFormat: TimeFormat) {
        binding.btnTimeFormatToggle.text = when (timeFormat) {
            TimeFormat.TWENTY_FOUR_HOUR -> getString(R.string.time_format_24h)
            TimeFormat.TWELVE_HOUR -> getString(R.string.time_format_12h)
        }
    }
    
    private fun updatePermissionStatus(textView: android.widget.TextView, granted: Boolean) {
        if (granted) {
            textView.text = getString(R.string.granted)
            textView.setTextColor(ContextCompat.getColor(this, R.color.credit_green))
        } else {
            textView.text = getString(R.string.denied)
            textView.setTextColor(ContextCompat.getColor(this, R.color.debit_red))
        }
    }
    
    private fun requestStoragePermissionAndExport() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                exportDataToCsv()
            }
            else -> {
                // For Android 10+ (API 29+), we don't need WRITE_EXTERNAL_STORAGE for app-specific directories
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    exportDataToCsv()
                } else {
                    storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }
    
    private fun exportDataToCsv() {
        viewModel.exportTransactionsToCSV { csvContent ->
            try {
                val fileName = "expense_tracker_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
                val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                
                FileWriter(file).use { writer ->
                    writer.write(csvContent)
                }
                
                // Share the file
                val uri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    file
                )
                
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Expense Tracker Data Export")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                startActivity(Intent.createChooser(shareIntent, "Share CSV File"))
                viewModel.setExportSuccess()
                
            } catch (e: Exception) {
                viewModel.setErrorMessage(Constants.ERROR_EXPORT_FAILED)
            }
        }
    }
    
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}