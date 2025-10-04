package com.expensetracker.utils

/**
 * Constants used throughout the application
 */
object Constants {
    
    // Database
    const val DATABASE_NAME = "transaction_database"
    const val DATABASE_VERSION = 1
    
    // Shared Preferences
    const val PREFS_NAME = "expense_tracker_prefs"
    
    // SMS Processing
    const val AXIS_BANK_SENDER_PATTERN = "^[A-Z]{2}-AXISBK-S$"
    const val DEFAULT_ACCOUNT_NUMBER = "XX3248"
    const val DEFAULT_BANK_NAME = "Axis Bank"
    
    // Notification
    const val NOTIFICATION_CHANNEL_ID = "transaction_notifications"
    const val NOTIFICATION_CHANNEL_NAME = "Transaction Notifications"
    const val NOTIFICATION_ID_NEW_TRANSACTION = 1001
    
    // Intent Extras
    const val EXTRA_TRANSACTION_ID = "transaction_id"
    const val EXTRA_TRANSACTION = "transaction"
    const val EXTRA_FROM_NOTIFICATION = "from_notification"
    
    // Request Codes
    const val REQUEST_CODE_SMS_PERMISSION = 1001
    const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1002
    const val REQUEST_CODE_STORAGE_PERMISSION = 1003
    
    // Animation Durations (in milliseconds)
    const val ANIMATION_DURATION_SHORT = 200L
    const val ANIMATION_DURATION_MEDIUM = 300L
    const val ANIMATION_DURATION_LONG = 500L
    
    // UI Constants
    const val TRANSACTION_CARD_ELEVATION = 2f
    const val TRANSACTION_CARD_CORNER_RADIUS = 12f
    const val FAB_MARGIN = 16
    
    // Pagination
    const val PAGE_SIZE = 20
    const val INITIAL_LOAD_SIZE = 40
    
    // CSV Export
    const val CSV_FILE_NAME = "expense_tracker_transactions.csv"
    const val CSV_MIME_TYPE = "text/csv"
    
    // Date Formats
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
    const val DATE_FORMAT_SHORT = "dd MMM"
    const val DATE_FORMAT_MONTH_YEAR = "MMMM yyyy"
    const val TIME_FORMAT_12H = "h:mm a"
    const val TIME_FORMAT_24H = "HH:mm"
    const val TIME_FORMAT_12H_SECONDS = "h:mm:ss a"
    const val TIME_FORMAT_24H_SECONDS = "HH:mm:ss"
    
    // Validation
    const val MIN_AMOUNT = 0.01
    const val MAX_AMOUNT = 999999999.99
    const val MAX_NAME_LENGTH = 100
    const val MAX_TAG_LENGTH = 50
    
    // Quick Tags
    val QUICK_TAGS = listOf(
        "Groceries",
        "Transport", 
        "Food",
        "Fuel",
        "Shopping",
        "Bills",
        "Entertainment",
        "Healthcare",
        "Education",
        "Salary"
    )
    
    // Error Messages
    const val ERROR_AMOUNT_REQUIRED = "Amount is required"
    const val ERROR_AMOUNT_INVALID = "Please enter a valid amount"
    const val ERROR_NAME_REQUIRED = "Receiver/Sender name is required"
    const val ERROR_NAME_TOO_LONG = "Name is too long"
    const val ERROR_TAG_TOO_LONG = "Tag is too long"
    const val ERROR_LOADING_TRANSACTIONS = "Error loading transactions"
    const val ERROR_SAVING_TRANSACTION = "Error saving transaction"
    const val ERROR_UPDATING_TAG = "Error updating tag"
    const val ERROR_EXPORT_FAILED = "Export operation failed"
    const val ERROR_STORAGE_PERMISSION_DENIED = "Storage permission denied"
    
    // Success Messages
    const val SUCCESS_TRANSACTION_SAVED = "Transaction saved successfully"
    const val SUCCESS_TAG_UPDATED = "Tag updated successfully"
    const val SUCCESS_DATA_EXPORTED = "Data exported successfully"
}