package com.ivy.reports.data

import com.ivy.data.category.Category

sealed class ReportsCatType {
    data class Cat(val cat: Category) : ReportsCatType()
    object None : ReportsCatType()
}
