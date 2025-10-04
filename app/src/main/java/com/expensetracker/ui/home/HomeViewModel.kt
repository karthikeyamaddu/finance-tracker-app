package com.expensetracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.model.TimeFormat
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.utils.Constants
import com.expensetracker.utils.SettingsManager
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen
 * Manages today's transactions, time format, and untagged count
 */
class HomeViewModel(
    private val repository: TransactionRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {
    
    // Today's transactions
    private val _todayTransactions = MutableLiveData<List<Transaction>>()
    val todayTransactions: LiveData<List<Transaction>> = _todayTransactions
    
    // Time format preference
    private val _timeFormat = MutableLiveData<TimeFormat>()
    val timeFormat: LiveData<TimeFormat> = _timeFormat
    
    // Untagged transactions count
    private val _untaggedCount = MutableLiveData<Int>()
    val untaggedCount: LiveData<Int> = _untaggedCount
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    init {
        loadInitialData()
    }
    
    /**
     * Load initial data when ViewModel is created
     */
    private fun loadInitialData() {
        loadTodayTransactions()
        loadTimeFormat()
        loadUntaggedCount()
    }
    
    /**
     * Load today's transactions from repository
     */
    private fun loadTodayTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getTodayTransactions()
                    .catch { exception ->
                        _errorMessage.value = Constants.ERROR_LOADING_TRANSACTIONS
                        _todayTransactions.value = emptyList()
                    }
                    .collect { transactions ->
                        _todayTransactions.value = transactions
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load time format preference from settings
     */
    private fun loadTimeFormat() {
        _timeFormat.value = settingsManager.getTimeFormat()
    }
    
    /**
     * Load count of untagged transactions
     */
    private fun loadUntaggedCount() {
        viewModelScope.launch {
            repository.getUntaggedCount()
                .catch { exception ->
                    _untaggedCount.value = 0
                }
                .collect { count ->
                    _untaggedCount.value = count
                }
        }
    }
    
    /**
     * Toggle between 12h and 24h time format
     */
    fun toggleTimeFormat() {
        val currentFormat = _timeFormat.value ?: TimeFormat.TWENTY_FOUR_HOUR
        val newFormat = currentFormat.toggle()
        
        // Save to settings
        settingsManager.setTimeFormat(newFormat)
        
        // Update LiveData
        _timeFormat.value = newFormat
    }
    
    /**
     * Save a manually entered transaction
     */
    fun saveManualTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.insertTransaction(transaction)
                // Refresh today's transactions to show the new one
                refreshTodayTransactions()
            } catch (e: Exception) {
                _errorMessage.value = Constants.ERROR_SAVING_TRANSACTION
            }
        }
    }
    
    /**
     * Refresh today's transactions (called when returning to screen)
     */
    fun refreshData() {
        refreshTodayTransactions()
        refreshUntaggedCount()
    }
    
    /**
     * Refresh today's transactions only
     */
    private fun refreshTodayTransactions() {
        viewModelScope.launch {
            try {
                repository.getTodayTransactions()
                    .catch { exception ->
                        _errorMessage.value = Constants.ERROR_LOADING_TRANSACTIONS
                    }
                    .collect { transactions ->
                        _todayTransactions.value = transactions
                    }
            } catch (e: Exception) {
                _errorMessage.value = Constants.ERROR_LOADING_TRANSACTIONS
            }
        }
    }
    
    /**
     * Refresh untagged count
     */
    private fun refreshUntaggedCount() {
        viewModelScope.launch {
            try {
                repository.getUntaggedCount()
                    .catch { exception ->
                        _untaggedCount.value = 0
                    }
                    .collect { count ->
                        _untaggedCount.value = count
                    }
            } catch (e: Exception) {
                // Silently fail for count updates
                _untaggedCount.value = 0
            }
        }
    }
    
    /**
     * Clear error message after it's been shown
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    /**
     * Get current time format
     */
    fun getCurrentTimeFormat(): TimeFormat {
        return _timeFormat.value ?: TimeFormat.TWENTY_FOUR_HOUR
    }
}