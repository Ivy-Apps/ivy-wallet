package com.ivy.core.persistence.entity.tag.converter

import androidx.room.TypeConverter
import com.ivy.data.tag.TagState

class TagTypeConverter {
    // region TagState
    @TypeConverter
    fun ser(state: TagState): Int = state.code

    @TypeConverter
    fun tagState(code: Int): TagState = TagState.fromCode(code)!!
    // endregion
}