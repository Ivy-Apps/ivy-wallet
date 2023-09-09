package com.ivy.wallet.io.persistence

import androidx.room.TypeConverter
import com.ivy.design.l0_system.Theme
import com.ivy.wallet.domain.data.AuthProviderType
import com.ivy.wallet.domain.data.IntervalType
import com.ivy.wallet.domain.data.LoanType
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.utils.epochMilliToDateTime
import com.ivy.wallet.utils.toEpochMilli
import java.time.LocalDateTime
import java.util.*

@SuppressWarnings("unused")
class RoomTypeConverters {
    @TypeConverter
    fun saveDate(localDateTime: LocalDateTime?): Long? = localDateTime?.toEpochMilli()

    @TypeConverter
    fun parseDate(timestampMillis: Long?): LocalDateTime? = timestampMillis?.epochMilliToDateTime()

    @TypeConverter
    fun saveUUID(id: UUID?) = id?.toString()

    @TypeConverter
    fun parseUUID(id: String?) = id?.let { UUID.fromString(id) }

    @TypeConverter
    fun saveTheme(value: Theme?) = value?.name

    @TypeConverter
    fun parseTheme(value: String?) = value?.let { Theme.valueOf(it) }

    @TypeConverter
    fun saveTransactionType(value: TransactionType?) = value?.name

    @TypeConverter
    fun parseTransactionType(value: String?) = value?.let { TransactionType.valueOf(it) }

    @TypeConverter
    fun saveAuthProviderType(authProviderType: AuthProviderType?) = authProviderType?.name

    @TypeConverter
    fun parseAuthProviderType(value: String?) = value?.let { AuthProviderType.valueOf(it) }

    @TypeConverter
    fun saveRecurringIntervalType(value: IntervalType?) = value?.name

    @TypeConverter
    fun parseRecurringIntervalType(value: String?) =
        value?.let { IntervalType.valueOf(it) }

    @TypeConverter
    fun saveLoanType(value: LoanType?) = value?.name

    @TypeConverter
    fun parseLoanType(value: String?) = value?.let { LoanType.valueOf(it) }
}
