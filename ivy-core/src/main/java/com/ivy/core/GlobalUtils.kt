package com.ivy.core

import android.content.Context
import androidx.annotation.StringRes

lateinit var appContext: Context
var activityContext: Context? = null // TODO: Get rid of that!

fun stringRes(
    @StringRes id: Int,
    vararg args: String
): String {
    // I don't want strings.xml to handle something different than String at this point
    return appContext.getString(id, *args)
}