package com.ivy.backup.base

fun <T> optional(block: () -> T): T? = try {
    block()
} catch (e: Exception) {
    null
}