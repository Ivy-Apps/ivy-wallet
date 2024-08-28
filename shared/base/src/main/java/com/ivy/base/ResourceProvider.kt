package com.ivy.base

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    fun getString(@StringRes resId: Int): String = context.getString(resId)

    fun getString(
        @StringRes resId: Int,
        vararg args: Any
    ): String = context.getString(resId, args)
}