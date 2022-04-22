package com.ivy.wallet.domain.data.core

import com.ivy.wallet.domain.data.AuthProviderType
import java.util.*

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
    fun names(): String = firstName + if (lastName != null) " $lastName" else ""
}