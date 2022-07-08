package com.ivy.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import timber.log.Timber

class WidgetBase {
    companion object {
        fun <T> updateBroadcast(context: Context, widget: Class<T>) {
            Timber.d("update()")
            val intent = Intent(context, widget)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val appWidgetManager = AppWidgetManager.getInstance(context)
            intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS,
                getAppWidgetIds(
                    context = context,
                    appWidgetManager = appWidgetManager,
                    widget = widget
                )
            )
            context.sendBroadcast(intent)
        }

        private fun <T> getAppWidgetIds(
            context: Context,
            appWidgetManager: AppWidgetManager,
            widget: Class<T>
        ): IntArray? {
            val ivyWidgetComponent =
                ComponentName(context, widget)
            return appWidgetManager.getAppWidgetIds(ivyWidgetComponent)
        }
    }

}