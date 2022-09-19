package com.ivy.design.l2_components.modal.scope

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.VerticalAlignmentLine

@Stable
class ModalScopeImpl(
    private val scope: ColumnScope
) : ModalScope {
    @Stable
    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier = with(scope) {
        this@align.align(alignment)
    }

    @Stable
    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier = with(scope) {
        this@alignBy.alignBy(alignmentLineBlock)
    }

    @Stable
    override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier = with(scope) {
        this@alignBy.alignBy(alignmentLine)
    }

    @Stable
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier = with(scope) {
        this@weight.weight(weight, fill)
    }
}

@Stable
interface ModalScope : ColumnScope