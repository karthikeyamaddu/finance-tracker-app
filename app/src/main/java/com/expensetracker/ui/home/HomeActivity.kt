package com.expensetracker.ui.home

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.ExpenseTrackerApplication
import com.expensetracker.R
import com.expensetracker.data.model.TimeFormat
import com.expensetracker.data.model.Transaction
import com.expensetracker.databinding.ActivityHomeBinding
import com.expensetracker.ui.detail.TransactionDetailActivity
import com.expensetracker.ui.history.HistoryActivity
import com.expensetracker.ui.manual.ManualEntryDialog
import com.expensetracker.ui.settings.SettingsActivity
import com.expensetracker.ui.untagged.UntaggedActivity
import com.expensetracker.utils.Constants
import com.expensetracker.utils.DateTimeFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Main activity displaying today's transactions and navigation options
 * Implements the home screen with transaction list, FAB, and bottom navigation
 */
class HomeActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    
    // Permission launcher for SMS permission
    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showSnackbar("SMS permission granted. Transaction capture is now active.")
        } else {
            showSmsPermissionRationale()
        }
    }
    
    // Permission launcher for notification permission (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showSnackbar("Notification permission denied. You won't receive transaction alerts.")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        
        // Check permissions on first launch
        checkPermissions()
    }
    
    private fun setupViewModel() {
        val application = application as ExpenseTrackerApplication
        val factory = HomeViewModelFactory(application.repository, application.settingsManager)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }
    
    private fun setupUI() {
        // Set today's date
        val todayDate = DateTimeFormatter.getCurrentDateString()
        binding.tvTodayDate.text = getString(R.string.today_transactions, todayDate)
        
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            openTransactionDetail(transaction)
        }
        
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = transactionAdapter
        }
    }
    
    private fun setupClickListeners() {
        // Time format toggle
        binding.btnTimeFormat.setOnClickListener {
            viewModel.toggleTimeFormat()
        }
        
        // Settings button
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // Untagged transactions button
        binding.btnUntagged.setOnClickListener {
            startActivity(Intent(this, UntaggedActivity::class.java))
        }
        
        // All history button
        binding.btnAllHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        
        // Floating action button - manual entry
        binding.fabAddTransaction.setOnClickListener {
            showManualEntryDialog()
        }
    }
    
    private fun observeViewModel() {
        // Observe today's transactions
        viewModel.todayTransactions.observe(this) { transactions ->
            updateTransactionsList(transactions)
        }
        
        // Observe time format
        viewModel.timeFormat.observe(this) { timeFormat ->
            updateTimeFormatButton(timeFormat)
            transactionAdapter.updateTimeFormat(timeFormat)
        }
        
        // Observe untagged count
        viewModel.untaggedCount.observe(this) { count ->
            updateUntaggedButton(count)
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
            binding.rvTransactions.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvTransactions.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            transactionAdapter.submitList(transactions)
        }
    }
    
    private fun updateTimeFormatButton(timeFormat: TimeFormat) {
        animateTimeFormatToggle(timeFormat)
    }
    
    /**
     * Animate time format toggle with slide and cross-fade effect
     */
    private fun animateTimeFormatToggle(timeFormat: TimeFormat) {
        val button = binding.btnTimeFormat
        
        // Create slide out animation
        val slideOut = ObjectAnimator.ofFloat(button, "translationX", 0f, -30f)
        val fadeOut = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)
        
        val outSet = AnimatorSet()
        outSet.playTogether(slideOut, fadeOut)
        outSet.duration = 150
        
        // Create slide in animation
        val slideIn = ObjectAnimator.ofFloat(button, "translationX", 30f, 0f)
        val fadeIn = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f)
        
        val inSet = AnimatorSet()
        inSet.playTogether(slideIn, fadeIn)
        inSet.duration = 150
        
        // Chain animations
        outSet.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // Update text during transition
                button.text = timeFormat.getDisplayName()
                inSet.start()
            }
        })
        
        outSet.start()
    }
    
    private fun updateUntaggedButton(count: Int) {
        binding.btnUntagged.text = getString(R.string.view_untagged, count)
    }
    
    private fun openTransactionDetail(transaction: Transaction) {
        val intent = Intent(this, TransactionDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_TRANSACTION_ID, transaction.id)
        }
        startActivity(intent)
    }
    
    private fun showManualEntryDialog() {
        val dialog = ManualEntryDialog { transaction ->
            viewModel.saveManualTransaction(transaction)
        }
        dialog.show(supportFragmentManager, "ManualEntryDialog")
    }
    
    private fun checkPermissions() {
        // Check SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) 
            != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                showSmsPermissionRationale()
            } else {
                smsPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
            }
        }
        
        // Check notification permission (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    private fun showSmsPermissionRationale() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_sms_title)
            .setMessage(R.string.permission_sms_message)
            .setPositiveButton(R.string.grant_permission) { _, _ ->
                smsPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
            }
            .setNegativeButton(R.string.not_now, null)
            .show()
    }
    
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when returning to the screen
        viewModel.refreshData()
    }
}