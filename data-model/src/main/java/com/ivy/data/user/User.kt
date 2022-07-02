package com.ivy.data.user

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
)