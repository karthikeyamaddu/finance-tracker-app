package com.expensetracker.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    
    private val _allTransactions = MutableLiveData<List<Transaction>>()
    val allTransactions: LiveData<List<Transaction>> = _allTransactions
    
    init {
        loadAllTransactions()
    }
    
    private fun loadAllTransactions() {
        viewModelScope.launch {
            repository.getAllTransactions()
                .catch { _allTransactions.value = emptyList() }
                .collect { transactions ->
                    _allTransactions.value = transactions
                }
        }
    }
}