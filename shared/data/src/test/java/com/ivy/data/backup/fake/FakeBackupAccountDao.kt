package com.ivy.data.backup.fake

import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.db.entity.AccountEntity
import java.util.UUID

class FakeBackupAccountDao : AccountDao, WriteAccountDao {
    private val accounts = mutableListOf<AccountEntity>()

    override suspend fun findAll(deleted: Boolean): List<AccountEntity> {
        return accounts
    }

    override suspend fun findById(id: UUID): AccountEntity? {
        return accounts.find { it.id == id }
    }

    override suspend fun findMaxOrderNum(): Double? {
        return accounts.maxOfOrNull { it.orderNum }
    }

    override suspend fun save(value: AccountEntity) {
        accounts.add(value)
    }

    override suspend fun saveMany(values: List<AccountEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun flagDeleted(id: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: UUID) {
        accounts.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        accounts.clear()
    }
}