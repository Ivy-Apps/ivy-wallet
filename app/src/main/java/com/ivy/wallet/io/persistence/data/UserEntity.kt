package com.ivy.wallet.io.persistence.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.domain.data.AuthProviderType
import com.ivy.wallet.domain.data.core.User
import java.util.*

@Entity(tableName = "users")
data class UserEntity(
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "authProviderType")
    val authProviderType: AuthProviderType,
    @ColumnInfo(name = "firstName")
    var firstName: String,
    @ColumnInfo(name = "lastName")
    val lastName: String?,
    @ColumnInfo(name = "profilePicture")
    val profilePicture: String?,
    @ColumnInfo(name = "color")
    val color: Int,

    @ColumnInfo(name = "testUser")
    val testUser: Boolean = false,

    @PrimaryKey @ColumnInfo(name = "id")
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