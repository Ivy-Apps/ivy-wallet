package com.ivy.wallet.domain.data.core

import com.ivy.wallet.domain.data.AuthProviderType
import com.ivy.wallet.io.persistence.data.UserEntity
import java.util.UUID

data class User(
    val email: String,
    val authProviderType: AuthProviderType,
    var firstName: String,
    val lastName: String?,
    val profilePicture: String?,
    val color: Int,

    val testUser: Boolean = false,
    var id: UUID
) {
    fun toEntity(): UserEntity = UserEntity(
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
