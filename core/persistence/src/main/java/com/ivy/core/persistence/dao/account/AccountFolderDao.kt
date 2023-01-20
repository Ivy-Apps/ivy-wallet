package com.ivy.core.persistence.dao.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.account.AccountFolderEntity
import com.ivy.data.DELETING
import com.ivy.data.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountFolderDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(values: List<AccountFolderEntity>)
    //endregion

    // region Select
    @Query("SELECT * FROM account_folders WHERE sync != $DELETING")
    suspend fun findAllBlocking(): List<AccountFolderEntity>

    @Query("SELECT * FROM account_folders WHERE sync != $DELETING ORDER BY orderNum ASC")
    fun findAll(): Flow<List<AccountFolderEntity>>

    @Query("SELECT * FROM account_folders WHERE sync != $DELETING AND id = :folderId")
    suspend fun findById(folderId: String): AccountFolderEntity?

    @Query("SELECT MAX(orderNum) FROM account_folders")
    suspend fun findMaxOrderNum(): Double?
    // endregion

    // region Update
    @Query("UPDATE account_folders SET sync = :sync WHERE id = :folderId")
    suspend fun updateSync(folderId: String, sync: SyncState)
    // endregion
}