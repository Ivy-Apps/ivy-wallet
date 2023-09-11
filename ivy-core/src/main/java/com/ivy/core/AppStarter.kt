package com.ivy.core

import android.content.Intent
import com.ivy.wallet.domain.data.TransactionType

interface AppStarter {
    fun getRootIntent(): Intent
    fun defaultStart()
    fun addTransactionStart(type: TransactionType)
}