package com.ivy.core.domain.calculation.history.data

import com.ivy.core.data.calculation.RawStats
import com.ivy.core.domain.api.data.period.Collapsable
import java.time.LocalDate

data class RawDateDivider(
    val date: LocalDate,
    val stats: RawStats,
    override val sectionId: String
) : Collapsable