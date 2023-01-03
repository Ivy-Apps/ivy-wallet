package com.ivy.data

import androidx.compose.runtime.Immutable

@Immutable
enum class Theme(val code: Int) {
    Light(1), Dark(-1), Auto(0);

    companion object {
        fun fromCode(code: Int): Theme = values().first { it.code == code }
    }
}