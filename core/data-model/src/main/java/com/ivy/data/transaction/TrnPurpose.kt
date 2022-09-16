package com.ivy.data.transaction

enum class TrnPurpose(val code: Int) {
    TransferFrom(1),
    TransferTo(2),
    Fee(3);

    companion object {
        fun fromCode(code: Int) = values().firstOrNull { it.code == code }
    }
}