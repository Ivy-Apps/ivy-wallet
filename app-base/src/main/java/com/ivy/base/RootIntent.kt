package com.ivy.base

import android.content.Context
import android.content.Intent
import com.ivy.data.transaction.TrnTypeOld

interface RootIntent {
    fun addTransactionStart(
        context: Context,
        type: TrnTypeOld
    ): Intent

    fun getIntent(context: Context): Intent
}

