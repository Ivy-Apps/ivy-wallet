package com.ivy.data.db.dao.fake

import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.db.entity.AccountEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeAccountDao : AccountDao, WriteAccountDao {
    private val accounts = mutableMapOf<UUID, AccountEntity>()

    override suspend fun findAll(deleted: Boolean): List<AccountEntity> {
        return accounts.filterValues { it.isDeleted == deleted }.values.toList()
    }

    override suspend fun findById(id: UUID): AccountEntity? {
        return accounts[id]
    }

    override suspend fun findMaxOrderNum(): Double? {
        return accounts.maxOfOrNull { (_, entity) -> entity.orderNum }
    }

    override suspend fun save(value: AccountEntity) {
        accounts[value.id] = value
    }

    override suspend fun saveMany(values: List<AccountEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deleteById(id: UUID) {
        accounts.remove(id)
    }

    override suspend fun deleteAll() {
        accounts.clear()
    }
}