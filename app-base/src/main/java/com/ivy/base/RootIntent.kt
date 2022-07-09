package com.ivy.base

import android.content.Context
import android.content.Intent
import com.ivy.data.transaction.TransactionType

interface RootIntent {
    fun addTransactionStart(
        context: Context,
        type: TransactionType
    ): Intent

    fun getIntent(context: Context): Intent
}

