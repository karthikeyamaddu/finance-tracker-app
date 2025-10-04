package com.expensetracker.ui.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.model.TimeFormat
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.utils.Constants
import com.expensetracker.utils.SettingsManager
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen
 * Manages app preferences, permissions, and data export functionality
 */
class SettingsViewModel(
    private val repository: TransactionRepository,
    private val settingsManager: SettingsManager,
    private val context: Context
) : ViewModel() {
    
    // Time format preference
    private val _timeFormat = MutableLiveData<TimeFormat>()
    val timeFormat: LiveData<TimeFormat> = _timeFormat
    
    // Notifications setting
    private val _notificationsEnabled = MutableLiveData<Boolean>()
    val notificationsEnabled: LiveData<Boolean> = _notificationsEnabled
    
    // Permission states
    private val _smsPermissionGranted = MutableLiveData<Boolean>()
    val smsPermissionGranted: LiveData<Boolean> = _smsPermissionGranted
    
    private val _notificationPermissionGranted = MutableLiveData<Boolean>()
    val notificationPermissionGranted: LiveData<Boolean> = _notificationPermissionGranted
    
    // Export state
    private val _exportSuccess = MutableLiveData<Boolean>()
    val exportSuccess: LiveData<Boolean> = _exportSuccess
    
    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    /**
     * Load current settings from preferences
     */
    fun loadSettings() {
        _timeFormat.value = settingsManager.getTimeFormat()
        _notificationsEnabled.value = settingsManager.areNotificationsEnabled()
    }
    
    /**
     * Toggle between 12h and 24h time format
     */
    fun toggleTimeFormat() {
        val currentFormat = _timeFormat.value ?: TimeFormat.TWENTY_FOUR_HOUR
        val newFormat = when (currentFormat) {
            TimeFormat.TWENTY_FOUR_HOUR -> TimeFormat.TWELVE_HOUR
            TimeFormat.TWELVE_HOUR -> TimeFormat.TWENTY_FOUR_HOUR
        }
        
        settingsManager.setTimeFormat(newFormat)
        _timeFormat.value = newFormat
    }
    
    /**
     * Set notifications enabled/disabled
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        settingsManager.setNotificationsEnabled(enabled)
        _notificationsEnabled.value = enabled
    }
    
    /**
     * Check current permission status
     */
    fun checkPermissions() {
        // Check SMS permission
        val smsGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
        _smsPermissionGranted.value = smsGranted
        
        // Check notification permission (for Android 13+)
        val notificationGranted = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notifications are allowed by default on older versions
        }
        _notificationPermissionGranted.value = notificationGranted
    }
    
    /**
     * Export all transactions to CSV format
     */
    fun exportTransactionsToCSV(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val transactions = repository.getAllTransactionsForExport()
                
                val csvBuilder = StringBuilder()
                
                // CSV Header
                csvBuilder.appendLine("Date,Time,Type,Amount,Receiver/Sender,Bank,Account,UPI Reference,Tag,Entry Method")
                
                // CSV Data
                transactions.forEach { transaction ->
                    csvBuilder.appendLine(
                        "${transaction.transactionDate}," +
                        "${transaction.transactionTime}," +
                        "${transaction.transactionType}," +
                        "${transaction.amount}," +
                        "\"${transaction.receiverSenderName}\"," +
                        "\"${transaction.bankName}\"," +
                        "${transaction.accountNumber}," +
                        "\"${transaction.upiReference ?: ""}\"," +
                        "\"${transaction.userTag ?: ""}\"," +
                        "${transaction.entryMethod}"
                    )
                }
                
                onComplete(csvBuilder.toString())
                
            } catch (e: Exception) {
                _errorMessage.value = Constants.ERROR_EXPORT_FAILED
            }
        }
    }
    
    /**
     * Set export success state
     */
    fun setExportSuccess() {
        _exportSuccess.value = true
    }
    
    /**
     * Clear export success state
     */
    fun clearExportSuccess() {
        _exportSuccess.value = false
    }
    
    /**
     * Set error message
     */
    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
    
    /**
     * Clear error message after it's been shown
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}