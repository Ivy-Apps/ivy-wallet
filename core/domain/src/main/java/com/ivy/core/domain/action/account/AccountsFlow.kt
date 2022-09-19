package com.ivy.core.domain.action.account

import com.ivy.common.toUUID
import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.domain.action.icon.DefaultTo
import com.ivy.core.domain.action.icon.IconAct
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.entity.account.AccountEntity
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
@Singleton
class AccountsFlow @Inject constructor(
    private val accountDao: AccountDao,
    private val iconAct: IconAct,
) : SharedFlowAction<List<Account>>() {

    override fun initialValue(): List<Account> = emptyList()

    override fun createFlow(): Flow<List<Account>> =
        accountDao.findAll().map { entities ->
            entities.map { toAccount(acc = it) }
        }.flowOn(Dispatchers.IO)

    private suspend fun toAccount(acc: AccountEntity): Account =
        Account(
            id = acc.id.toUUID(),
            name = acc.name,
            currency = acc.currency,
            color = acc.color,
            icon = iconAct(
                IconAct.Input(
                    iconId = acc.icon,
                    defaultTo = DefaultTo.Account
                )
            ),
            excluded = acc.excluded,
            folderId = acc.folderId?.toUUID(),
            orderNum = acc.orderNum,
            state = acc.state,
            sync = acc.sync
        )
}