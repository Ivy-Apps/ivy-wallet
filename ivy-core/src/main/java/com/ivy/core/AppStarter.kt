package com.ivy.core

import android.content.Intent
import com.ivy.core.data.db.entity.TransactionType

interface AppStarter {
    fun getRootIntent(): Intent
    fun defaultStart()
    fun addTransactionStart(type: TransactionType)
}