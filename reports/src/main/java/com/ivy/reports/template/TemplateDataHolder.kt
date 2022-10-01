package com.ivy.reports.template

import com.ivy.reports.template.data.Template
import com.ivy.reports.template.data.TemplatePeriodRule

data class TemplateDataHolder(
    val templateList: List<Template> = emptyList(),
    val selectedTemplate: Template?=null,
    val alternativePeriodRule: TemplatePeriodRule?=null
) {
    companion object {
        fun empty() = TemplateDataHolder(
            templateList = emptyList(),
            selectedTemplate = null,
            alternativePeriodRule = null
        )
    }
}
