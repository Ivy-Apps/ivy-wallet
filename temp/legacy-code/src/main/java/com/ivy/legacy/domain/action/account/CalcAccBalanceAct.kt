package com.ivy.wallet.domain.action.account

import arrow.core.nonEmptyListOf
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.data.model.Account
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.transaction.AccountValueFunctions
import com.ivy.wallet.domain.pure.transaction.foldTransactions
import java.math.BigDecimal
import javax.inject.Inject

class CalcAccBalanceAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct,
    private val transactionMapper: TransactionMapper
) : FPAction<CalcAccBalanceAct.Input, CalcAccBalanceAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        AccTrnsAct.Input(
            accountId = account.id.value, range = range
        )
    } then accTrnsAct then { accTrns ->
        foldTransactions(
            transactions = with(transactionMapper) {
                accTrns.map {
                    it.toEntity().toDomain(AssetCode("NGN")).getOrNull()
                }.filterNotNull()
            },
            arg = account.id.value,
            valueFunctions = nonEmptyListOf(AccountValueFunctions::balance)
        ).head
    } then { balance ->
        Output(
            account = account, balance = balance
        )
    }

    data class Input(
        val account: Account,
        val range: ClosedTimeRange = ClosedTimeRange.allTimeIvy()
    )

    data class Output(
        val account: Account,
        val balance: BigDecimal,
    )
}
