package com.ivy.legacy.domain.deprecated.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.utils.ioThread
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.wallet.domain.deprecated.logic.WalletAccountLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.pure.util.nextOrderNum
import javax.inject.Inject

class AccountCreator @Inject constructor(
    private val accountLogic: WalletAccountLogic,
    private val accountDao: AccountDao,
    private val accountWriter: WriteAccountDao,
) {

    suspend fun createAccount(
        data: CreateAccountData,
        onRefreshUI: suspend () -> Unit
    ) {
        val name = data.name
        if (name.isBlank()) return


        ioThread {
            val account = Account(
                name = name,
                currency = data.currency,
                color = data.color.toArgb(),
                icon = data.icon,
                includeInBalance = data.includeBalance,
                orderNum = accountDao.findMaxOrderNum().nextOrderNum(),
                isSynced = false
            )
            accountWriter.save(account.toEntity())

            accountLogic.adjustBalance(
                account = account,
                actualBalance = 0.0,
                newBalance = data.balance
            )
        }

        onRefreshUI()
    }

    suspend fun editAccount(
        account: Account,
        newBalance: Double,
        onRefreshUI: suspend () -> Unit
    ) {
        val updatedAccount = account.copy(
            isSynced = false
        )

        ioThread {
            accountWriter.save(updatedAccount.toEntity())
            accountLogic.adjustBalance(
                account = updatedAccount,
                actualBalance = accountLogic.calculateAccountBalance(updatedAccount),
                newBalance = newBalance
            )
        }

        onRefreshUI()
    }
}
