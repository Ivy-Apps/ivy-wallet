package com.ivy.data.source

import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.db.entity.AccountEntity
import java.util.UUID
import javax.inject.Inject

class LocalAccountDataSource @Inject constructor(
    private val accountDao: AccountDao,
    private val writeAccountDao: WriteAccountDao,
) {
    suspend fun findById(id: UUID): AccountEntity? {
        return accountDao.findById(id)
    }

    suspend fun findAll(deleted: Boolean = false): List<AccountEntity> {
        return accountDao.findAll(deleted = deleted)
    }

    suspend fun findMaxOrderNum(): Double? {
        return accountDao.findMaxOrderNum()
    }

    suspend fun save(value: AccountEntity) {
        writeAccountDao.save(value)
    }

    suspend fun saveMany(values: List<AccountEntity>) {
        writeAccountDao.saveMany(values)
    }

    suspend fun flagDeleted(id: UUID) {
        writeAccountDao.flagDeleted(id)
    }

    suspend fun deleteById(id: UUID) {
        writeAccountDao.deleteById(id)
    }

    suspend fun deleteAll() {
        writeAccountDao.deleteAll()
    }
}