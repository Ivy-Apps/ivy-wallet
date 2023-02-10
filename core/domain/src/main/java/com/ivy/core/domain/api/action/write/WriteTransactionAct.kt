package com.ivy.core.domain.api.action.write

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.continuations.either
import arrow.core.right
import arrow.core.toNonEmptyListOrNull
import com.ivy.core.data.Transaction
import com.ivy.core.data.TransactionId
import com.ivy.core.data.calculation.AccountCache
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.api.data.ActionError
import com.ivy.core.domain.calculation.account.cache.invalidCaches
import com.ivy.core.persistence.api.account.AccountCacheRead
import com.ivy.core.persistence.api.account.AccountCacheWrite
import com.ivy.core.persistence.api.data.PersistenceError
import com.ivy.core.persistence.api.transaction.TransactionQuery
import com.ivy.core.persistence.api.transaction.TransactionRead
import com.ivy.core.persistence.api.transaction.TransactionWrite
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class WriteTransactionAct @Inject constructor(
    private val transactionRead: TransactionRead,
    private val transactionWrite: TransactionWrite,
    private val accountCacheWrite: AccountCacheWrite,
    private val accountCacheRead: AccountCacheRead
) : Action<Modify<Transaction, TransactionId>, Either<ActionError, Unit>>() {
    override suspend fun action(
        input: Modify<Transaction, TransactionId>
    ): Either<ActionError, Unit> = when (input) {
        is Modify.Save -> save(input.item)
        is Modify.SaveMany -> saveMany(input.items)
        is Modify.Delete -> delete(input.id)
        is Modify.DeleteMany -> deleteMany(input.ids)
    }.mapLeft { ActionError.IO(it.reason) }

    private suspend fun save(item: Transaction): Either<PersistenceError, Unit> = either {
        val old = transactionRead.single(item.id).first()
        val caches = accountCacheRead.all()
        invalidateCaches(invalidCaches(caches, setOfNotNull(old, item))).bind()
        transactionWrite.save(item).bind()
    }

    private suspend fun saveMany(
        items: NonEmptyList<Transaction>
    ): Either<PersistenceError, Unit> = either {
        val oldOnes = items.map(Transaction::id).let {
            transactionRead.many(TransactionQuery.ByIds(it)).first()
        }
        val caches = accountCacheRead.all()
        invalidateCaches(invalidCaches(caches, (oldOnes + items).toSet())).bind()
        transactionWrite.saveMany(items).bind()
    }

    private suspend fun delete(id: TransactionId): Either<PersistenceError, Unit> = either {
        val item = transactionRead.single(id).first()
        val caches = accountCacheRead.all()
        invalidateCaches(invalidCaches(caches, setOfNotNull(item))).bind()
        transactionWrite.delete(id).bind()
    }

    private suspend fun deleteMany(
        ids: NonEmptyList<TransactionId>
    ): Either<PersistenceError, Unit> = either {
        val items = transactionRead.many(TransactionQuery.ByIds(ids)).first()
        val caches = accountCacheRead.all()
        invalidateCaches(invalidCaches(caches, items.toSet())).bind()
        transactionWrite.deleteMany(ids).bind()
    }

    private suspend fun invalidateCaches(
        caches: Set<AccountCache>,
    ): Either<PersistenceError, Unit> {
        return caches.map(AccountCache::accountId).toNonEmptyListOrNull()
            ?.let { ids ->
                accountCacheWrite.deleteMany(ids)
            } ?: Unit.right()
    }
}