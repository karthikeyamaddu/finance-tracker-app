package com.expensetracker.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.expensetracker.ExpenseTrackerApplication
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.notification.TransactionNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver for intercepting SMS messages from Axis Bank
 * Automatically processes UPI transaction messages and saves them to database
 */
class TransactionSmsReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "TransactionSmsReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "SMS received, action: ${intent.action}")
        
        // Only process SMS_RECEIVED_ACTION
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }
        
        try {
            // Extract SMS messages from intent
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isNullOrEmpty()) {
                Log.w(TAG, "No SMS messages found in intent")
                return
            }
            
            // Get application dependencies
            val application = context.applicationContext as ExpenseTrackerApplication
            val smsParser = application.smsParser
            val repository = application.repository
            val notificationHelper = TransactionNotificationHelper(context)
            
            // Process each SMS message
            for (message in messages) {
                val sender = message.displayOriginatingAddress
                val body = message.messageBody
                
                Log.d(TAG, "Processing SMS from: $sender")
                
                // Check if sender matches Axis Bank pattern (??-AXISBK-S)
                if (!smsParser.isValidAxisBankSender(sender)) {
                    Log.d(TAG, "SMS not from Axis Bank, ignoring. Sender: $sender")
                    continue
                }
                
                // Check if SMS contains transaction keywords
                if (!smsParser.containsTransactionKeywords(body)) {
                    Log.d(TAG, "SMS doesn't contain transaction keywords, ignoring")
                    continue
                }
                
                // Process the Axis Bank SMS in background
                processAxisBankSms(body, smsParser, repository, notificationHelper)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing SMS: ${e.message}", e)
        }
    }
    
    /**
     * Process Axis Bank SMS message in background thread
     */
    private fun processAxisBankSms(
        smsBody: String,
        smsParser: SmsParser,
        repository: TransactionRepository,
        notificationHelper: TransactionNotificationHelper
    ) {
        // Use background coroutine scope for database operations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Processing Axis Bank SMS: $smsBody")
                
                // Parse the SMS content
                val transaction = smsParser.parseTransaction(smsBody)
                if (transaction == null) {
                    Log.w(TAG, "Failed to parse SMS transaction")
                    return@launch
                }
                
                // Save to database
                val transactionId = repository.insertTransaction(transaction)
                
                Log.d(TAG, "Transaction saved with ID: $transactionId")
                
                // Show notification to user
                val savedTransaction = transaction.copy(id = transactionId)
                notificationHelper.showNewTransactionNotification(savedTransaction)
                
                Log.d(TAG, "Successfully processed transaction: ${savedTransaction.receiverSenderName}, Amount: ₹${savedTransaction.amount}")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error saving transaction: ${e.message}", e)
            }
        }
    }
}