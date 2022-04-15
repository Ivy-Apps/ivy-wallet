package com.ivy.wallet.domain.logic.bankintegrations

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.data.bankintegrations.SEAccount
import com.ivy.wallet.domain.data.bankintegrations.SETransaction
import com.ivy.wallet.domain.data.entity.Account
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.ui.theme.components.IVY_COLOR_PICKER_COLORS_FREE
import com.ivy.wallet.utils.toLowerCaseLocal
import java.util.*

@Deprecated("Use FP style, look into `domain.fp` package")
class SaltEdgeAccountMapper(
    private val accountDao: AccountDao
) {

    fun mapAccount(
        seAccounts: List<SEAccount>,
        seTransaction: SETransaction
    ): UUID? {
        val existingAccount = accountDao.findBySeAccountId(
            seAccountId = seTransaction.account_id
        )

        val account = if (existingAccount == null) {
            //create account
            val seAccount = seAccounts.find { it.id == seTransaction.account_id } ?: return null

            val account = Account(
                seAccountId = seAccount.id,

                name = seAccount.name,
                color = mapColor(seAccount).toArgb(),
                icon = mapIcon(seAccount),

                currency = IvyCurrency.fromCode(seAccount.currency_code)?.code,
                orderNum = accountDao.findMaxOrderNum(),

                isSynced = false,
                isDeleted = false
            )

            accountDao.save(account)

            account
        } else existingAccount

        return account.id
    }

    private fun mapColor(seAccount: SEAccount): Color {
        //TODO: Create better mapping
        return IVY_COLOR_PICKER_COLORS_FREE.shuffled().first()
    }

    private fun mapIcon(seAccount: SEAccount): String? {
        //TODO: Create better mapping
        return when {
            seAccount.name.toLowerCaseLocal().contains("revolut") -> {
                "revolut"
            }
            else -> null
        }
    }
}