package com.ivy.base.legacy

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes

@Deprecated("Legacy. Will be removed.")
lateinit var appContext: Context

@Deprecated("Legacy. Will be removed.")
fun stringRes(
    @StringRes id: Int,
    vararg args: String
): String {
    // I don't want strings.xml to handle something different than String at this point
    return appContext.getString(id, *args)
}

@Deprecated("Legacy. Will be removed.")
fun refreshWidget(widgetReceiver: Class<*>) {
    val updateIntent = Intent(appContext, widgetReceiver)
    updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

    val widgetManager = AppWidgetManager.getInstance(appContext)
    val ids = widgetManager.getAppWidgetIds(
        ComponentName(
            appContext,
            widgetReceiver
        )
    )
    updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

    appContext.sendBroadcast(updateIntent)
}