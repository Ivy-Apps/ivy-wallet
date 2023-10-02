package com.ivy.data.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

// TODO: Drop this database table
@Keep
@Serializable
@Deprecated("Legacy table. Must be dropped")
@Entity(tableName = "users")
data class UserEntity(
    @SerialName("email")
    @ColumnInfo(name = "email")
    val email: String,
    @SerialName("authProviderType")
    @ColumnInfo(name = "authProviderType")
    val authProviderType: String,
    @SerialName("firstName")
    @ColumnInfo(name = "firstName")
    var firstName: String,
    @SerialName("lastName")
    @ColumnInfo(name = "lastName")
    val lastName: String?,
    @SerialName("profilePicture")
    @ColumnInfo(name = "profilePicture")
    val profilePicture: String?,
    @SerialName("color")
    @ColumnInfo(name = "color")
    val color: Int,

    @SerialName("testUser")
    @ColumnInfo(name = "testUser")
    val testUser: Boolean = false,

    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    @Serializable(with = KSerializerUUID::class)
    var id: UUID
)
