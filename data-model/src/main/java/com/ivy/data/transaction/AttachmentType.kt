package com.ivy.data.transaction

enum class AttachmentType(val code: Int) {
    Image(1), PDF(2), File(3), Unknown(999);

    companion object {
        fun fromCode(code: Int): AttachmentType =
            values().firstOrNull { it.code == code } ?: Unknown
    }
}