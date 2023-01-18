package com.ivy.data.transaction

const val TrnStateHidden = 2
const val TrnStateDefault = 1

enum class TrnState(val code: Int) {
    Default(TrnStateDefault), Hidden(TrnStateHidden);

    companion object {
        fun fromCode(code: Int): TrnState? = values().firstOrNull { it.code == code }
    }
}