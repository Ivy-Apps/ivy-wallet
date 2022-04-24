package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R

import com.ivy.wallet.ui.theme.Red
import java.util.*

@Composable
fun BoxWithConstraintsScope.DeleteModal(
    id: UUID = UUID.randomUUID(),
    title: String,
    description: String,
    visible: Boolean,
    buttonText: String = stringResource(R.string.delete),
    iconStart: Int = R.drawable.ic_delete,
    dismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalNegativeButton(
                text = buttonText,
                iconStart = iconStart
            ) {
                onDelete()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = title,
            style = UI.typo.b1.style(
                color = Red,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = description,
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(Modifier.height(48.dp))
    }
}