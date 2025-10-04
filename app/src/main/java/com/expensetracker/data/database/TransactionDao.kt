package com.expensetracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Transaction operations
 * Provides methods for all database operations on transactions
 */
@Dao
interface TransactionDao {
    
    /**
     * Get today's transactions ordered by time (newest first)
     * Uses SQLite date functions to filter by current date
     */
    @Query("""
        SELECT * FROM transactions 
        WHERE DATE(transactionDate) = DATE('now', 'localtime') 
        ORDER BY transactionDate DESC, transactionTime DESC
    """)
    fun getTodayTransactions(): Flow<List<TransactionEntity>>
    
    /**
     * Get all untagged transactions (is_tagged = 0)
     * Ordered by date and time (newest first)
     */
    @Query("""
        SELECT * FROM transactions 
        WHERE isTagged = 0 
        ORDER BY transactionDate DESC, transactionTime DESC
    """)
    fun getUntaggedTransactions(): Flow<List<TransactionEntity>>
    
    /**
     * Get all transactions ordered by date and time (newest first)
     */
    @Query("""
        SELECT * FROM transactions 
        ORDER BY transactionDate DESC, transactionTime DESC
    """)
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    /**
     * Search transactions by receiver/sender name or user tag
     * Case-insensitive search using LIKE operator
     */
    @Query("""
        SELECT * FROM transactions 
        WHERE receiverSenderName LIKE '%' || :query || '%' 
           OR userTag LIKE '%' || :query || '%'
        ORDER BY transactionDate DESC, transactionTime DESC
    """)
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>
    
    /**
     * Get transactions filtered by type (DEBIT or CREDIT)
     */
    @Query("""
        SELECT * FROM transactions 
        WHERE transactionType = :type 
        ORDER BY transactionDate DESC, transactionTime DESC
    """)
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>
    
    /**
     * Get transactions within a date range
     */
    @Query("""
        SELECT * FROM transactions 
        WHERE transactionDate BETWEEN :startDate AND :endDate 
        ORDER BY transactionDate DESC, transactionTime DESC
    """)
    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<TransactionEntity>>
    
    /**
     * Get transactions grouped by month for history view
     * Returns transactions for pagination support
     */
    @Query("""
        SELECT * FROM transactions 
        ORDER BY transactionDate DESC, transactionTime DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun getTransactionsPaged(limit: Int, offset: Int): Flow<List<TransactionEntity>>
    
    /**
     * Get transaction by ID for detail view
     */
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?
    
    /**
     * Get count of untagged transactions for badge display
     */
    @Query("SELECT COUNT(*) FROM transactions WHERE isTagged = 0")
    fun getUntaggedCount(): Flow<Int>
    
    /**
     * Insert a new transaction and return the generated ID
     */
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long
    
    /**
     * Insert multiple transactions (useful for bulk operations)
     */
    @Insert
    suspend fun insertTransactions(transactions: List<TransactionEntity>): List<Long>
    
    /**
     * Update an existing transaction
     */
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
    
    /**
     * Update only the tag and tagged status of a transaction
     * More efficient than updating the entire entity
     */
    @Query("""
        UPDATE transactions 
        SET userTag = :tag, isTagged = :isTagged 
        WHERE id = :id
    """)
    suspend fun updateTransactionTag(id: Long, tag: String?, isTagged: Boolean)
    
    /**
     * Delete a transaction by ID
     */
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)
    
    /**
     * Delete all transactions (for data clearing functionality)
     */
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
    
    /**
     * Get transactions by entry method (SMS or MANUAL)
     */
    @Query("""
        SELECT * FROM transactions 
        WHERE entryMethod = :method 
        ORDER BY transactionDate DESC, transactionTime DESC
    """)
    fun getTransactionsByEntryMethod(method: String): Flow<List<TransactionEntity>>
}