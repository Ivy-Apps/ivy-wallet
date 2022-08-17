package com.ivy.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.IdRes
import com.ivy.data.transaction.TrnType

class AddTransactionWidgetClick {
    companion object {
        const val ACTION_ADD_INCOME = "com.ivy.wallet.ACTION_ADD_INCOME"
        const val ACTION_ADD_EXPENSE = "com.ivy.wallet.ACTION_ADD_EXPENSE"
        const val ACTION_ADD_TRANSFER = "com.ivy.wallet.ACTION_ADD_TRANSFER"
    }

    //============================= <HANDLE> =======================================================
    fun handleClick(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ADD_INCOME -> {
                context.startActivity(
                    com.ivy.core.ui.temp.GlobalProvider.rootIntent.addTransactionStart(
                        context = context,
                        type = TrnType.INCOME
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            ACTION_ADD_EXPENSE -> {
                context.startActivity(
                    com.ivy.core.ui.temp.GlobalProvider.rootIntent.addTransactionStart(
                        context = context,
                        type = TrnType.EXPENSE
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            ACTION_ADD_TRANSFER -> {
                context.startActivity(
                    com.ivy.core.ui.temp.GlobalProvider.rootIntent.addTransactionStart(
                        context = context,
                        type = TrnType.TRANSFER
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            else -> return
        }
    }

    //============================= <HANDLE> =======================================================
    //------------------------------ <SETUP> -------------------------------------------------------
    class Setup(
        private val context: Context,
        private val rv: RemoteViews,
        private val appWidgetId: Int
    ) {
        fun clickListener(@IdRes viewId: Int, action: String) {
            val actionIntent = newActionIntent(context, appWidgetId, action)
            rv.setOnClickPendingIntent(viewId, actionIntent)
        }

        private fun newActionIntent(
            context: Context,
            appWidgetId: Int,
            action: String
        ): PendingIntent {
            val intent = Intent(context, AddTransactionWidget::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.action = action
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    } //----------------------------- </SETUP> -------------------------------------------------------
}