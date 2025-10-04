package com.expensetracker.utils

import android.content.Context
import android.content.SharedPreferences
import com.expensetracker.data.model.TimeFormat

/**
 * Manager class for app settings and preferences
 * Handles storage and retrieval of user preferences using SharedPreferences
 */
class SettingsManager(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "expense_tracker_prefs"
        private const val KEY_TIME_FORMAT = "time_format"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Get current time format preference
     */
    fun getTimeFormat(): TimeFormat {
        val formatName = prefs.getString(KEY_TIME_FORMAT, TimeFormat.TWENTY_FOUR_HOUR.name)
        return try {
            TimeFormat.valueOf(formatName ?: TimeFormat.TWENTY_FOUR_HOUR.name)
        } catch (e: IllegalArgumentException) {
            TimeFormat.TWENTY_FOUR_HOUR
        }
    }
    
    /**
     * Set time format preference
     */
    fun setTimeFormat(timeFormat: TimeFormat) {
        prefs.edit()
            .putString(KEY_TIME_FORMAT, timeFormat.name)
            .apply()
    }
    
    /**
     * Check if notifications are enabled
     */
    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }
    
    /**
     * Set notifications enabled state
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
            .apply()
    }
    
    /**
     * Check if this is the first app launch
     */
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    /**
     * Mark first launch as completed
     */
    fun setFirstLaunchCompleted() {
        prefs.edit()
            .putBoolean(KEY_FIRST_LAUNCH, false)
            .apply()
    }
    
    /**
     * Clear all preferences (for data reset functionality)
     */
    fun clearAllPreferences() {
        prefs.edit().clear().apply()
    }
}