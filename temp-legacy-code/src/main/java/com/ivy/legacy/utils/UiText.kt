package com.ivy.legacy.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Immutable
sealed interface UiText {
    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    @Immutable
    data class DynamicString(val value: String) : UiText

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    @Immutable
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }
}
