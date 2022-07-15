package com.ivy.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class AddTransactionWidgetCompact : AppWidgetProvider() {

    companion object {
        fun updateBroadcast(context: Context) {
            WidgetBase.updateBroadcast(context, AddTransactionWidgetCompact::class.java)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        updateBroadcast(context)
    }

    //--------------------------- </BROADCASTS> ----------------------------------------------------
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val rv = RemoteViews(context.packageName, R.layout.widget_add_transaction_compact)
        val clickSetup = AddTransactionWidgetClick.Setup(context, rv, appWidgetId)

        clickSetup.clickListener(R.id.ivIncome, AddTransactionWidgetClick.ACTION_ADD_INCOME)

        clickSetup.clickListener(R.id.ivExpense, AddTransactionWidgetClick.ACTION_ADD_EXPENSE)

        clickSetup.clickListener(R.id.ivTransfer, AddTransactionWidgetClick.ACTION_ADD_TRANSFER)

        appWidgetManager.updateAppWidget(appWidgetId, rv)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val widgetClick = AddTransactionWidgetClick()
        widgetClick.handleClick(context, intent)
    }
}