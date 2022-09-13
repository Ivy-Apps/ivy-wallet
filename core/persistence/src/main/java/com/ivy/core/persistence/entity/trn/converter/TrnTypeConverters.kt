package com.ivy.core.persistence.entity.trn.converter

import androidx.room.TypeConverter
import com.ivy.core.persistence.entity.trn.TrnTags
import com.ivy.core.persistence.entity.trn.TrnTimeType
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnType

class TrnTypeConverters {
    companion object {
        const val NONE = 0
    }

    // region BatchPurpose
    @TypeConverter
    fun ser(purpose: TrnPurpose?): Int = purpose?.code ?: NONE

    @TypeConverter
    fun purpose(code: Int): TrnPurpose? =
        code.takeIf { it != NONE }?.let(TrnPurpose::fromCode)
    // endregion

    // region TrnType
    @TypeConverter
    fun ser(type: TrnType): Int = type.code

    @TypeConverter
    fun trnType(code: Int): TrnType = TrnType.fromCode(code)
    // endregion

    // region TrnTimeType
    @TypeConverter
    fun ser(timeType: TrnTimeType): Int = timeType.code

    @TypeConverter
    fun trnTimeType(code: Int): TrnTimeType = TrnTimeType.fromCode(code)!!
    // endregion

    // region TrnTags
    @TypeConverter
    fun ser(trnTags: TrnTags?): String? = trnTags?.tagIds?.joinToString(separator = ",")

    @TypeConverter
    fun trnTags(str: String): TrnTags? =
        str.split(",")
            .takeIf { it.isNotEmpty() }
            ?.let { TrnTags(tagIds = it) }
    // endregion
}