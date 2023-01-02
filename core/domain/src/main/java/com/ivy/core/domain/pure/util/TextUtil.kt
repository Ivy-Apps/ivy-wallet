package com.ivy.core.domain.pure.util

fun beautify(text: String?): String? =
    text?.trim()?.takeIf { it.isNotBlank() }

fun String?.takeIfNotBlank(): String? = this?.takeIf { it.isNotBlank() }