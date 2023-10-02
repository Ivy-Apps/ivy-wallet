package com.ivy.data.repository

import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import java.util.UUID

interface AccountRepository {
    suspend fun findById(id: AccountId): Account?
    suspend fun findAll(deleted: Boolean = false): List<Account>
    suspend fun findMaxOrderNum(): Double

    suspend fun save(value: Account)
    suspend fun saveMany(values: List<Account>)
    suspend fun flagDeleted(id: UUID)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAll()
}