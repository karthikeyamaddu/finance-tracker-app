package com.expensetracker.data.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Enum representing time display format preference
 */
enum class TimeFormat {
    /**
     * 12-hour format with AM/PM (e.g., 2:30 PM)
     */
    TWELVE_HOUR,
    
    /**
     * 24-hour format (e.g., 14:30)
     */
    TWENTY_FOUR_HOUR;
    
    /**
     * Get display name for UI toggle
     */
    fun getDisplayName(): String {
        return when (this) {
            TWELVE_HOUR -> "12h"
            TWENTY_FOUR_HOUR -> "24h"
        }
    }
    
    /**
     * Get description for settings
     */
    fun getDescription(): String {
        return when (this) {
            TWELVE_HOUR -> "12-hour format (AM/PM)"
            TWENTY_FOUR_HOUR -> "24-hour format"
        }
    }
    
    /**
     * Format time according to this format preference
     */
    fun formatTime(time: LocalTime): String {
        return when (this) {
            TWELVE_HOUR -> time.format(DateTimeFormatter.ofPattern("h:mm a"))
            TWENTY_FOUR_HOUR -> time.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }
    
    /**
     * Format time with seconds according to this format preference
     */
    fun formatTimeWithSeconds(time: LocalTime): String {
        return when (this) {
            TWELVE_HOUR -> time.format(DateTimeFormatter.ofPattern("h:mm:ss a"))
            TWENTY_FOUR_HOUR -> time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        }
    }
    
    /**
     * Get the opposite format for toggling
     */
    fun toggle(): TimeFormat {
        return when (this) {
            TWELVE_HOUR -> TWENTY_FOUR_HOUR
            TWENTY_FOUR_HOUR -> TWELVE_HOUR
        }
    }
}