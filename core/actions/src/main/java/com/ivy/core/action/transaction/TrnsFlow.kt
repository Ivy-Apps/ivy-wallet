package com.ivy.core.action.transaction

import androidx.sqlite.db.SimpleSQLiteQuery
import arrow.core.Either
import com.ivy.core.action.FlowAction
import com.ivy.core.action.account.AccountsFlow
import com.ivy.core.action.category.CategoriesFlow
import com.ivy.core.functions.transaction.TrnWhere
import com.ivy.core.functions.transaction.toWhereClause
import com.ivy.data.Invalid
import com.ivy.data.SyncMetadata
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.*
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.io.persistence.data.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class TrnsFlow @Inject constructor(
    private val accountsFlow: AccountsFlow,
    private val categoriesFlow: CategoriesFlow,
    private val transactionDao: TransactionDao,
    private val trnsSignal: TrnsSignal
) : FlowAction<TrnWhere, List<Transaction>>() {

    override suspend fun TrnWhere.createFlow(): Flow<List<Transaction>> =
        combine(accountsFlow(), categoriesFlow(), trnsSignal.receive()) { accs, cats, _ ->
            val where = toWhereClause(this)
            val entities = transactionDao.findByQuery(
                SimpleSQLiteQuery(
                    "SELECT * FROM transactions WHERE isDeleted = 0 AND " +
                            where.query +
                            " ORDER BY dateTime DESC, dueDate ASC",
                    where.args.toTypedArray()
                )
            )

            val accsMap = accs.associateBy { it.id }
            val catsMap = cats.associateBy { it.id }

            entities.mapNotNull {
                mapTransactionEntity(
                    accounts = accsMap,
                    categories = catsMap,
                    entity = it
                )
            }
        }.flowOn(Dispatchers.Default)

    private fun mapTransactionEntity(
        accounts: Map<UUID, Account>,
        categories: Map<UUID, Category>,
        entity: TransactionEntity
    ): Transaction? {
        val account = accounts[entity.accountId] ?: return null

        return Transaction(
            id = entity.id,
            account = account,
            type = transactionType(
                accounts = accounts,
                entity = entity
            ).orNull() ?: return null,
            value = Value(
                amount = entity.amount,
                currency = account.currency
            ),
            category = categories[entity.categoryId],
            time = trnTime(entity) ?: return null,
            title = entity.title.takeIf { it.isNullOrBlank().not() },
            description = entity.description.takeIf { it.isNullOrBlank().not() },
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
            TrnType.INCOME -> Either.Right(TransactionType.Income)
            TrnType.EXPENSE -> Either.Right(TransactionType.Expense)
            TrnType.TRANSFER -> {
                val toAccount =
                    entity.toAccountId?.let(accounts::get) ?: return Either.Left(Invalid)
                val toAmount = entity.toAmount ?: entity.amount

                Either.Right(
                    TransactionType.Transfer(
                        toValue = Value(
                            amount = toAmount,
                            currency = toAccount.currency
                        ),
                        toAccount = toAccount
                    )
                )
            }
        }
    }
}