package com.ivy.core.ui.time

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Set

@Composable
fun BoxScope.PeriodModal(
    modal: IvyModal
) {
    Modal(
        modal = modal,
        actions = {
            Set {

            }
        }
    ) {

    }
}