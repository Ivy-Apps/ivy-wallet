package com.ivy.data.testing

import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.repository.AccountRepository

class FakeAccountRepository : AccountRepository {
    private val accounts = mutableMapOf<AccountId, Account>()

    override suspend fun findById(id: AccountId): Account? {
        return accounts[id]
    }

    override suspend fun findAll(deleted: Boolean): List<Account> {
        return accounts.values
            .filter { !it.removed }
            .sortedBy { it.orderNum }
    }

    override suspend fun findMaxOrderNum(): Double {
        return accounts.values.firstOrNull()?.orderNum ?: 0.0
    }

    override suspend fun save(value: Account) {
        accounts[value.id] = value
    }

    override suspend fun saveMany(values: List<Account>) {
        values.forEach {
            save(it)
        }
    }

    override suspend fun flagDeleted(id: AccountId) {
        accounts.computeIfPresent(id) { _, acc ->
            acc.copy(removed = true)
        }
    }

    override suspend fun deleteById(id: AccountId) {
        accounts.remove(id)
    }

    override suspend fun deleteAll() {
        accounts.clear()
    }
}