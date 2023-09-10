package com.ivy.widget

import com.ivy.wallet.domain.data.TransactionType

interface AppStarter {
    fun defaultStart()
    fun addTransactionStart(type: TransactionType)
}