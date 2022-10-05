package com.ivy.formula.persistence.entity.formula

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "formulas")
data class FormulaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    val id: String,
    // TODO:
)