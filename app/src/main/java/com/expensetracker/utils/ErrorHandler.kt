package com.expensetracker.utils

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import com.expensetracker.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Centralized error handling utility
 * Provides user-friendly error messages and logging
 */
object ErrorHandler {
    
    private const val TAG = "ErrorHandler"
    
    /**
     * Handle and convert exceptions to user-friendly messages
     */
    fun handleError(context: Context, throwable: Throwable): String {
        Log.e(TAG, "Error occurred: ${throwable.message}", throwable)
        
        return when (throwable) {
            is SQLiteException -> {
                Log.e(TAG, "Database error: ${throwable.message}", throwable)
                context.getString(R.string.error_database)
            }
            is IOException -> {
                Log.e(TAG, "IO error: ${throwable.message}", throwable)
                context.getString(R.string.error_file_operation)
            }
            is SecurityException -> {
                Log.e(TAG, "Security error: ${throwable.message}", throwable)
                context.getString(R.string.error_permission_denied)
            }
            is OutOfMemoryError -> {
                Log.e(TAG, "Out of memory error", throwable)
                context.getString(R.string.error_out_of_memory)
            }
            is NumberFormatException -> {
                Log.e(TAG, "Number format error: ${throwable.message}", throwable)
                context.getString(R.string.error_invalid_number)
            }
            is IllegalArgumentException -> {
                Log.e(TAG, "Invalid argument: ${throwable.message}", throwable)
                context.getString(R.string.error_invalid_input)
            }
            is UnknownHostException, is SocketTimeoutException -> {
                Log.e(TAG, "Network error: ${throwable.message}", throwable)
                context.getString(R.string.error_network)
            }
            else -> {
                Log.e(TAG, "Unknown error: ${throwable.message}", throwable)
                context.getString(R.string.error_unknown)
            }
        }
    }
    
    /**
     * Handle SMS parsing errors specifically
     */
    fun handleSmsParsingError(context: Context, smsBody: String, error: Throwable): String {
        Log.e(TAG, "SMS parsing failed for: $smsBody", error)
        return context.getString(R.string.error_sms_parsing)
    }
    
    /**
     * Handle database operation errors
     */
    fun handleDatabaseError(context: Context, operation: String, error: Throwable): String {
        Log.e(TAG, "Database operation '$operation' failed", error)
        
        return when (error) {
            is SQLiteException -> {
                if (error.message?.contains("UNIQUE constraint failed") == true) {
                    context.getString(R.string.error_duplicate_transaction)
                } else {
                    context.getString(R.string.error_database_operation, operation)
                }
            }
            else -> context.getString(R.string.error_database_operation, operation)
        }
    }
    
    /**
     * Handle permission-related errors
     */
    fun handlePermissionError(context: Context, permission: String): String {
        Log.w(TAG, "Permission denied: $permission")
        
        return when (permission) {
            android.Manifest.permission.RECEIVE_SMS -> 
                context.getString(R.string.error_sms_permission_required)
            android.Manifest.permission.POST_NOTIFICATIONS -> 
                context.getString(R.string.error_notification_permission_required)
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE -> 
                context.getString(R.string.error_storage_permission_required)
            else -> context.getString(R.string.error_permission_required, permission)
        }
    }
    
    /**
     * Handle export-related errors
     */
    fun handleExportError(context: Context, error: Throwable): String {
        Log.e(TAG, "Export operation failed", error)
        
        return when (error) {
            is IOException -> context.getString(R.string.error_export_file_write)
            is SecurityException -> context.getString(R.string.error_export_permission)
            else -> context.getString(R.string.error_export_failed)
        }
    }
    
    /**
     * Log non-critical warnings
     */
    fun logWarning(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.w(tag, message, throwable)
        } else {
            Log.w(tag, message)
        }
    }
    
    /**
     * Log informational messages
     */
    fun logInfo(tag: String, message: String) {
        Log.i(tag, message)
    }
}