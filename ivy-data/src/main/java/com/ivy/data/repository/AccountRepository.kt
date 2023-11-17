package com.ivy.data.repository

import com.ivy.data.model.Account
import com.ivy.data.model.AccountId

interface AccountRepository {
    suspend fun findById(id: AccountId): Account?
    suspend fun findAll(deleted: Boolean = false): List<Account>
    suspend fun findMaxOrderNum(): Double

    suspend fun save(value: Account)
    suspend fun saveMany(values: List<Account>)
    suspend fun flagDeleted(id: AccountId)
    suspend fun deleteById(id: AccountId)
    suspend fun deleteAll()
}