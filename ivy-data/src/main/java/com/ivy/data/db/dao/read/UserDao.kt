package com.ivy.data.db.dao.read

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.data.db.entity.UserEntity
import java.util.*

@Deprecated("No longer needed, must be removed.")
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun findById(userId: UUID): UserEntity?

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
