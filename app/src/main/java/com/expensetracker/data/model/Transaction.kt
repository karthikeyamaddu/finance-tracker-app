package com.expensetracker.data.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model representing a financial transaction
 * This is the core business model used throughout the application
 */
data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val transactionType: TransactionType,
    val accountNumber: String,
    val transactionDate: LocalDate,
    val transactionTime: LocalTime,
    val receiverSenderName: String,
    val upiReference: String?,
    val bankName: String,
    val userTag: String?,
    val isTagged: Boolean,
    val entryMethod: EntryMethod,
    val rawSmsText: String?,
    val createdAt: Long
) {
    
    /**
     * Get formatted amount with currency symbol
     */
    fun getFormattedAmount(): String {
        return "₹${String.format("%.2f", amount)}"
    }
    
    /**
     * Get display name for transaction type
     */
    fun getTransactionTypeDisplay(): String {
        return when (transactionType) {
            TransactionType.DEBIT -> "DEBITED"
            TransactionType.CREDIT -> "CREDITED"
        }
    }
    
    /**
     * Check if transaction needs tagging
     */
    fun needsTag(): Boolean {
        return !isTagged || userTag.isNullOrBlank()
    }
    
    /**
     * Get display tag or default message
     */
    fun getDisplayTag(): String {
        return if (isTagged && !userTag.isNullOrBlank()) {
            userTag
        } else {
            "+ Add Tag"
        }
    }
    
    /**
     * Check if transaction is from today
     */
    fun isFromToday(): Boolean {
        return transactionDate == LocalDate.now()
    }
    
    /**
     * Get short receiver/sender name (first 20 characters)
     */
    fun getShortName(): String {
        return if (receiverSenderName.length > 20) {
            "${receiverSenderName.take(17)}..."
        } else {
            receiverSenderName
        }
    }
}