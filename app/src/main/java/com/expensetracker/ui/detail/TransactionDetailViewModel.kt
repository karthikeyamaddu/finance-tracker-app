package com.expensetracker.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.utils.Constants
import kotlinx.coroutines.launch

/**
 * ViewModel for the Transaction Detail screen
 * Manages transaction data loading and tag updates
 */
class TransactionDetailViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    
    // Transaction data
    private val _transaction = MutableLiveData<Transaction?>()
    val transaction: LiveData<Transaction?> = _transaction
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Save success state
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess
    
    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    /**
     * Load transaction by ID
     */
    fun loadTransaction(transactionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val transaction = repository.getTransactionById(transactionId)
                _transaction.value = transaction
                if (transaction == null) {
                    _errorMessage.value = "Transaction not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = Constants.ERROR_LOADING_TRANSACTIONS
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Save tag for the current transaction
     */
    fun saveTag(tag: String?) {
        val currentTransaction = _transaction.value
        if (currentTransaction == null) {
            _errorMessage.value = "No transaction loaded"
            return
        }
        
        viewModelScope.launch {
            try {
                repository.updateTransactionTag(currentTransaction.id, tag)
                
                // Update local transaction object
                val updatedTransaction = currentTransaction.copy(
                    userTag = tag,
                    isTagged = !tag.isNullOrBlank()
                )
                _transaction.value = updatedTransaction
                _saveSuccess.value = true
                
            } catch (e: Exception) {
                _errorMessage.value = Constants.ERROR_UPDATING_TAG
            }
        }
    }
    
    /**
     * Clear error message after it's been shown
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}