package com.ivy.formula.persistence.entity.datasource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_sources")
data class DataSourceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    val id: String,
    // TODO:
)