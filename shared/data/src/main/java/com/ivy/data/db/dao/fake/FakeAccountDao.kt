package com.ivy.data.db.dao.fake

import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.db.entity.AccountEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeAccountDao : AccountDao, WriteAccountDao {
    private val accounts = mutableListOf<AccountEntity>()

    override suspend fun findAll(deleted: Boolean): List<AccountEntity> {
        return accounts.filter { it.isDeleted == deleted }
    }

    override suspend fun findById(id: UUID): AccountEntity? {
        return accounts.find { it.id == id }
    }

    override suspend fun findMaxOrderNum(): Double? {
        return accounts.maxOfOrNull { it.orderNum }
    }

    override suspend fun save(value: AccountEntity) {
        val existingItemIndex = accounts.indexOfFirst { it.id == value.id }
        if (existingItemIndex > -1) {
            accounts[existingItemIndex] = value
        } else {
            accounts.add(value)
        }
    }

    override suspend fun saveMany(values: List<AccountEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun flagDeleted(id: UUID) {
        accounts.replaceAll { account ->
            if (account.id == id) {
                account.copy(isDeleted = true)
            } else {
                account
            }
        }
    }

    override suspend fun deleteById(id: UUID) {
        accounts.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        accounts.clear()
    }
}