package com.ivy.core.persistence.dao.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.account.AccountEntity
import com.ivy.data.DELETING
import com.ivy.data.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(values: List<AccountEntity>)
    //endregion

    // region Select
    @Query("SELECT * FROM accounts WHERE sync != $DELETING ORDER BY orderNum ASC")
    fun findAll(): Flow<List<AccountEntity>>
    // endregion

    // region Update
    @Query("UPDATE accounts SET sync = :sync WHERE id = :accountId")
    suspend fun updateSync(accountId: String, sync: SyncState)
    // endregion
}