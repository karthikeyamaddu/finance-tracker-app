package com.expensetracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.expensetracker.data.model.EntryMethod
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Room entity representing a transaction in the database
 * Maps to the transactions table with all required fields
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val amount: Double,
    
    val transactionType: String, // "DEBIT" or "CREDIT"
    
    val accountNumber: String = "XX3248",
    
    val transactionDate: String, // YYYY-MM-DD format
    
    val transactionTime: String, // HH:MM:SS format
    
    val receiverSenderName: String,
    
    val upiReference: String?,
    
    val bankName: String = "Axis Bank",
    
    val userTag: String?, // User's custom label (nullable)
    
    val isTagged: Boolean = false, // 0 = not tagged, 1 = tagged
    
    val entryMethod: String, // "SMS" or "MANUAL"
    
    val rawSmsText: String?, // Store original SMS for debugging
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Extension function to convert TransactionEntity to domain Transaction model
 */
fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        transactionType = TransactionType.valueOf(transactionType),
        accountNumber = accountNumber,
        transactionDate = LocalDate.parse(transactionDate, DateTimeFormatter.ISO_LOCAL_DATE),
        transactionTime = LocalTime.parse(transactionTime, DateTimeFormatter.ISO_LOCAL_TIME),
        receiverSenderName = receiverSenderName,
        upiReference = upiReference,
        bankName = bankName,
        userTag = userTag,
        isTagged = isTagged,
        entryMethod = EntryMethod.valueOf(entryMethod),
        rawSmsText = rawSmsText,
        createdAt = createdAt
    )
}

/**
 * Extension function to convert domain Transaction model to TransactionEntity
 */
fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        transactionType = transactionType.name,
        accountNumber = accountNumber,
        transactionDate = transactionDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
        transactionTime = transactionTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
        receiverSenderName = receiverSenderName,
        upiReference = upiReference,
        bankName = bankName,
        userTag = userTag,
        isTagged = isTagged,
        entryMethod = entryMethod.name,
        rawSmsText = rawSmsText,
        createdAt = createdAt
    )
}