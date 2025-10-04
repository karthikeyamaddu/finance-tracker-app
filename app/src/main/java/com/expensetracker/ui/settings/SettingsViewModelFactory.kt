package com.expensetracker.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.utils.SettingsManager

/**
 * Factory class for creating SettingsViewModel with dependencies
 */
class SettingsViewModelFactory(
    private val repository: TransactionRepository,
    private val settingsManager: SettingsManager,
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repository, settingsManager, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}