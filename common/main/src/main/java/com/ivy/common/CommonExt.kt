package com.ivy.common

import java.util.*

fun String.toUUID(): UUID = UUID.fromString(this)

fun String.toUUIDOrNull(): UUID? = try {
    UUID.fromString(this)
} catch (e: Exception) {
    null
}

fun String?.isNotEmpty(): Boolean = !isNullOrEmpty()

fun String?.isNotBlank(): Boolean = !isNullOrBlank()