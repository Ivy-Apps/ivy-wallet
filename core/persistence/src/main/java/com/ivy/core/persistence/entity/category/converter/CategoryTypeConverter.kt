package com.ivy.core.persistence.entity.category.converter

import androidx.room.TypeConverter
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType

class CategoryTypeConverter {
    // region CategoryState
    @TypeConverter
    fun ser(state: CategoryState): Int = state.code

    @TypeConverter
    fun categoryState(code: Int): CategoryState =
        CategoryState.fromCode(code) ?: CategoryState.Default
    // endregion

    // region CategoryType
    @TypeConverter
    fun ser(type: CategoryType): Int = type.code

    @TypeConverter
    fun categoryType(code: Int): CategoryType = CategoryType.fromCode(code)!!
    // endregion
}