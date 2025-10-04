package com.expensetracker.ui.manual

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.expensetracker.R
import com.expensetracker.data.model.EntryMethod
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import com.expensetracker.databinding.DialogManualEntryBinding
import com.expensetracker.utils.Constants
import com.expensetracker.utils.CurrencyFormatter
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Dialog for manual transaction entry
 * Provides form validation and date/time pickers
 */
class ManualEntryDialog(private val onSave: (Transaction) -> Unit) : DialogFragment() {
    
    private var _binding: DialogManualEntryBinding? = null
    private val binding get() = _binding!!
    
    private var selectedDate: LocalDate = LocalDate.now()
    private var selectedTime: LocalTime = LocalTime.now()
    private var selectedTransactionType: TransactionType = TransactionType.DEBIT
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogManualEntryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupClickListeners()
        setDefaultValues()
    }
    
    private fun setupUI() {
        // Set dialog to be full width
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    
    private fun setupClickListeners() {
        // Transaction type toggle
        binding.toggleGroupTransactionType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedTransactionType = when (checkedId) {
                    R.id.btnDebit -> TransactionType.DEBIT
                    R.id.btnCredit -> TransactionType.CREDIT
                    else -> TransactionType.DEBIT
                }
                updateTransactionTypeColors()
            }
        }
        
        // Date picker
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        
        // Time picker
        binding.etTime.setOnClickListener {
            showTimePicker()
        }
        
        // Cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        // Save button
        binding.btnSave.setOnClickListener {
            if (validateAndSave()) {
                dismiss()
            }
        }
    }
    
    private fun setDefaultValues() {
        // Set default transaction type to DEBIT
        binding.btnDebit.isChecked = true
        selectedTransactionType = TransactionType.DEBIT
        updateTransactionTypeColors()
        
        // Set current date and time
        updateDateDisplay()
        updateTimeDisplay()
    }
    
    private fun updateTransactionTypeColors() {
        when (selectedTransactionType) {
            TransactionType.DEBIT -> {
                binding.btnDebit.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
                binding.btnDebit.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.debit_red))
                binding.btnCredit.setTextColor(ContextCompat.getColor(requireContext(), R.color.credit_green))
                binding.btnCredit.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            }
            TransactionType.CREDIT -> {
                binding.btnCredit.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
                binding.btnCredit.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.credit_green))
                binding.btnDebit.setTextColor(ContextCompat.getColor(requireContext(), R.color.debit_red))
                binding.btnDebit.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            }
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
        
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun showTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedTime = LocalTime.of(hourOfDay, minute)
                updateTimeDisplay()
            },
            selectedTime.hour,
            selectedTime.minute,
            true // 24-hour format
        ).show()
    }
    
    private fun updateDateDisplay() {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        binding.etDate.setText(selectedDate.format(formatter))
    }
    
    private fun updateTimeDisplay() {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        binding.etTime.setText(selectedTime.format(formatter))
    }
    
    private fun validateAndSave(): Boolean {
        // Clear previous errors
        binding.tilAmount.error = null
        binding.tilReceiverSenderName.error = null
        
        var isValid = true
        
        // Validate amount
        val amountText = binding.etAmount.text?.toString()?.trim()
        if (amountText.isNullOrBlank()) {
            binding.tilAmount.error = Constants.ERROR_AMOUNT_REQUIRED
            isValid = false
        } else {
            val amount = CurrencyFormatter.parseAmount(amountText)
            if (amount == null || amount <= 0) {
                binding.tilAmount.error = Constants.ERROR_AMOUNT_INVALID
                isValid = false
            } else if (amount < Constants.MIN_AMOUNT || amount > Constants.MAX_AMOUNT) {
                binding.tilAmount.error = Constants.ERROR_AMOUNT_INVALID
                isValid = false
            }
        }
        
        // Validate receiver/sender name
        val nameText = binding.etReceiverSenderName.text?.toString()?.trim()
        if (nameText.isNullOrBlank()) {
            binding.tilReceiverSenderName.error = Constants.ERROR_NAME_REQUIRED
            isValid = false
        } else if (nameText.length > Constants.MAX_NAME_LENGTH) {
            binding.tilReceiverSenderName.error = Constants.ERROR_NAME_TOO_LONG
            isValid = false
        }
        
        // Validate tag length (optional field)
        val tagText = binding.etTag.text?.toString()?.trim()
        if (!tagText.isNullOrBlank() && tagText.length > Constants.MAX_TAG_LENGTH) {
            binding.tilTag.error = Constants.ERROR_TAG_TOO_LONG
            isValid = false
        }
        
        if (!isValid) {
            return false
        }
        
        // Create transaction object
        val amount = CurrencyFormatter.parseAmount(amountText!!)!!
        val transaction = Transaction(
            id = 0, // Will be set by database
            amount = amount,
            transactionType = selectedTransactionType,
            accountNumber = "XX3248", // Default account
            transactionDate = selectedDate,
            transactionTime = selectedTime,
            receiverSenderName = nameText!!,
            upiReference = null, // Manual entries don't have UPI reference
            bankName = "Manual Entry",
            userTag = if (tagText.isNullOrBlank()) null else tagText,
            isTagged = !tagText.isNullOrBlank(),
            entryMethod = EntryMethod.MANUAL,
            rawSmsText = null,
            createdAt = System.currentTimeMillis()
        )
        
        // Call the save callback
        onSave(transaction)
        
        return true
    }
    
    private fun showSnackbar(message: String) {
        view?.let { 
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}