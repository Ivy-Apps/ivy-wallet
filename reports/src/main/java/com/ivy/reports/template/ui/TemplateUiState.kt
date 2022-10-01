package com.ivy.reports.template.ui

import com.ivy.data.account.Account
import com.ivy.reports.data.ReportCategoryType
import com.ivy.reports.template.data.TemplateLabels

data class TemplateUiState(
    val title: String,
    val accounts: List<Account>,
    val categories: List<ReportCategoryType>,
    val compulsoryContent: Map<TemplateLabels, String>,
    val optionalContent: Map<TemplateLabels, String> = emptyMap()
) {
    companion object {
        fun empty() = TemplateUiState("", emptyList(), emptyList(), emptyMap())
    }
}


