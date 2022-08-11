package com.ivy.base

import android.content.Context
import android.content.Intent
import com.ivy.data.transaction.TrnType

interface RootIntent {
    fun addTransactionStart(
        context: Context,
        type: TrnType
    ): Intent

    fun getIntent(context: Context): Intent
}

