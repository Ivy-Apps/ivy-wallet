package com.ivy.wallet.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.ivy.wallet.R
import timber.log.Timber

class AddTransactionWidget : AppWidgetProvider() {

    companion object {
        fun updateBroadcast(context: Context) {
            Timber.d("update()")
            val intent = Intent(context, AddTransactionWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val appWidgetManager = AppWidgetManager.getInstance(context)
            intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS,
                getAppWidgetIds(context, appWidgetManager)
            )
            context.sendBroadcast(intent)
        }

        private fun getAppWidgetIds(
            context: Context,
            appWidgetManager: AppWidgetManager
        ): IntArray? {
            val ivyWidgetComponent =
                ComponentName(context, AddTransactionWidget::class.java)
            return appWidgetManager.getAppWidgetIds(ivyWidgetComponent)
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