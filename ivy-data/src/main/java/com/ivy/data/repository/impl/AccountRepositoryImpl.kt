package com.ivy.data.repository.impl

import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.mapper.AccountMapper
import com.ivy.data.source.LocalAccountDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val mapper: AccountMapper,
    private val dataSource: LocalAccountDataSource
) : AccountRepository {
    override suspend fun findById(id: AccountId): Account? {
        return withContext(Dispatchers.IO) {
            dataSource.findById(id.value)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    }

    override suspend fun findAll(deleted: Boolean): List<Account> {
        return withContext(Dispatchers.IO) {
            dataSource.findAll(deleted).mapNotNull {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    }

    override suspend fun findMaxOrderNum(): Double {
        return withContext(Dispatchers.IO) {
            dataSource.findMaxOrderNum() ?: 0.0
        }
    }

    override suspend fun save(value: Account) {
        withContext(Dispatchers.IO) {
            dataSource.save(
                with(mapper) { value.toEntity() }
            )
        }
    }

    override suspend fun saveMany(values: List<Account>) {
        withContext(Dispatchers.IO) {
            dataSource.saveMany(
                values.map { with(mapper) { it.toEntity() } }
            )
        }
    }

    override suspend fun flagDeleted(id: AccountId) {
        withContext(Dispatchers.IO) {
            dataSource.flagDeleted(id.value)
        }
    }

    override suspend fun deleteById(id: AccountId) {
        withContext(Dispatchers.IO) {
            dataSource.deleteById(id.value)
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dataSource.deleteAll()
        }
    }
}