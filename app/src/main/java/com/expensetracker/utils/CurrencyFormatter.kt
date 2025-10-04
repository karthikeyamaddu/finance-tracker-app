package com.expensetracker.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * Utility class for formatting currency amounts
 */
object CurrencyFormatter {
    
    private const val CURRENCY_SYMBOL = "₹"
    
    // Number formatters
    private val AMOUNT_FORMATTER = DecimalFormat("#,##0.00")
    private val COMPACT_FORMATTER = DecimalFormat("#,##0.##")
    
    /**
     * Format amount with currency symbol (e.g., "₹1,234.56")
     */
    fun formatAmount(amount: Double): String {
        return "$CURRENCY_SYMBOL${AMOUNT_FORMATTER.format(amount)}"
    }
    
    /**
     * Format amount without currency symbol (e.g., "1,234.56")
     */
    fun formatAmountWithoutSymbol(amount: Double): String {
        return AMOUNT_FORMATTER.format(amount)
    }
    
    /**
     * Format amount in compact form (e.g., "₹1,234.5" instead of "₹1,234.50")
     */
    fun formatAmountCompact(amount: Double): String {
        return "$CURRENCY_SYMBOL${COMPACT_FORMATTER.format(amount)}"
    }
    
    /**
     * Format large amounts with K/L notation (e.g., "₹1.2K", "₹1.5L")
     */
    fun formatAmountShort(amount: Double): String {
        return when {
            amount >= 10_000_000 -> "$CURRENCY_SYMBOL${String.format("%.1f", amount / 10_000_000)}Cr"
            amount >= 100_000 -> "$CURRENCY_SYMBOL${String.format("%.1f", amount / 100_000)}L"
            amount >= 1_000 -> "$CURRENCY_SYMBOL${String.format("%.1f", amount / 1_000)}K"
            else -> formatAmountCompact(amount)
        }
    }
    
    /**
     * Parse amount string to double (removes currency symbol and commas)
     */
    fun parseAmount(amountString: String): Double? {
        return try {
            val cleanString = amountString
                .replace(CURRENCY_SYMBOL, "")
                .replace(",", "")
                .trim()
            cleanString.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Validate if amount string is valid
     */
    fun isValidAmount(amountString: String): Boolean {
        val amount = parseAmount(amountString)
        return amount != null && amount > 0
    }
    
    /**
     * Get currency symbol
     */
    fun getCurrencySymbol(): String {
        return CURRENCY_SYMBOL
    }
}