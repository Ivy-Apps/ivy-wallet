package com.ivy.wallet.backup.github.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.design.l0_system.UI
import com.ivy.wallet.R

@Composable
fun GitHubBackupRequirementsCard(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = UI.colors.orange,
            contentColor = UI.colors.red1Inverse,
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                WarningIcon()
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    modifier = Modifier,
                    text = "Warning",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            val text = buildAnnotatedString {
                append(
                    "To ensure app can auto backup your data you must do the below:\n" +
                            "1. Disable "
                )

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("\"BATTERY OPTIMIZATION\"")
                }

                append(
                    " for this app in the settings.\n" +
                            "2. In some mobile models like Xiaomi and Vivo the "
                )

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("\"AUTOSTART\"")
                }

                append(" settings must be activated.")
            }

            Text(
                modifier = Modifier,
                text = text,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun WarningIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_add_due_date),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(LocalContentColor.current)
    )
}