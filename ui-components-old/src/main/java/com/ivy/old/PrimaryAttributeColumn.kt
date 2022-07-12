package com.ivy.transaction_details


import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.IvyWalletComponentPreview
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.base.R
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.utils.clickableNoIndication

@Composable
fun PrimaryAttributeColumn(
    @DrawableRes icon: Int,
    title: String,
    TitleRowExtra: (@Composable RowScope.() -> Unit)? = null,
    onClick: () -> Unit,
    Content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .border(2.dp, UI.colors.medium, UI.shapes.r4)
            .clickableNoIndication(onClick = onClick),
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IvyIcon(icon = icon)

            Spacer(Modifier.width(8.dp))

            Text(
                text = title,
                style = UI.typo.b2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            TitleRowExtra?.invoke(this)
        }

        Content()
    }
}

@Preview
@Composable
private fun PreviewPrimaryAttributeColumn() {
    IvyWalletComponentPreview {
        PrimaryAttributeColumn(
            icon = R.drawable.ic_description,
            title = stringResource(R.string.description),
            onClick = { }
        ) {
            Spacer(Modifier.height(12.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "This mode is not recommended for production use,\n" +
                        "as no stability/compatibility guarantees are given on\n" +
                        "compiler or generated code. Use it at your own risk!\n" +
                        "\n" +
                        "\n" +
                        "Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.\n" +
                        "Use '--warning-mode all' to show the individual deprecation warnings.\n" +
                        "See https://docs.gradle.org/7.0-rc-1/userguide/command_line_interface.html#sec:command_line_warnings",
                style = UI.typo.b2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}