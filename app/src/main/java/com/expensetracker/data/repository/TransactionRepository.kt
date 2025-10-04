package com.expensetracker.data.repository

import android.util.Log
import com.expensetracker.data.database.TransactionDao
import com.expensetracker.data.database.toEntity
import com.expensetracker.data.database.toTransaction
import com.expensetracker.data.model.Transaction
import com.expensetracker.sms.SmsParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val smsParser: SmsParser
) {

    companion object {
        private const val TAG = "TransactionRepository"
    }

    // ========== Query Operations ==========

    fun getTodayTransactions(): Flow<List<Transaction>> {
        return transactionDao.getTodayTransactions().map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getUntaggedTransactions(): Flow<List<Transaction>> {
        return transactionDao.getUntaggedTransactions().map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun searchTransactions(query: String): Flow<List<Transaction>> {
        return transactionDao.searchTransactions(query).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getTransactionsByType(type: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getTransactionsPaged(limit: Int, offset: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsPaged(limit, offset).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)?.toTransaction()
    }

    fun getUntaggedCount(): Flow<Int> {
        return transactionDao.getUntaggedCount()
    }

    // ========== Mutation Operations ==========

    suspend fun insertTransaction(transaction: Transaction): Long {
        return try {
            val id = transactionDao.insertTransaction(transaction.toEntity())
            Log.d(TAG, "Transaction inserted with ID: $id")
            id
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting transaction: ${e.message}", e)
            throw e
        }
    }

    suspend fun insertTransactions(transactions: List<Transaction>): List<Long> {
        return try {
            val entities = transactions.map { it.toEntity() }
            val ids = transactionDao.insertTransactions(entities)
            Log.d(TAG, "Inserted ${ids.size} transactions")
            ids
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting transactions: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        try {
            transactionDao.updateTransaction(transaction.toEntity())
            Log.d(TAG, "Transaction updated: ${transaction.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating transaction: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateTransactionTag(transactionId: Long, tag: String?) {
        try {
            val isTagged = !tag.isNullOrBlank()
            transactionDao.updateTransactionTag(transactionId, tag, isTagged)
            Log.d(TAG, "Transaction tag updated: ID=$transactionId, tag=$tag, isTagged=$isTagged")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating transaction tag: ${e.message}", e)
            throw e
        }
    }

    suspend fun deleteTransaction(id: Long) {
        try {
            transactionDao.deleteTransaction(id)
            Log.d(TAG, "Transaction deleted: $id")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting transaction: ${e.message}", e)
            throw e
        }
    }

    suspend fun deleteAllTransactions() {
        try {
            transactionDao.deleteAllTransactions()
            Log.d(TAG, "All transactions deleted")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all transactions: ${e.message}", e)
            throw e
        }
    }

    // ========== SMS Processing Operations ==========

    suspend fun processSmsTransaction(smsBody: String): Transaction? {
        return try {
            Log.d(TAG, "Processing SMS transaction")
            val parsedTransaction = smsParser.parseTransaction(smsBody)
            if (parsedTransaction == null) {
                Log.w(TAG, "Failed to parse SMS transaction")
                return null
            }
            val id = insertTransaction(parsedTransaction)
            val savedTransaction = parsedTransaction.copy(id = id)
            Log.d(TAG, "SMS transaction processed successfully: ${savedTransaction.receiverSenderName}")
            savedTransaction
        } catch (e: Exception) {
            Log.e(TAG, "Error processing SMS transaction: ${e.message}", e)
            null
        }
    }

    fun isValidAxisBankSms(sender: String, body: String): Boolean {
        return smsParser.isValidAxisBankSender(sender) &&
               smsParser.containsTransactionKeywords(body)
    }

    // ========== Data Export Operations ==========

    suspend fun getAllTransactionsForExport(): List<Transaction> {
        return try {
            // Collect the first (and only) list from the Flow
            transactionDao.getAllTransactions().first().map { it.toTransaction() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting transactions for export: ${e.message}", e)
            emptyList()
        }
    }
}