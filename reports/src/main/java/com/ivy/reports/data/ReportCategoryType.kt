package com.ivy.reports.data

import com.ivy.data.category.Category

sealed class ReportCategoryType {
    data class Cat(val cat: Category) : ReportCategoryType()
    object None : ReportCategoryType()
}
