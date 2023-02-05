package com.ivy.core.domain.calculation.history.data

import com.ivy.core.data.calculation.RawStats
import com.ivy.core.domain.api.data.period.Collapsable
import com.ivy.core.domain.api.data.period.DueDividerType

data class RawDueDivider(
    val stats: RawStats,
    val type: DueDividerType,
    override val sectionId: String
) : Collapsable