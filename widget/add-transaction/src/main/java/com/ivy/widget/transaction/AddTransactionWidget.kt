package com.ivy.widget.transaction

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.ivy.base.model.TransactionType
import com.ivy.widgets.WidgetBase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddTransactionWidget : AppWidgetProvider() {

    @Inject
    lateinit var widgetClick: AddTransactionWidgetClick

    companion object {
        fun updateBroadcast(context: Context) {
            WidgetBase.updateBroadcast(context, AddTransactionWidget::class.java)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        updateBroadcast(context)
    }

    // --------------------------- </BROADCASTS> ----------------------------------------------------
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

        widgetClick.clickListener(context, rv, R.id.ivIncome, TransactionType.INCOME)
        widgetClick.clickListener(context, rv, R.id.tvIncome, TransactionType.INCOME)

        widgetClick.clickListener(context, rv, R.id.ivExpense, TransactionType.EXPENSE)
        widgetClick.clickListener(context, rv, R.id.tvExpense, TransactionType.EXPENSE)

        widgetClick.clickListener(context, rv, R.id.ivTransfer, TransactionType.TRANSFER)
        widgetClick.clickListener(context, rv, R.id.tvTransfer, TransactionType.TRANSFER)

        appWidgetManager.updateAppWidget(appWidgetId, rv)
    }
}
