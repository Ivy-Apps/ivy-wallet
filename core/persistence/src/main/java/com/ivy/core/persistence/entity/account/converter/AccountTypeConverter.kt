package com.ivy.core.persistence.entity.account.converter

import androidx.room.TypeConverter
import com.ivy.data.account.AccountState

class AccountTypeConverter {
    // region AccountState
    @TypeConverter
    fun ser(state: AccountState): Int = state.code

    @TypeConverter
    fun accountState(code: Int): AccountState =
        AccountState.fromCode(code) ?: AccountState.Default
    // endregion
}