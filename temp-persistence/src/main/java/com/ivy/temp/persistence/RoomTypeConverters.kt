package com.ivy.wallet.io.persistence

import androidx.room.TypeConverter
import com.ivy.common.time.deviceTimeProvider
import com.ivy.common.time.epochMilliToDateTime
import com.ivy.common.time.toEpochMilli
import com.ivy.data.ThemeOld
import com.ivy.data.loan.LoanType
import com.ivy.data.planned.IntervalType
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.data.user.AuthProviderType
import java.time.LocalDateTime
import java.util.*

@Deprecated("old")
@SuppressWarnings("unused")
class RoomTypeConverters {
    @TypeConverter
    fun saveDate(localDateTime: LocalDateTime?): Long? = localDateTime?.toEpochMilli(
        deviceTimeProvider()
    )

    @TypeConverter
    fun parseDate(timestampMillis: Long?): LocalDateTime? = timestampMillis?.epochMilliToDateTime()

    @TypeConverter
    fun saveUUID(id: UUID?) = id?.toString()

    @TypeConverter
    fun parseUUID(id: String?) = id?.let { UUID.fromString(id) }

    @TypeConverter
    fun saveTheme(value: ThemeOld?) = value?.name

    @TypeConverter
    fun parseTheme(value: String?) = value?.let { ThemeOld.valueOf(it) }

    @TypeConverter
    fun saveTransactionType(value: TrnTypeOld?) = value?.name

    @TypeConverter
    fun parseTransactionType(value: String?) = value?.let { TrnTypeOld.valueOf(it) }

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