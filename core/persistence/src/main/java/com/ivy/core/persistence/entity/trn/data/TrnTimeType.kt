package com.ivy.core.persistence.entity.trn.data

enum class TrnTimeType(val code: Int) {
    Actual(1), Due(2);

    companion object {
        fun fromCode(code: Int): TrnTimeType? = values().firstOrNull { it.code == code }
    }
}