package com.expensetracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.expensetracker.R
import com.expensetracker.data.model.Transaction
import com.expensetracker.ui.detail.TransactionDetailActivity
import com.expensetracker.utils.Constants

/**
 * Helper class for managing transaction notifications
 * Creates high-priority notifications with action buttons and deep linking
 */
class TransactionNotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "transaction_notifications"
        private const val CHANNEL_NAME = "Transaction Notifications"
    }

    init {
        createNotificationChannel()
    }

    /**
     * Show notification for new transaction capture
     */
    fun showNewTransactionNotification(transaction: Transaction) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create intent to open transaction detail
        val detailIntent = Intent(context, TransactionDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_TRANSACTION_ID, transaction.id)
            putExtra(Constants.EXTRA_FROM_NOTIFICATION, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            transaction.id.toInt(),
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create "Add Tag" action intent
        val addTagIntent = Intent(context, TransactionDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_TRANSACTION_ID, transaction.id)
            putExtra(Constants.EXTRA_FROM_NOTIFICATION, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val addTagPendingIntent = PendingIntent.getActivity(
            context,
            transaction.id.toInt() + 1000,
            addTagIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(
                context.getString(
                    R.string.notification_content,
                    transaction.amount,
                    transaction.receiverSenderName
                )
            )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_tag,
                context.getString(R.string.notification_action_add_tag),
                addTagPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setColor(
                if (transaction.transactionType.isDebit()) {
                    context.getColor(R.color.debit_red)
                } else {
                    context.getColor(R.color.credit_green)
                }
            )
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_NEW_TRANSACTION, notification)
    }

    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new transaction captures"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}