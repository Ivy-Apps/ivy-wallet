package com.ivy.design.l2_components.modal.scope

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Measured

@Stable
class ModalActionsScopeImpl(
    private val scope: RowScope
) : ModalActionsScope {
    override fun Modifier.align(alignment: Alignment.Vertical): Modifier = with(scope) {
        this@align.align(alignment)
    }

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier = with(scope) {
        this@alignBy.alignBy(alignmentLineBlock)
    }

    override fun Modifier.alignBy(alignmentLine: HorizontalAlignmentLine): Modifier = with(scope) {
        this@alignBy.alignBy(alignmentLine)
    }

    override fun Modifier.alignByBaseline(): Modifier = with(scope) {
        this@alignByBaseline.alignByBaseline()
    }

    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier = with(scope) {
        this@weight.weight(weight, fill)
    }
}

@Stable
interface ModalActionsScope : RowScope