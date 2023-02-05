package com.ivy.core.domain.calculation.history.data

import com.ivy.core.domain.api.data.Collapsable
import com.ivy.core.domain.data.RawStats
import java.time.LocalDate

data class RawDateDivider(
    val date: LocalDate,
    val stats: RawStats,
    override val sectionId: String
) : Collapsable