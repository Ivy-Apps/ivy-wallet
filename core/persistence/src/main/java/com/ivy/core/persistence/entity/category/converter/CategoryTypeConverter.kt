package com.ivy.core.persistence.entity.category.converter

import androidx.room.TypeConverter
import com.ivy.data.category.CategoryState

class CategoryTypeConverter {
    // region CategoryState
    @TypeConverter
    fun ser(state: CategoryState): Int = state.code

    @TypeConverter
    fun categoryState(code: Int): CategoryState =
        CategoryState.fromCode(code) ?: CategoryState.Default
    // endregion
}