package com.ivy.core.action.transaction

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.ivy.core.action.AccountsAct
import com.ivy.core.action.CategoriesAct
import com.ivy.core.functions.toRange
import com.ivy.data.Invalid
import com.ivy.data.Period
import com.ivy.data.SyncMetadata
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.*
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.io.persistence.data.TransactionEntity
import java.time.LocalDateTime
import java.util.*

class TransactionsAct constructor(
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
) : FPAction<TransactionsAct.Input, List<Transaction>>() {

    data class Input(
        val period: Period,
        val query: suspend (LocalDateTime, LocalDateTime) -> List<TransactionEntity>
    )


    override suspend fun Input.compose(): suspend () -> List<Transaction> = {
        period.toRange()
    } then { (from, to) ->
        query(from, to)
    } then { entities ->
        val accounts = accountsAct(Unit).associateBy { it.id }
        val categories = categoriesAct(Unit).associateBy { it.id }

        entities.mapNotNull {
            mapTransactionEntity(
                accounts = accounts,
                categories = categories,
                entity = it
            )
        }
    }

    private fun mapTransactionEntity(
        accounts: Map<UUID, Account>,
        categories: Map<UUID, Category>,
        entity: TransactionEntity
    ): Transaction? {
        return Transaction(
            id = entity.id,
            account = accounts[entity.accountId] ?: return null,
            type = transactionType(
                accounts = accounts,
                entity = entity
            ).orNull() ?: return null,
            amount = entity.amount,
            category = categories[entity.categoryId],
            time = trnTime(entity) ?: return null,
            title = entity.title,
            description = entity.description,
            attachmentUrl = entity.attachmentUrl,
            metadata = TrnMetadata(
                recurringRuleId = entity.recurringRuleId,
                loanId = entity.loanId,
                loanRecordId = entity.loanRecordId,
                sync = SyncMetadata(
                    isSynced = entity.isSynced,
                    isDeleted = entity.isDeleted
                )
            )
        )
    }

    private fun trnTime(entity: TransactionEntity): TrnTime? =
        entity.dateTime?.let { TrnTime.Actual(it) } ?: entity.dueDate?.let { TrnTime.Due(it) }

    private fun transactionType(
        accounts: Map<UUID, Account>,
        entity: TransactionEntity
    ): Either<Invalid, TransactionType> {
        return when (entity.type) {
            TrnType.INCOME -> Right(TransactionType.Income)
            TrnType.EXPENSE -> Right(TransactionType.Expense)
            TrnType.TRANSFER -> {
                val toAccount = entity.toAccountId?.let(accounts::get) ?: return Left(Invalid)
                val toAmount = entity.toAmount ?: entity.amount

                Right(
                    TransactionType.Transfer(
                        toAccount = toAccount,
                        toAmount = toAmount
                    )
                )
            }
        }
    }
}