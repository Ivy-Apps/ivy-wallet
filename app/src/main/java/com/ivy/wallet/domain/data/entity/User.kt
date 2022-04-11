package com.ivy.wallet.domain.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.AuthProviderType
import java.util.*

@Entity(tableName = "users")
data class User(
    @SerializedName("email")
    @ColumnInfo(name = "email")
    val email: String,
    @SerializedName("authProviderType")
    val authProviderType: AuthProviderType,
    @SerializedName("firstName")
    @ColumnInfo(name = "firstName")
    var firstName: String,
    @SerializedName("lastName")
    @ColumnInfo(name = "lastName")
    val lastName: String?,
    @SerializedName("profilePictureUrl")
    @ColumnInfo(name = "profilePicture")
    val profilePicture: String?,
    @SerializedName("color")
    @ColumnInfo(name = "color")
    val color: Int,

    @SerializedName("testUser")
    @ColumnInfo(name = "testUser")
    val testUser: Boolean = false,

    @SerializedName("id")
    @PrimaryKey @ColumnInfo(name = "id")
    var id: UUID
) {
    fun names(): String = firstName + if (lastName != null) " $lastName" else ""
}