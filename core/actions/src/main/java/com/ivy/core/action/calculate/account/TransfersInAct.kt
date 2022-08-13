package com.ivy.core.action.calculate.account

import arrow.core.nonEmptyListOf
import com.ivy.core.action.transaction.read.TrnsAct
import com.ivy.core.functions.allTime
import com.ivy.core.functions.transaction.foldTransactions
import com.ivy.data.account.Account
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnType
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.frp.then
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class TransfersInAct @Inject constructor(
    private val trnsAct: TrnsAct,
    private val transactionDao: TransactionDao,
) : FPAction<Account, TransfersInAct.Output>() {

    data class Output(
        val transfersIn: List<Transaction>,
        val amount: Double
    )

    override suspend fun Account.compose(): suspend () -> Output =
        TrnsAct.Input(
            period = allTime(),
            query = { _, _ ->
                transactionDao.findAllTransfersToAccount(
                    toAccountId = this.id,
                    type = TrnType.TRANSFER
                )
            }
        ) asParamTo trnsAct then { transfersIn ->
            Output(
                transfersIn = transfersIn,
                amount = foldTransactions(
                    transactions = transfersIn,
                    valueFunctions = nonEmptyListOf(
                        ::transfersInAmount
                    ),
                    arg = this
                ).head
            )
        }

    private suspend fun transfersInAmount(trn: Transaction, arg: Account): Double {
        val transfer = (trn.type as? TransactionType.Transfer) ?: return 0.0
        if (transfer.toAccount != arg) return 0.0
        return transfer.toAmount
    }

}