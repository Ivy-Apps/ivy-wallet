package com.ivy.core.ui.action

import com.ivy.core.ui.data.TransactionUi
import com.ivy.data.transaction.Transaction

class MapTrnUiAct : MapUiAction<Transaction, TransactionUi>() {
    override fun transform(domain: Transaction): TransactionUi {
        TODO("Not yet implemented")
    }
}