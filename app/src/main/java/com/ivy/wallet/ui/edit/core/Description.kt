package com.ivy.wallet.ui.edit.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.wallet.R
import com.ivy.wallet.ui.IvyWalletComponentPreview
import com.ivy.wallet.ui.edit.PrimaryAttributeColumn
import com.ivy.wallet.ui.theme.components.AddPrimaryAttributeButton
import com.ivy.wallet.utils.isNotNullOrBlank
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun Description(
    description: String?,
    onAddDescription: () -> Unit,
    onEditDescription: (String) -> Unit
) {
    if (description.isNotNullOrBlank()) {
        DescriptionText(
            description = description!!,
            onClick = {
                onEditDescription(description)
            }
        )
    } else {
        AddPrimaryAttributeButton(
            icon = R.drawable.ic_description,
            text = "Add description",
            onClick = onAddDescription
        )
    }
}

@Composable
private fun DescriptionText(
    description: String,
    onClick: () -> Unit,
) {
    PrimaryAttributeColumn(
        icon = R.drawable.ic_description,
        title = "Description",
        onClick = onClick
    ) {
        Spacer(Modifier.height(12.dp))

        MarkdownText(
            modifier = Modifier
                .clickable {
                    onClick()
                }
                .padding(horizontal = 24.dp),
            markdown = description,
            textAlign = TextAlign.Left,
            color = UI.colors.pureInverse,
            fontSize = UI.typo.b2.fontSize,
            fontResource = R.font.raleway_medium
        )

        Spacer(Modifier.height(20.dp))
    }
}

@Preview
@Composable
private fun PreviewDescription_Empty() {
    IvyWalletComponentPreview {
        Description(
            description = null,
            onAddDescription = {}
        ) {

        }
    }
}

@Preview
@Composable
private fun PreviewDescription_withText() {
    IvyWalletComponentPreview {
        Description(
            description = "This is my sample description.",
            onAddDescription = {}
        ) {

        }
    }
}