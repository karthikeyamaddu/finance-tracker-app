package com.expensetracker

import com.expensetracker.data.model.EntryMethod
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import com.expensetracker.sms.SmsParser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.system.measureTimeMillis

/**
 * Performance tests to ensure the app can handle large datasets efficiently
 */
class PerformanceTest {
    
    private lateinit var smsParser: SmsParser
    
    @Before
    fun setup() {
        smsParser = SmsParser()
    }
    
    @Test
    fun `test SMS parsing performance with multiple messages`() {
        val sampleSms = """
            INR 150.00 debited
            A/c no. XX3248
            02-10-25, 20:05:59
            UPI/P2M/527537387973/MANGARAM CHOWDARY
            Axis Bank
        """.trimIndent()
        
        val iterations = 1000
        
        val timeMillis = measureTimeMillis {
            repeat(iterations) {
                val transaction = smsParser.parseTransaction(sampleSms)
                assertNotNull("Transaction should be parsed successfully", transaction)
            }
        }
        
        // Should parse 1000 SMS messages in under 5 seconds
        assertTrue("SMS parsing should be fast (${timeMillis}ms for $iterations iterations)", timeMillis < 5000)
        
        // Average time per SMS should be under 5ms
        val avgTimePerSms = timeMillis.toDouble() / iterations
        assertTrue("Average parsing time should be under 5ms (actual: ${avgTimePerSms}ms)", avgTimePerSms < 5.0)
    }
    
    @Test
    fun `test transaction list performance with large dataset`() {
        // Create a large list of transactions
        val transactions = mutableListOf<Transaction>()
        val transactionCount = 10000
        
        val creationTime = measureTimeMillis {
            repeat(transactionCount) { index ->
                transactions.add(
                    Transaction(
                        id = index.toLong(),
                        amount = (index % 1000) + 1.0,
                        transactionType = if (index % 2 == 0) TransactionType.DEBIT else TransactionType.CREDIT,
                        accountNumber = "XX3248",
                        transactionDate = LocalDate.now().minusDays((index % 365).toLong()),
                        transactionTime = LocalTime.now(),
                        receiverSenderName = "Test User $index",
                        upiReference = "UPI/P2M/$index/TEST",
                        bankName = "Axis Bank",
                        userTag = if (index % 3 == 0) "Tag $index" else null,
                        isTagged = index % 3 == 0,
                        entryMethod = EntryMethod.SMS,
                        rawSmsText = "Test SMS $index",
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
        
        // Should create 10,000 transactions in under 1 second
        assertTrue("Transaction creation should be fast (${creationTime}ms)", creationTime < 1000)
        
        // Test filtering performance
        val filterTime = measureTimeMillis {
            val untaggedTransactions = transactions.filter { !it.isTagged }
            val debitTransactions = transactions.filter { it.transactionType == TransactionType.DEBIT }
            val recentTransactions = transactions.filter { 
                it.transactionDate.isAfter(LocalDate.now().minusDays(30)) 
            }
            
            assertTrue("Should have untagged transactions", untaggedTransactions.isNotEmpty())
            assertTrue("Should have debit transactions", debitTransactions.isNotEmpty())
            assertTrue("Should have recent transactions", recentTransactions.isNotEmpty())
        }
        
        // Filtering should be fast even with large dataset
        assertTrue("Filtering should be fast (${filterTime}ms)", filterTime < 500)
    }
    
    @Test
    fun `test search performance with large dataset`() {
        // Create transactions with searchable names
        val transactions = mutableListOf<Transaction>()
        val searchTerms = listOf("GROCERY", "FUEL", "RESTAURANT", "SHOPPING", "TRANSPORT")
        
        repeat(5000) { index ->
            transactions.add(
                Transaction(
                    id = index.toLong(),
                    amount = 100.0,
                    transactionType = TransactionType.DEBIT,
                    accountNumber = "XX3248",
                    transactionDate = LocalDate.now(),
                    transactionTime = LocalTime.now(),
                    receiverSenderName = "${searchTerms[index % searchTerms.size]} STORE $index",
                    upiReference = "UPI/P2M/$index/TEST",
                    bankName = "Axis Bank",
                    userTag = null,
                    isTagged = false,
                    entryMethod = EntryMethod.SMS,
                    rawSmsText = "Test SMS $index",
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        
        // Test search performance
        val searchTime = measureTimeMillis {
            val groceryResults = transactions.filter { 
                it.receiverSenderName.contains("GROCERY", ignoreCase = true) 
            }
            val fuelResults = transactions.filter { 
                it.receiverSenderName.contains("FUEL", ignoreCase = true) 
            }
            
            assertTrue("Should find grocery transactions", groceryResults.isNotEmpty())
            assertTrue("Should find fuel transactions", fuelResults.isNotEmpty())
        }
        
        // Search should be fast
        assertTrue("Search should be fast (${searchTime}ms)", searchTime < 200)
    }
    
    @Test
    fun `test memory usage with large transaction list`() {
        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()
        
        // Create a large list of transactions
        val transactions = mutableListOf<Transaction>()
        repeat(50000) { index ->
            transactions.add(
                Transaction(
                    id = index.toLong(),
                    amount = 100.0,
                    transactionType = TransactionType.DEBIT,
                    accountNumber = "XX3248",
                    transactionDate = LocalDate.now(),
                    transactionTime = LocalTime.now(),
                    receiverSenderName = "Test User $index",
                    upiReference = "UPI/P2M/$index/TEST",
                    bankName = "Axis Bank",
                    userTag = null,
                    isTagged = false,
                    entryMethod = EntryMethod.SMS,
                    rawSmsText = "Test SMS $index",
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        
        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsed = finalMemory - initialMemory
        
        // Memory usage should be reasonable (less than 100MB for 50k transactions)
        val memoryUsedMB = memoryUsed / (1024 * 1024)
        assertTrue("Memory usage should be reasonable (${memoryUsedMB}MB)", memoryUsedMB < 100)
        
        // Cleanup
        transactions.clear()
        System.gc()
    }
}