package com.ivy.core.domain.calculation.history.data

import com.ivy.core.domain.api.data.Collapsable
import com.ivy.core.domain.api.data.DueDividerType
import com.ivy.core.domain.data.RawStats

data class RawDueDivider(
    val stats: RawStats,
    val type: DueDividerType,
    override val sectionId: String
) : Collapsable