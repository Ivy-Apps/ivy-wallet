package com.ivy.wallet.domain.deprecated.logic.bankintegrations

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.bankintegrations.SEAccount
import com.ivy.wallet.domain.data.bankintegrations.SETransaction
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.logic.WalletAccountLogic
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.utils.ioThread
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.absoluteValue

@Deprecated("Use FP style, look into `domain.fp` package")
class SaltEdgeTransactionMapper(
    private val transactionDao: TransactionDao,
    private val seAccountMapper: SaltEdgeAccountMapper,
    private val seCategoryMapper: SaltEdgeCategoryMapper,
    private val accountDao: AccountDao,
    private val walletAccountLogic: WalletAccountLogic
) {

    suspend fun save(seAccounts: List<SEAccount>, seTransactions: List<SETransaction>) {
        ioThread {
            syncTransactions(
                seTransactions = seTransactions,
                seAccounts = seAccounts
            )

            syncBalances(
                seAccounts = seAccounts
            )
        }
    }

    private fun syncTransactions(
        seTransactions: List<SETransaction>,
        seAccounts: List<SEAccount>
    ) {
        seTransactions.mapNotNull {
            mapSeTransaction(
                seAccounts = seAccounts,
                seTransaction = it
            )
        }.forEach {
            transactionDao.save(it)
        }
    }

    private fun syncBalances(
        seAccounts: List<SEAccount>
    ) {
        val seAccountToAccount = seAccounts.map { seAccount ->
            seAccount to accountDao.findBySeAccountId(seAccount.id)
        }.toMap()

        seAccountToAccount.forEach { (seAccount, account) ->
            if (account != null) {
                walletAccountLogic.adjustBalance(
                    account = account,
                    newBalance = seAccount.balance,
                    adjustTransactionTitle = "Auto-adjust bank account balance",
                    isFiat = true,
                    trnIsSyncedFlag = false
                )
            }
        }
    }

    private fun mapSeTransaction(
        seAccounts: List<SEAccount>,
        seTransaction: SETransaction
    ): Transaction? {
        val existingIvyTransaction = transactionDao.findBySeTransactionId(
            seTransactionId = seTransaction.id
        )

        //Persist ----------------------------------------------------------------------
        val ivyTrnId = existingIvyTransaction?.id ?: UUID.randomUUID()
        val categoryId = existingIvyTransaction?.categoryId
        val title = existingIvyTransaction?.title ?: seTransaction.description
        val description = existingIvyTransaction?.description
        //Persist ----------------------------------------------------------------------

        //Map --------------------------------------------------------------------------
        val accountId = seAccountMapper.mapAccount(
            seAccounts = seAccounts,
            seTransaction = seTransaction
        ) ?: return null

        val seAutoCategoryId = seCategoryMapper.mapSeAutoCategoryId(
            seTransaction = seTransaction
        )

        val type = if (seTransaction.amount > 0)
            TransactionType.INCOME else TransactionType.EXPENSE
        val amount = seTransaction.amount.absoluteValue

        val dateTime = mapDateTime(seTransaction = seTransaction)
        //Map --------------------------------------------------------------------------

        val finalTransaction = Transaction(
            id = ivyTrnId,
            type = type,
            amount = amount,
            accountId = accountId,
            dateTime = dateTime,

            categoryId = categoryId,
            seTransactionId = seTransaction.id,
            seAutoCategoryId = seAutoCategoryId,
            description = description,
            title = title,
        )
        val shouldUploadToSerer = !finalTransaction.isIdenticalWith(existingIvyTransaction) ||
                existingIvyTransaction?.isSynced == false
        Timber.i("Should upload to server: $shouldUploadToSerer")

        return finalTransaction.copy(
            isSynced = !shouldUploadToSerer,
            isDeleted = false
        )
    }

    private fun mapDateTime(seTransaction: SETransaction): LocalDateTime? {
        val time = (seTransaction.extra?.get("time") as? String?)
            ?.parseSaltEdgeTime() ?: LocalTime.of(12, 0, 0)

        return seTransaction.made_on
            .parseSaltEdgeDate()
            ?.atTime(time) ?: return null
    }

    private fun String.parseSaltEdgeTime(): LocalTime? {
        return try {
            LocalTime.parse(this)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun String.parseSaltEdgeDate(): LocalDate? {
        return try {
            return LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun String.parseSaltEdgeDateTime(): LocalDateTime? {
        return try {
            return LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}