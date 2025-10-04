package com.expensetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.utils.SettingsManager

/**
 * Factory class for creating HomeViewModel with dependencies
 */
class HomeViewModelFactory(
    private val repository: TransactionRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}