package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.Red
import java.util.*

@Composable
fun BoxWithConstraintsScope.ProgressModal(
    id: UUID = UUID.randomUUID(),
    title: String,
    description: String,
    visible: Boolean,
    color: Color = UI.colors.orange,
    dismiss: () -> Unit = {},
) {
    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {}
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

        Spacer(Modifier.height(24.dp))

        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(8.dp)
                .clip(
                    UI.shapes.rFull
                ),
            color = color
        )

        Spacer(Modifier.height(48.dp))
    }
}