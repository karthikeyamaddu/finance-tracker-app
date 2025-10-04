package com.expensetracker.data.model

/**
 * Enum representing the type of financial transaction
 */
enum class TransactionType {
    /**
     * Money going out of the account (expense)
     */
    DEBIT,
    
    /**
     * Money coming into the account (income)
     */
    CREDIT;
    
    /**
     * Get display name for UI
     */
    fun getDisplayName(): String {
        return when (this) {
            DEBIT -> "Debit"
            CREDIT -> "Credit"
        }
    }
    
    /**
     * Get badge text for transaction cards
     */
    fun getBadgeText(): String {
        return when (this) {
            DEBIT -> "DEBITED"
            CREDIT -> "CREDITED"
        }
    }
    
    /**
     * Check if this is a debit transaction
     */
    fun isDebit(): Boolean = this == DEBIT
    
    /**
     * Check if this is a credit transaction
     */
    fun isCredit(): Boolean = this == CREDIT
}