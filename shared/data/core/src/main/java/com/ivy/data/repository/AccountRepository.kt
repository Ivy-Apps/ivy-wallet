package com.ivy.data.repository

import com.ivy.data.model.Account
import com.ivy.data.model.AccountId

interface AccountRepository {
    suspend fun findById(id: com.ivy.data.model.AccountId): com.ivy.data.model.Account?
    suspend fun findAll(deleted: Boolean = false): List<com.ivy.data.model.Account>
    suspend fun findMaxOrderNum(): Double

    suspend fun save(value: com.ivy.data.model.Account)
    suspend fun saveMany(values: List<com.ivy.data.model.Account>)
    suspend fun deleteById(id: com.ivy.data.model.AccountId)
    suspend fun deleteAll()
}
