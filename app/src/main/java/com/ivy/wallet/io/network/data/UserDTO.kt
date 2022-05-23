package com.ivy.wallet.io.network.data

import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.AuthProviderType
import com.ivy.wallet.io.persistence.data.UserEntity
import java.util.*

data class UserDTO(
    val email: String,
    val authProviderType: AuthProviderType,
    var firstName: String,
    val lastName: String?,
    @SerializedName("profilePictureUrl")
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
        id = id,
    )

    fun names(): String = firstName + if (lastName != null) " $lastName" else ""
}