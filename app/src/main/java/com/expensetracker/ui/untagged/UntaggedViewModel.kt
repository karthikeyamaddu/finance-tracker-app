package com.expensetracker.ui.untagged

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.utils.Constants
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for the Untagged Transactions screen
 * Manages untagged transactions list and count
 */
class UntaggedViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    
    // Untagged transactions
    private val _untaggedTransactions = MutableLiveData<List<Transaction>>()
    val untaggedTransactions: LiveData<List<Transaction>> = _untaggedTransactions
    
    // Untagged count
    private val _untaggedCount = MutableLiveData<Int>()
    val untaggedCount: LiveData<Int> = _untaggedCount
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    init {
        loadUntaggedTransactions()
        loadUntaggedCount()
    }
    
    /**
     * Load untagged transactions from repository
     */
    private fun loadUntaggedTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getUntaggedTransactions()
                    .catch { _ ->
                        _errorMessage.value = Constants.ERROR_LOADING_TRANSACTIONS
                        _untaggedTransactions.value = emptyList()
                    }
                    .collect { transactions ->
                        _untaggedTransactions.value = transactions
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load count of untagged transactions
     */
    private fun loadUntaggedCount() {
        viewModelScope.launch {
            repository.getUntaggedCount()
                .catch { _ ->
                    _untaggedCount.value = 0
                }
                .collect { count ->
                    _untaggedCount.value = count
                }
        }
    }
    
    /**
     * Refresh data (called when returning to screen)
     */
    fun refreshData() {
        loadUntaggedTransactions()
        loadUntaggedCount()
    }
    
    /**
     * Clear error message after it's been shown
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}