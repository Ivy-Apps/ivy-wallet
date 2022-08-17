package com.ivy.core.ui.temp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Intent
import androidx.annotation.StringRes

fun stringRes(
    @StringRes id: Int,
    vararg args: String
): String {
    //I don't want strings.xml to handle something different than String at this point
    return GlobalProvider.appContext.getString(id, *args)
}

fun refreshWidget(widgetReceiver: Class<out AppWidgetProvider>) {
    val updateIntent = Intent(GlobalProvider.appContext, widgetReceiver)
    updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

    val widgetManager = AppWidgetManager.getInstance(GlobalProvider.appContext)
    val ids = widgetManager.getAppWidgetIds(
        ComponentName(
            GlobalProvider.appContext,
            widgetReceiver
        )
    )
    updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

    GlobalProvider.appContext.sendBroadcast(updateIntent)
}