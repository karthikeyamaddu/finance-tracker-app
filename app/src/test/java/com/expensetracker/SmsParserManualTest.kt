package com.expensetracker

import com.expensetracker.data.model.EntryMethod
import com.expensetracker.data.model.TransactionType
import com.expensetracker.sms.SmsParser

/**
 * Manual test class to verify SMS parsing functionality
 * Run this to debug SMS parsing issues
 */
class SmsParserManualTest {
    
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val parser = SmsParser()
            
            println("=== SMS Parser Manual Test ===")
            
            // Test 1: Debit SMS
            println("\n--- Test 1: Debit SMS ---")
            val debitSms = """
                INR 150.00 debited
                A/c no. XX3248
                02-10-25, 20:05:59
                UPI/P2M/527537387973/MANGARAM CHOWDARY
                Not you? SMS BLOCKUPI Cust ID to 91XXXXXXXX
                Axis Bank
            """.trimIndent()
            
            println("Input SMS:")
            println(debitSms)
            println("\nParsing result:")
            val debitTransaction = parser.parseTransaction(debitSms)
            if (debitTransaction != null) {
                println("✓ Successfully parsed!")
                println("Amount: ${debitTransaction.amount}")
                println("Type: ${debitTransaction.transactionType}")
                println("Account: ${debitTransaction.accountNumber}")
                println("Date: ${debitTransaction.transactionDate}")
                println("Time: ${debitTransaction.transactionTime}")
                println("Name: ${debitTransaction.receiverSenderName}")
                println("UPI Ref: ${debitTransaction.upiReference}")
                println("Bank: ${debitTransaction.bankName}")
                println("Entry Method: ${debitTransaction.entryMethod}")
            } else {
                println("✗ Failed to parse!")
            }
            
            // Test 2: Credit SMS
            println("\n--- Test 2: Credit SMS ---")
            val creditSms = """
                INR 5000.00 credited
                A/c no. XX3248
                28-09-25, 20:05:06 IST
                UPI/P2A/512654122901/MADDU REV/AXIS BANK - Axis Bank
            """.trimIndent()
            
            println("Input SMS:")
            println(creditSms)
            println("\nParsing result:")
            val creditTransaction = parser.parseTransaction(creditSms)
            if (creditTransaction != null) {
                println("✓ Successfully parsed!")
                println("Amount: ${creditTransaction.amount}")
                println("Type: ${creditTransaction.transactionType}")
                println("Account: ${creditTransaction.accountNumber}")
                println("Date: ${creditTransaction.transactionDate}")
                println("Time: ${creditTransaction.transactionTime}")
                println("Name: ${creditTransaction.receiverSenderName}")
                println("UPI Ref: ${creditTransaction.upiReference}")
                println("Bank: ${creditTransaction.bankName}")
                println("Entry Method: ${creditTransaction.entryMethod}")
            } else {
                println("✗ Failed to parse!")
            }
            
            // Test 3: Invalid SMS
            println("\n--- Test 3: Invalid SMS ---")
            val invalidSms = "This is not a valid transaction SMS"
            println("Input SMS: $invalidSms")
            val invalidTransaction = parser.parseTransaction(invalidSms)
            if (invalidTransaction == null) {
                println("✓ Correctly rejected invalid SMS")
            } else {
                println("✗ Incorrectly parsed invalid SMS")
            }
            
            // Test 4: Sender validation
            println("\n--- Test 4: Sender Validation ---")
            val validSenders = listOf("AD-AXISBK-S", "JD-AXISBK-S", "CP-AXISBK-S")
            val invalidSenders = listOf("RANDOM-SENDER", "", "AXISBK", "AD-AXISBK")
            
            println("Valid senders:")
            validSenders.forEach { sender ->
                val isValid = parser.isValidAxisBankSender(sender)
                println("$sender: ${if (isValid) "✓" else "✗"}")
            }
            
            println("Invalid senders:")
            invalidSenders.forEach { sender ->
                val isValid = parser.isValidAxisBankSender(sender)
                println("$sender: ${if (!isValid) "✓" else "✗"}")
            }
            
            // Test 5: Keyword detection
            println("\n--- Test 5: Keyword Detection ---")
            val validKeywords = listOf(
                "INR 100.00 debited from your account",
                "INR 500.00 credited to your account"
            )
            val invalidKeywords = listOf(
                "Your OTP is 123456",
                "Account balance inquiry"
            )
            
            println("Valid keyword SMS:")
            validKeywords.forEach { sms ->
                val hasKeywords = parser.containsTransactionKeywords(sms)
                println("'$sms': ${if (hasKeywords) "✓" else "✗"}")
            }
            
            println("Invalid keyword SMS:")
            invalidKeywords.forEach { sms ->
                val hasKeywords = parser.containsTransactionKeywords(sms)
                println("'$sms': ${if (!hasKeywords) "✓" else "✗"}")
            }
            
            println("\n=== Test Complete ===")
        }
    }
}