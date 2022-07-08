package com.ivy.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class AddTransactionWidget : AppWidgetProvider() {

    companion object {
        fun updateBroadcast(context: Context) {
            WidgetBase.updateBroadcast(context, AddTransactionWidget::class.java)
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
        val rv = RemoteViews(context.packageName, R.layout.widget_add_transaction)
        val clickSetup = AddTransactionWidgetClick.Setup(context, rv, appWidgetId)

        clickSetup.clickListener(R.id.ivIncome, AddTransactionWidgetClick.ACTION_ADD_INCOME)
        clickSetup.clickListener(R.id.tvIncome, AddTransactionWidgetClick.ACTION_ADD_INCOME)

        clickSetup.clickListener(R.id.ivExpense, AddTransactionWidgetClick.ACTION_ADD_EXPENSE)
        clickSetup.clickListener(R.id.tvExpense, AddTransactionWidgetClick.ACTION_ADD_EXPENSE)

        clickSetup.clickListener(R.id.ivTransfer, AddTransactionWidgetClick.ACTION_ADD_TRANSFER)
        clickSetup.clickListener(R.id.tvTransfer, AddTransactionWidgetClick.ACTION_ADD_TRANSFER)

        appWidgetManager.updateAppWidget(appWidgetId, rv)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val widgetClick = AddTransactionWidgetClick()
        widgetClick.handleClick(context, intent)
    }
}