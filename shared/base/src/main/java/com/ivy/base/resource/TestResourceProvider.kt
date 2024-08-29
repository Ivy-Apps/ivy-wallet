package com.ivy.base.resource

import androidx.annotation.StringRes

// TODO: Add unit tests for this class
class TestResourceProvider : ResourceProvider {
    private val strings = mutableMapOf<Int, String>()

    fun putString(@StringRes resId: Int, value: String) {
        strings[resId] = value
    }

    override fun getString(@StringRes resId: Int): String {
        return strings[resId] ?: stringNotFoundError(resId)
    }

    override fun getString(@StringRes resId: Int, vararg args: Any): String {
        // TODO: this function might not work, add unit tests to verify correctness
        return strings[resId]?.let { String.format(it, *args) }
            ?: stringNotFoundError(resId)
    }

    private fun stringNotFoundError(@StringRes resId: Int): Nothing =
        throw TestStringNotFoundException(resId)
}

class TestStringNotFoundException(@StringRes val stringRes: Int) :
    IllegalStateException("TestResourceProvider(): String not found for resId=$stringRes")