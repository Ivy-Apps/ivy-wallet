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

class TransfersOutAct @Inject constructor(
    private val trnsAct: TrnsAct,
    private val transactionDao: TransactionDao,
) : FPAction<Account, TransfersOutAct.Output>() {

    data class Output(
        val transfersOut: List<Transaction>,
        val amount: Double
    )

    override suspend fun Account.compose(): suspend () -> Output =
        TrnsAct.Input(
            period = allTime(),
            query = { _, _ ->
                transactionDao.findAllByTypeAndAccount(
                    accountId = this.id,
                    type = TrnType.TRANSFER
                )
            }
        ) asParamTo trnsAct then { transfersOut ->
            Output(
                transfersOut = transfersOut,
                amount = foldTransactions(
                    transactions = transfersOut,
                    valueFunctions = nonEmptyListOf(
                        ::transfersOutAmount
                    ),
                    arg = this
                ).head
            )
        }

    private suspend fun transfersOutAmount(trn: Transaction, arg: Account): Double {
        if (trn.type !is TransactionType.Transfer) return 0.0
        if (trn.account != arg) return 0.0
        return trn.amount
    }

}