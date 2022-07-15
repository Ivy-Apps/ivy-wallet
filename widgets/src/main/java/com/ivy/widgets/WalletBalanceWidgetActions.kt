package com.ivy.widgets

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.ivy.base.GlobalProvider
import com.ivy.data.transaction.TransactionType

class WalletBalanceButtonsAction : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        when (parameters[walletBtnActParam]) {
            AddTransactionWidgetClick.ACTION_ADD_INCOME -> {
                context.startActivity(
                    GlobalProvider.rootIntent.addTransactionStart(
                        context = context,
                        type = TransactionType.INCOME
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            AddTransactionWidgetClick.ACTION_ADD_EXPENSE -> {
                context.startActivity(
                    GlobalProvider.rootIntent.addTransactionStart(
                        context = context,
                        type = TransactionType.EXPENSE
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            AddTransactionWidgetClick.ACTION_ADD_TRANSFER -> {
                context.startActivity(
                    GlobalProvider.rootIntent.addTransactionStart(
                        context = context,
                        type = TransactionType.TRANSFER
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            else -> return
        }
    }
}

class WalletBalanceWidgetClickAction : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        context.startActivity(
            GlobalProvider.rootIntent.getIntent(
                context = context
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}

val walletBtnActParam = ActionParameters.Key<String>("wallet_balance_button_action")