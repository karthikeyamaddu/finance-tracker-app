package com.expensetracker.ui.untagged

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.expensetracker.data.repository.TransactionRepository

/**
 * Factory class for creating UntaggedViewModel with dependencies
 */
class UntaggedViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UntaggedViewModel::class.java)) {
            return UntaggedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}