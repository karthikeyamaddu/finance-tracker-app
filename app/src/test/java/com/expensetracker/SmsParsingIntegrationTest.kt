package com.expensetracker

import com.expensetracker.data.model.EntryMethod
import com.expensetracker.data.model.TransactionType
import com.expensetracker.sms.SmsParser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

/**
 * Integration test for SMS parsing functionality
 * Tests the complete SMS-to-Transaction workflow
 */
class SmsParsingIntegrationTest {
    
    private lateinit var smsParser: SmsParser
    
    @Before
    fun setup() {
        smsParser = SmsParser()
    }
    
    @Test
    fun `test debit SMS parsing - complete workflow`() {
        // Sample debit SMS from Axis Bank
        val debitSms = """
            INR 150.00 debited
            A/c no. XX3248
            02-10-25, 20:05:59
            UPI/P2M/527537387973/MANGARAM CHOWDARY
            Not you? SMS BLOCKUPI Cust ID to 91XXXXXXXX
            Axis Bank
        """.trimIndent()
        
        // Parse the SMS
        val transaction = smsParser.parseTransaction(debitSms)
        
        // Verify transaction is not null
        assertNotNull("Transaction should not be null", transaction)
        
        // Verify all fields are correctly parsed
        transaction?.let { txn ->
            assertEquals("Amount should be 150.00", 150.00, txn.amount, 0.01)
            assertEquals("Transaction type should be DEBIT", TransactionType.DEBIT, txn.transactionType)
            assertEquals("Account number should be XX3248", "XX3248", txn.accountNumber)
            assertEquals("Receiver name should be MANGARAM CHOWDARY", "MANGARAM CHOWDARY", txn.receiverSenderName)
            assertEquals("UPI reference should match", "UPI/P2M/527537387973/MANGARAM CHOWDARY", txn.upiReference)
            assertEquals("Bank name should be Axis Bank", "Axis Bank", txn.bankName)
            assertEquals("Entry method should be SMS", EntryMethod.SMS, txn.entryMethod)
            assertEquals("Should be initially untagged", false, txn.isTagged)
            assertNull("User tag should be null initially", txn.userTag)
            assertEquals("Raw SMS should be stored", debitSms, txn.rawSmsText)
        }
    }
    
    @Test
    fun `test credit SMS parsing - complete workflow`() {
        // Sample credit SMS from Axis Bank
        val creditSms = """
            INR 5000.00 credited
            A/c no. XX3248
            28-09-25, 20:05:06 IST
            UPI/P2A/512654122901/MADDU REV/AXIS BANK - Axis Bank
        """.trimIndent()
        
        // Parse the SMS
        val transaction = smsParser.parseTransaction(creditSms)
        
        // Verify transaction is not null
        assertNotNull("Transaction should not be null", transaction)
        
        // Verify all fields are correctly parsed
        transaction?.let { txn ->
            assertEquals("Amount should be 5000.00", 5000.00, txn.amount, 0.01)
            assertEquals("Transaction type should be CREDIT", TransactionType.CREDIT, txn.transactionType)
            assertEquals("Account number should be XX3248", "XX3248", txn.accountNumber)
            assertEquals("Sender name should be MADDU REV", "MADDU REV", txn.receiverSenderName)
            assertEquals("UPI reference should match", "UPI/P2A/512654122901/MADDU REV/AXIS BANK", txn.upiReference)
            assertEquals("Bank name should be Axis Bank", "Axis Bank", txn.bankName)
            assertEquals("Entry method should be SMS", EntryMethod.SMS, txn.entryMethod)
        }
    }
    
    @Test
    fun `test invalid SMS handling`() {
        // Invalid SMS that doesn't match Axis Bank format
        val invalidSms = "This is not a valid transaction SMS"
        
        // Parse the SMS
        val transaction = smsParser.parseTransaction(invalidSms)
        
        // Verify transaction is null for invalid SMS
        assertNull("Transaction should be null for invalid SMS", transaction)
    }
    
    @Test
    fun `test sender validation`() {
        // Test valid Axis Bank sender patterns
        assertTrue("AD-AXISBK-S should be valid", smsParser.isValidAxisBankSender("AD-AXISBK-S"))
        assertTrue("JD-AXISBK-S should be valid", smsParser.isValidAxisBankSender("JD-AXISBK-S"))
        assertTrue("CP-AXISBK-S should be valid", smsParser.isValidAxisBankSender("CP-AXISBK-S"))
        
        // Test invalid sender patterns
        assertFalse("Random sender should be invalid", smsParser.isValidAxisBankSender("RANDOM-SENDER"))
        assertFalse("Empty sender should be invalid", smsParser.isValidAxisBankSender(""))
        assertFalse("Wrong format should be invalid", smsParser.isValidAxisBankSender("AXISBK"))
    }
    
    @Test
    fun `test transaction keywords detection`() {
        // Test SMS with transaction keywords
        val validSms = "INR 100.00 debited from your account"
        assertTrue("Should detect transaction keywords", smsParser.containsTransactionKeywords(validSms))
        
        // Test SMS without transaction keywords
        val invalidSms = "Your OTP is 123456"
        assertFalse("Should not detect transaction keywords", smsParser.containsTransactionKeywords(invalidSms))
    }
    
    @Test
    fun `test date and time parsing accuracy`() {
        val sms = """
            INR 100.00 debited
            A/c no. XX3248
            15-12-24, 14:30:45
            UPI/P2M/123456789/TEST USER
            Axis Bank
        """.trimIndent()
        
        val transaction = smsParser.parseTransaction(sms)
        
        assertNotNull("Transaction should not be null", transaction)
        transaction?.let { txn ->
            // Verify date parsing (15-12-24 should become 2024-12-15)
            assertEquals("Year should be 2024", 2024, txn.transactionDate.year)
            assertEquals("Month should be 12", 12, txn.transactionDate.monthValue)
            assertEquals("Day should be 15", 15, txn.transactionDate.dayOfMonth)
            
            // Verify time parsing (14:30:45)
            assertEquals("Hour should be 14", 14, txn.transactionTime.hour)
            assertEquals("Minute should be 30", 30, txn.transactionTime.minute)
            assertEquals("Second should be 45", 45, txn.transactionTime.second)
        }
    }
}