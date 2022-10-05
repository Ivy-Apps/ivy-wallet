package com.ivy.formula.persistence.entity.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "project_formula_links")
data class ProjectFormulaLinkEntity(
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    val id: String,
    @ColumnInfo(name = "projectId", index = true)
    val projectId: String,
    @ColumnInfo(name = "formulaId", index = true)
    val formulaId: String,
)