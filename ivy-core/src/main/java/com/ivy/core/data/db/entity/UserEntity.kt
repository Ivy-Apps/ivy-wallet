package com.ivy.core.data.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

// TODO: Drop this database table
@Keep
@Entity(tableName = "users")
data class UserEntity(
    @SerializedName("email")
    @ColumnInfo(name = "email")
    val email: String,
    @SerializedName("authProviderType")
    @ColumnInfo(name = "authProviderType")
    val authProviderType: String,
    @SerializedName("firstName")
    @ColumnInfo(name = "firstName")
    var firstName: String,
    @SerializedName("lastName")
    @ColumnInfo(name = "lastName")
    val lastName: String?,
    @SerializedName("profilePicture")
    @ColumnInfo(name = "profilePicture")
    val profilePicture: String?,
    @SerializedName("color")
    @ColumnInfo(name = "color")
    val color: Int,

    @SerializedName("testUser")
    @ColumnInfo(name = "testUser")
    val testUser: Boolean = false,

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: UUID
)
