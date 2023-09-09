package com.ivy.core

import com.ivy.wallet.domain.data.TransactionType

interface RootScreen {
    fun startActivityForAddingTransaction(type: TransactionType)
}