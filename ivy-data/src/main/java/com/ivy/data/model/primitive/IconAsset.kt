package com.ivy.data.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.ivy.base.exact.Exact

@JvmInline
value class IconAsset private constructor(val id: String) {
    companion object : Exact<String, IconAsset> {
        override val exactName = "IconId"

        override fun Raise<String>.spec(raw: String): IconAsset {
            val notBlankTrimmed = NotBlankTrimmedString.from(raw).bind().value
            ensure(" " !in notBlankTrimmed) { "Cannot contain spaces" }
            return IconAsset(notBlankTrimmed.lowercase())
        }
    }
}