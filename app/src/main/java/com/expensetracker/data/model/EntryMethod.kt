package com.expensetracker.data.model

/**
 * Enum representing how a transaction was entered into the system
 */
enum class EntryMethod {
    /**
     * Transaction was automatically captured from SMS
     */
    SMS,
    
    /**
     * Transaction was manually entered by the user
     */
    MANUAL;
    
    /**
     * Get display name for UI
     */
    fun getDisplayName(): String {
        return when (this) {
            SMS -> "SMS"
            MANUAL -> "Manual"
        }
    }
    
    /**
     * Get description for UI
     */
    fun getDescription(): String {
        return when (this) {
            SMS -> "Automatically captured from SMS"
            MANUAL -> "Manually entered by user"
        }
    }
    
    /**
     * Check if transaction was automatically captured
     */
    fun isAutomatic(): Boolean = this == SMS
    
    /**
     * Check if transaction was manually entered
     */
    fun isManual(): Boolean = this == MANUAL
}