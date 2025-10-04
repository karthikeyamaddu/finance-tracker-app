package com.expensetracker.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.R
import com.expensetracker.data.model.TimeFormat
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import com.expensetracker.databinding.ItemTransactionBinding
import com.expensetracker.utils.DateTimeFormatter

/**
 * RecyclerView adapter for displaying transaction items
 * Handles color coding, time formatting, click events, and animations
 */
class TransactionAdapter(
    private val onTransactionClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {
    
    private var timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOUR
    private var lastPosition = -1
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return TransactionViewHolder(binding, onTransactionClick)
    }
    
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position), timeFormat)
        
        // Add slide-up animation for new items
        if (position > lastPosition) {
            animateItemEntry(holder.itemView, position)
            lastPosition = position
        }
    }
    
    /**
     * Animate item entry with slide up and fade in
     */
    private fun animateItemEntry(view: View, position: Int) {
        // Set initial state
        view.translationY = 100f
        view.alpha = 0f
        
        // Create slide up animation
        val slideUp = ObjectAnimator.ofFloat(view, "translationY", 100f, 0f)
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(slideUp, fadeIn)
        animatorSet.duration = 300
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.startDelay = (position * 50).toLong() // Stagger animation
        animatorSet.start()
    }
    
    /**
     * Update time format and refresh all items
     */
    fun updateTimeFormat(newTimeFormat: TimeFormat) {
        timeFormat = newTimeFormat
        notifyDataSetChanged()
    }
    
    /**
     * Reset animation position when new data is submitted
     */
    override fun submitList(list: List<Transaction>?) {
        lastPosition = -1
        super.submitList(list)
    }
    
    /**
     * ViewHolder class for transaction items
     */
    class TransactionViewHolder(
        private val binding: ItemTransactionBinding,
        private val onTransactionClick: (Transaction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(transaction: Transaction, timeFormat: TimeFormat) {
            binding.apply {
                // Set click listener with ripple animation
                root.setOnClickListener { 
                    animateClick(root) {
                        onTransactionClick(transaction)
                    }
                }
                
                // Set amount with currency formatting
                tvAmount.text = transaction.getFormattedAmount()
                
                // Set transaction type badge
                tvTransactionType.text = transaction.getTransactionTypeDisplay()
                
                // Set receiver/sender name
                tvReceiverSenderName.text = transaction.getShortName()
                
                // Set time with format preference
                tvTime.text = DateTimeFormatter.formatTime(transaction.transactionTime, timeFormat)
                
                // Set tag or "Add Tag" text
                tvTag.text = transaction.getDisplayTag()
                
                // Apply color coding based on transaction type
                applyColorCoding(transaction.transactionType)
                
                // Show/hide untagged indicator
                viewUntaggedIndicator.visibility = if (transaction.needsTag()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                
                // Set tag text color based on tagged status
                tvTag.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        if (transaction.needsTag()) R.color.untagged_amber else R.color.text_secondary
                    )
                )
            }
        }
        
        /**
         * Animate click with scale down and up effect
         */
        private fun animateClick(view: View, onClick: () -> Unit) {
            val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.98f)
            val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.98f)
            val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.98f, 1.0f)
            val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.98f, 1.0f)
            
            scaleDown.duration = 100
            scaleDownY.duration = 100
            scaleUp.duration = 100
            scaleUpY.duration = 100
            
            val downSet = AnimatorSet()
            downSet.playTogether(scaleDown, scaleDownY)
            
            val upSet = AnimatorSet()
            upSet.playTogether(scaleUp, scaleUpY)
            
            downSet.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    upSet.start()
                    onClick()
                }
            })
            
            downSet.start()
        }
        
        /**
         * Apply color coding based on transaction type (debit = red, credit = green)
         */
        private fun applyColorCoding(transactionType: TransactionType) {
            val context = binding.root.context
            
            when (transactionType) {
                TransactionType.DEBIT -> {
                    // Red color scheme for debit transactions
                    binding.viewLeftBorder.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.debit_red)
                    )
                    binding.tvAmount.setTextColor(
                        ContextCompat.getColor(context, R.color.debit_red)
                    )
                    binding.tvTransactionType.setBackgroundResource(R.drawable.debit_badge_background)
                }
                TransactionType.CREDIT -> {
                    // Green color scheme for credit transactions
                    binding.viewLeftBorder.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.credit_green)
                    )
                    binding.tvAmount.setTextColor(
                        ContextCompat.getColor(context, R.color.credit_green)
                    )
                    binding.tvTransactionType.setBackgroundResource(R.drawable.credit_badge_background)
                }
            }
        }
    }
}

/**
 * DiffUtil callback for efficient list updates
 */
class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}