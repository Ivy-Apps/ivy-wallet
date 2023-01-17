package com.ivy.core.persistence.entity.trn.data

const val ActualCode = 1
const val DueCode = 2

enum class TrnTimeType(val code: Int) {
    Actual(ActualCode), Due(DueCode);

    companion object {
        fun fromCode(code: Int): TrnTimeType? = values().firstOrNull { it.code == code }
    }
}