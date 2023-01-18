package com.ivy.core.ui.algorithm.trnhistory.data.raw

import com.ivy.core.domain.algorithm.calc.data.RawStats

data class RawDueDivider(
    override val id: String,
    val type: RawDividerType,
    val rawStats: RawStats,
) : TrnListRawSectionKey

enum class RawDividerType {
    Upcoming, Overdue
}