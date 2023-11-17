package com.ivy.data.model.primitive

import arrow.core.raise.Raise
import com.ivy.base.exact.Exact

@JvmInline
value class AssetCode private constructor(val code: String) {
    companion object : Exact<String, AssetCode> {
        override val exactName = "AssetCode"

        override fun Raise<String>.spec(raw: String): AssetCode {
            val notBlankTrimmed = NotBlankTrimmedString.from(raw).bind()
            return AssetCode(notBlankTrimmed.value.uppercase())
        }

    }
}