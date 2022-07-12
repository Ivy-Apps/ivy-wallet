package com.ivy.wallet.utils

import android.annotation.SuppressLint
import android.util.Patterns
import androidx.compose.runtime.Composable

fun String?.validate(
    basicValidationError: () -> InputError = { InputError("field is null or blank") },
    additionalValidation: ((trimmed: String) -> Unit)? = null
): String = this?.trim()?.apply {
    if (isBlank()) throw basicValidationError()
    additionalValidation?.invoke(this)
} ?: throw basicValidationError()

fun String?.isNotNullOrBlank(): Boolean {
    return this != null && this.isNotBlank()
}

@SuppressLint("ComposableNaming")
@Composable
fun String?.ifNotNullOrBlank(block: @Composable (String) -> Unit) {
    if (this.isNotNullOrBlank()) {
        block(this!!)
    }
}

fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
