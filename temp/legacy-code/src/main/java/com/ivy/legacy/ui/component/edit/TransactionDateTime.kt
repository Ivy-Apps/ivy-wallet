package com.ivy.legacy.ui.component.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.api.LocalTimeConverter
import com.ivy.design.api.LocalTimeFormatter
import com.ivy.design.api.LocalTimeProvider
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.IvyWalletComponentPreview
import com.ivy.ui.R
import com.ivy.ui.time.TimeFormatter
import com.ivy.wallet.ui.theme.components.IvyIcon
import java.time.Instant

@Suppress("MultipleEmitters")
@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun TransactionDateTime(
    dateTime: Instant?,
    dueDateTime: Instant?,
    onEditDate: () -> Unit,
    onEditTime: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (dueDateTime == null || dateTime != null) {
        Spacer(modifier.height(12.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(UI.shapes.r4)
                .background(UI.colors.medium, UI.shapes.r4)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(16.dp))

            IvyIcon(icon = R.drawable.ic_calendar)

            Spacer(Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.created_on),
                style = UI.typo.b2.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.width(24.dp))
            Spacer(Modifier.weight(1f))

            val localDateTime = with(LocalTimeConverter.current) {
                (dateTime ?: LocalTimeProvider.current.utcNow()).toLocalDateTime()
            }
            val timeFormatter = LocalTimeFormatter.current
            Text(
                text = with(timeFormatter) {
                    localDateTime.format(TimeFormatter.Style.DateOnly(includeWeekDay = false))
                },
                style = UI.typo.nB2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.clickable {
                    onEditDate()
                }
            )

            Text(
                text = " " + with(timeFormatter) {
                    localDateTime.toLocalTime().format()
                },
                style = UI.typo.nB2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.clickable {
                    onEditTime()
                }
            )
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletComponentPreview {
        TransactionDateTime(
            dateTime = LocalTimeProvider.current.utcNow(),
            dueDateTime = null,
            onEditDate = {
            },
            onEditTime = {
            }
        )
    }
}
