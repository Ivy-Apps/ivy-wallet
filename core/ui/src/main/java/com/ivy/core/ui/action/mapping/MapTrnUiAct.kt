package com.ivy.core.ui.action.mapping

import com.ivy.core.ui.data.TransactionUi
import com.ivy.data.transaction.Transaction

class MapTrnUiAct : MapUiAction<Transaction, TransactionUi>() {
    override suspend fun transform(domain: Transaction): TransactionUi {
        TODO("Not yet implemented")
    }
}