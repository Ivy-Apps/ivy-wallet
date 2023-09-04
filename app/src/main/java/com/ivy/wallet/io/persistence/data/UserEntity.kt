package com.ivy.wallet.io.persistence.data

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.AuthProviderType
import com.ivy.wallet.domain.data.core.User
import java.util.*

@Keep
@Entity(tableName = "users")
data class UserEntity(
    @SerializedName("email")
    @ColumnInfo(name = "email")
    val email: String,
    @SerializedName("authProviderType")
    @ColumnInfo(name = "authProviderType")
    val authProviderType: AuthProviderType,
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
) {
    fun toDomain(): User = User(
        email = email,
        authProviderType = authProviderType,
        firstName = firstName,
        lastName = lastName,
        profilePicture = profilePicture,
        color = color,
        testUser = testUser,
        id = id
    )

    fun names(): String = firstName + if (lastName != null) " $lastName" else ""
}
