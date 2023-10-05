package com.ivy.data.testing

import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.repository.AccountRepository

class FakeAccountRepository : AccountRepository {
    private val accountsMap = mutableMapOf<AccountId, Account>()

    override suspend fun findById(id: AccountId): Account? {
        return accountsMap[id]
    }

    override suspend fun findAll(deleted: Boolean): List<Account> {
        return accountsMap.values
            .filter { it.removed == deleted }
            .sortedBy { it.orderNum }
    }

    override suspend fun findMaxOrderNum(): Double {
        return accountsMap.values.maxOfOrNull { it.orderNum } ?: 0.0
    }

    override suspend fun save(value: Account) {
        accountsMap[value.id] = value
    }

    override suspend fun saveMany(values: List<Account>) {
        values.forEach {
            save(it)
        }
    }

    override suspend fun flagDeleted(id: AccountId) {
        accountsMap.computeIfPresent(id) { _, acc ->
            acc.copy(removed = true)
        }
    }

    override suspend fun deleteById(id: AccountId) {
        accountsMap.remove(id)
    }

    override suspend fun deleteAll() {
        accountsMap.clear()
    }
}