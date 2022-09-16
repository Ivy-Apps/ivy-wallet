package com.ivy.data.attachment

enum class AttachmentType(val code: Int) {
    Image(1), PDF(2), File(3);

    companion object {
        fun fromCode(code: Int): AttachmentType? = values().firstOrNull { it.code == code }
    }
}