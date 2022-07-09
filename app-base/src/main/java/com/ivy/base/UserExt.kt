package com.ivy.base

import com.ivy.data.user.User

fun User.names(): String = firstName + if (lastName != null) " $lastName" else ""
