package com.expensetracker.ui.detail

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.expensetracker.ExpenseTrackerApplication
import com.expensetracker.R
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import com.expensetracker.databinding.ActivityTransactionDetailBinding
import com.expensetracker.utils.Constants
import com.expensetracker.utils.DateTimeFormatter
import com.google.android.material.snackbar.Snackbar

/**
 * Activity for displaying transaction details and allowing tag editing
 * Implements animations and Material Design principles
 */
class TransactionDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTransactionDetailBinding
    private lateinit var viewModel: TransactionDetailViewModel
    private var transactionId: Long = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get transaction ID from intent
        transactionId = intent.getLongExtra(Constants.EXTRA_TRANSACTION_ID, -1)
        if (transactionId == -1L) {
            finish()
            return
        }
        
        setupViewModel()
        setupUI()
        setupClickListeners()
        observeViewModel()
        
        // Load transaction data
        viewModel.loadTransaction(transactionId)
    }
    
    private fun setupViewModel() {
        val application = application as ExpenseTrackerApplication
        val factory = TransactionDetailViewModelFactory(application.repository)
        viewModel = ViewModelProvider(this, factory)[TransactionDetailViewModel::class.java]
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Setup quick tag chips
        setupQuickTagChips()
    }
    
    private fun setupClickListeners() {
        // Back button
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Save button
        binding.fabSave.setOnClickListener {
            val tag = binding.etTag.text?.toString()?.trim()
            viewModel.saveTag(tag)
        }
    }
    
    private fun setupQuickTagChips() {
        // Set click listeners for quick tag chips
        binding.chipGroceries.setOnClickListener {
            binding.etTag.setText(getString(R.string.tag_groceries))
        }
        
        binding.chipTransport.setOnClickListener {
            binding.etTag.setText(getString(R.string.tag_transport))
        }
        
        binding.chipFood.setOnClickListener {
            binding.etTag.setText(getString(R.string.tag_food))
        }
    }
    
    private fun observeViewModel() {
        // Observe transaction data
        viewModel.transaction.observe(this) { transaction ->
            transaction?.let {
                displayTransaction(it)
                animateTransactionDisplay()
            }
        }
        
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe save success
        viewModel.saveSuccess.observe(this) { success ->
            if (success) {
                animateSaveSuccess()
                showSnackbar(getString(R.string.tag_updated))
                // Finish activity after short delay
                binding.root.postDelayed({ finish() }, 1000)
            }
        }
        
        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                showSnackbar(it)
                viewModel.clearErrorMessage()
            }
        }
    }
    
    private fun displayTransaction(transaction: Transaction) {
        // Set amount
        binding.tvAmount.text = transaction.getFormattedAmount()
        
        // Set transaction type and colors
        when (transaction.transactionType) {
            TransactionType.DEBIT -> {
                binding.tvTransactionType.text = getString(R.string.debited)
                binding.tvTransactionType.setBackgroundResource(R.drawable.debit_badge_background)
                binding.tvAmount.setTextColor(ContextCompat.getColor(this, R.color.debit_red))
                binding.ivTransactionIcon.setImageResource(R.drawable.ic_arrow_down)
                binding.ivTransactionIcon.setColorFilter(ContextCompat.getColor(this, R.color.debit_red))
            }
            TransactionType.CREDIT -> {
                binding.tvTransactionType.text = getString(R.string.credited)
                binding.tvTransactionType.setBackgroundResource(R.drawable.credit_badge_background)
                binding.tvAmount.setTextColor(ContextCompat.getColor(this, R.color.credit_green))
                binding.ivTransactionIcon.setImageResource(R.drawable.ic_arrow_up)
                binding.ivTransactionIcon.setColorFilter(ContextCompat.getColor(this, R.color.credit_green))
            }
        }
        
        // Set date and time
        val dateStr = DateTimeFormatter.formatTodayDate(transaction.transactionDate)
        val timeStr = DateTimeFormatter.formatTimeWithSeconds(transaction.transactionTime, 
            com.expensetracker.data.model.TimeFormat.TWENTY_FOUR_HOUR) // Default to 24h for detail view
        binding.tvDateTime.text = "$dateStr • $timeStr"
        
        // Set receiver/sender name
        binding.tvReceiverSenderName.text = transaction.receiverSenderName
        
        // Set bank and account
        binding.tvBankAccount.text = "${transaction.bankName} (${transaction.accountNumber})"
        
        // Set UPI reference
        if (transaction.upiReference != null) {
            binding.layoutUpiReference.visibility = View.VISIBLE
            binding.tvUpiReference.text = transaction.upiReference
        } else {
            binding.layoutUpiReference.visibility = View.GONE
        }
        
        // Set existing tag
        if (!transaction.userTag.isNullOrBlank()) {
            binding.etTag.setText(transaction.userTag)
        }
    }
    
    private fun animateTransactionDisplay() {
        // Animate amount with pop effect
        val amountAnimator = ObjectAnimator.ofFloat(binding.tvAmount, "scaleX", 1.0f, 1.15f, 1.0f)
        amountAnimator.duration = 500
        amountAnimator.interpolator = OvershootInterpolator()
        amountAnimator.start()
        
        val amountAnimatorY = ObjectAnimator.ofFloat(binding.tvAmount, "scaleY", 1.0f, 1.15f, 1.0f)
        amountAnimatorY.duration = 500
        amountAnimatorY.interpolator = OvershootInterpolator()
        amountAnimatorY.start()
        
        // Animate icon pulse
        val iconAnimator = ObjectAnimator.ofFloat(binding.ivTransactionIcon, "scaleX", 1.0f, 1.2f, 1.0f)
        iconAnimator.duration = 600
        iconAnimator.start()
        
        val iconAnimatorY = ObjectAnimator.ofFloat(binding.ivTransactionIcon, "scaleY", 1.0f, 1.2f, 1.0f)
        iconAnimatorY.duration = 600
        iconAnimatorY.start()
        
        // Animate badge fade-in
        binding.tvTransactionType.alpha = 0f
        binding.tvTransactionType.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
    
    private fun animateSaveSuccess() {
        // Animate save button with checkmark
        binding.fabSave.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(150)
            .withEndAction {
                binding.fabSave.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
    
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}