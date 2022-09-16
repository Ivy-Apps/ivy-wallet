package com.ivy.data.account

enum class AccountState(val code: Int) {
    Default(1), Archived(2);

    companion object {
        fun fromCode(code: Int): AccountState? = values().firstOrNull { it.code == code }
    }
}