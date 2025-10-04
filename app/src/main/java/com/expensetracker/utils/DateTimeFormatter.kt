package com.expensetracker.utils

import com.expensetracker.data.model.TimeFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Utility class for formatting dates and times throughout the app
 */
object DateTimeFormatter {
    
    // Date formatters
    private val TODAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy")
    private val SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM")
    private val MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy")
    
    /**
     * Format date for "Today" display (e.g., "03 Oct 2025")
     */
    fun formatTodayDate(date: LocalDate): String {
        return date.format(TODAY_FORMATTER)
    }
    
    /**
     * Format date for transaction cards (e.g., "02 Oct")
     */
    fun formatShortDate(date: LocalDate): String {
        return date.format(SHORT_DATE_FORMATTER)
    }
    
    /**
     * Format month and year for history grouping (e.g., "October 2025")
     */
    fun formatMonthYear(date: LocalDate): String {
        return date.format(MONTH_YEAR_FORMATTER)
    }
    
    /**
     * Format time according to user preference
     */
    fun formatTime(time: LocalTime, timeFormat: TimeFormat): String {
        return timeFormat.formatTime(time)
    }
    
    /**
     * Format time with seconds according to user preference
     */
    fun formatTimeWithSeconds(time: LocalTime, timeFormat: TimeFormat): String {
        return timeFormat.formatTimeWithSeconds(time)
    }
    
    /**
     * Get relative date string (Today, Yesterday, or formatted date)
     */
    fun getRelativeDateString(date: LocalDate): String {
        val today = LocalDate.now()
        return when {
            date == today -> "Today"
            date == today.minusDays(1) -> "Yesterday"
            date.year == today.year -> formatShortDate(date)
            else -> date.format(TODAY_FORMATTER)
        }
    }
    
    /**
     * Check if date is today
     */
    fun isToday(date: LocalDate): Boolean {
        return date == LocalDate.now()
    }
    
    /**
     * Check if date is yesterday
     */
    fun isYesterday(date: LocalDate): Boolean {
        return date == LocalDate.now().minusDays(1)
    }
    
    /**
     * Get current date string for display
     */
    fun getCurrentDateString(): String {
        return formatTodayDate(LocalDate.now())
    }
}