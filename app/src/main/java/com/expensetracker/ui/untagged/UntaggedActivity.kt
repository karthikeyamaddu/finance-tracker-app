package com.expensetracker.ui.untagged

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.ExpenseTrackerApplication
import com.expensetracker.R
import com.expensetracker.data.model.Transaction
import com.expensetracker.databinding.ActivityUntaggedBinding
import com.expensetracker.ui.detail.TransactionDetailActivity
import com.expensetracker.ui.home.TransactionAdapter
import com.expensetracker.utils.Constants
import com.google.android.material.snackbar.Snackbar

/**
 * Activity for displaying untagged transactions
 * Uses amber/yellow accent colors to emphasize action needed
 */
class UntaggedActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityUntaggedBinding
    private lateinit var viewModel: UntaggedViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUntaggedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupViewModel() {
        val application = application as ExpenseTrackerApplication
        val factory = UntaggedViewModelFactory(application.repository)
        viewModel = ViewModelProvider(this, factory)[UntaggedViewModel::class.java]
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            openTransactionDetail(transaction)
        }
        
        binding.rvUntaggedTransactions.apply {
            layoutManager = LinearLayoutManager(this@UntaggedActivity)
            adapter = transactionAdapter
        }
    }
    
    private fun setupClickListeners() {
        // Back button
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun observeViewModel() {
        // Observe untagged transactions
        viewModel.untaggedTransactions.observe(this) { transactions ->
            updateTransactionsList(transactions)
        }
        
        // Observe untagged count for title
        viewModel.untaggedCount.observe(this) { count ->
            updateTitle(count)
        }
        
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                showSnackbar(it)
                viewModel.clearErrorMessage()
            }
        }
    }
    
    private fun updateTransactionsList(transactions: List<Transaction>) {
        if (transactions.isEmpty()) {
            binding.rvUntaggedTransactions.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvUntaggedTransactions.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            transactionAdapter.submitList(transactions)
        }
    }
    
    private fun updateTitle(count: Int) {
        supportActionBar?.title = getString(R.string.untagged_title, count)
    }
    
    private fun openTransactionDetail(transaction: Transaction) {
        val intent = Intent(this, TransactionDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_TRANSACTION_ID, transaction.id)
        }
        startActivity(intent)
    }
    
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when returning to the screen (e.g., after tagging a transaction)
        viewModel.refreshData()
    }
}