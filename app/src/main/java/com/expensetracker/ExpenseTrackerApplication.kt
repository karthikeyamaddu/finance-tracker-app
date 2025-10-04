package com.expensetracker

import android.app.Application
import com.expensetracker.data.database.TransactionDatabase
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.sms.SmsParser
import com.expensetracker.utils.SettingsManager

/**
 * Application class for Expense Tracker
 * Initializes core dependencies and provides singleton instances
 */
class ExpenseTrackerApplication : Application() {
    
    // Database instance - lazy initialization
    val database by lazy { TransactionDatabase.getDatabase(this) }
    
    // SMS Parser instance
    val smsParser by lazy { SmsParser() }
    
    // Settings Manager instance
    val settingsManager by lazy { SettingsManager(this) }
    
    // Repository instance - depends on database and SMS parser
    val repository by lazy { 
        TransactionRepository(
            transactionDao = database.transactionDao(),
            smsParser = smsParser
        ) 
    }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any required components here
    }
}