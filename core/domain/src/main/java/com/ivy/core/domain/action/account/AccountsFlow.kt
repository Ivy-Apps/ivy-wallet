package com.ivy.core.domain.action.account

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toLocal
import com.ivy.common.toUUID
import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.entity.account.AccountEntity
import com.ivy.data.Sync
import com.ivy.data.account.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @return a flow of latest [Account]s by transforming db entities into domain objects.
 */
// TODO: Remove from `SharedFlowAction`: no need to be @Singleton and occupy memory!
@Singleton
class AccountsFlow @Inject constructor(
    private val accountDao: AccountDao,
    private val timeProvider: TimeProvider,
) : SharedFlowAction<List<Account>>() {
    override fun initialValue(): List<Account> = emptyList()

    override fun createFlow(): Flow<List<Account>> =
        accountDao.findAll().map { entities ->
            entities.map {
                toDomain(acc = it, timeProvider = timeProvider)
            }
        }.flowOn(Dispatchers.IO)
}

fun toDomain(
    acc: AccountEntity,
    timeProvider: TimeProvider,
): Account =
    Account(
        id = acc.id.toUUID(),
        name = acc.name,
        currency = acc.currency,
        color = acc.color,
        icon = acc.icon,
        excluded = acc.excluded,
        folderId = acc.folderId?.toUUID(),
        orderNum = acc.orderNum,
        state = acc.state,
        sync = Sync(
            state = acc.sync,
            lastUpdated = acc.lastUpdated.toLocal(timeProvider)
        ),
    )