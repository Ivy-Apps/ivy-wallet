package com.ivy.wallet.domain.deprecated.logic

import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.logic.model.CreateAccountData
import com.ivy.wallet.domain.sync.item.TransactionSync
import com.ivy.wallet.domain.sync.uploader.AccountUploader
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.utils.ioThread

class AccountCreator(
    private val paywallLogic: PaywallLogic,
    private val accountDao: AccountDao,
    private val accountUploader: AccountUploader,
    private val transactionSync: TransactionSync,
    private val accountLogic: WalletAccountLogic
) {

    suspend fun createAccount(
        data: CreateAccountData,
        onRefreshUI: suspend () -> Unit
    ) {
        val name = data.name
        if (name.isBlank()) return

        paywallLogic.protectAddWithPaywall(
            addAccount = true
        ) {
            val newAccount = ioThread {
                val account = Account(
                    name = name,
                    currency = data.currency,
                    color = data.color.toArgb(),
                    icon = data.icon,
                    includeInBalance = data.includeBalance,
                    orderNum = accountDao.findMaxOrderNum() + 1,
                    isSynced = false
                )
                accountDao.save(account)

                accountLogic.adjustBalance(
                    account = account,
                    actualBalance = 0.0,
                    newBalance = data.balance
                )
                account
            }

            onRefreshUI()

            ioThread {
                accountUploader.sync(newAccount)
                transactionSync.sync()
            }
        }
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
            accountDao.save(updatedAccount)
            accountLogic.adjustBalance(
                account = updatedAccount,
                actualBalance = accountLogic.calculateAccountBalance(updatedAccount),
                newBalance = newBalance
            )
        }

        onRefreshUI()

        ioThread {
            accountUploader.sync(updatedAccount)
            transactionSync.sync()
        }
    }
}