package com.expensetracker.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database class for the Expense Tracker application
 * Manages the SQLite database and provides access to DAOs
 */
@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {
    
    /**
     * Provides access to transaction operations
     */
    abstract fun transactionDao(): TransactionDao
    
    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null
        
        /**
         * Gets the singleton database instance
         * Uses double-checked locking pattern for thread safety
         */
        fun getDatabase(context: Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Database callback for initialization
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Database created - can add initial data here if needed
            }
        }
        
        /**
         * Migration from version 1 to 2 (for future use)
         * Example migration - not needed for initial version
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add migration logic here when needed
                // Example: database.execSQL("ALTER TABLE transactions ADD COLUMN newColumn TEXT")
            }
        }
    }
}

/**
 * Type converters for Room database
 * Handles conversion of complex types to/from database storage
 */
class Converters {
    
    /**
     * Convert timestamp to Long for database storage
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Long? {
        return value
    }
}