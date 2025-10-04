package com.expensetracker.ui.history

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.ExpenseTrackerApplication
import com.expensetracker.databinding.ActivityHistoryBinding
import com.expensetracker.ui.detail.TransactionDetailActivity
import com.expensetracker.ui.home.TransactionAdapter
import com.expensetracker.utils.Constants

class HistoryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
        setupRecyclerView()
        observeViewModel()
    }
    
    private fun setupViewModel() {
        val application = application as ExpenseTrackerApplication
        val factory = HistoryViewModelFactory(application.repository)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]
    }
    
    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            val intent = Intent(this, TransactionDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_TRANSACTION_ID, transaction.id)
            }
            startActivity(intent)
        }
        
        binding.rvAllTransactions.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = transactionAdapter
        }
    }
    
    private fun observeViewModel() {
        viewModel.allTransactions.observe(this) { transactions ->
            transactionAdapter.submitList(transactions)
        }
    }
}