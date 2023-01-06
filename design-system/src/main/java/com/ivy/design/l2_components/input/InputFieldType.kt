package com.ivy.design.l2_components.input

sealed interface InputFieldType {
    object SingleLine : InputFieldType
    data class Multiline(val maxLines: Int = Int.MAX_VALUE) : InputFieldType
}