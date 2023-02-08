package com.ivy.core.domain.api.action

import com.ivy.core.data.Transaction

class WriteTransactionAct {
    // TODO: Implement
}

sealed interface ModifyTransaction {
    data class Save(
        val transaction: Transaction,
    ) : ModifyTransaction

}