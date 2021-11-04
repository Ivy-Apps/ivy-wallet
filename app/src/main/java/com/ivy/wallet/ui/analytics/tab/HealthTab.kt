package com.ivy.wallet.ui.analytics.tab

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.analytics.DataCircle
import com.ivy.wallet.ui.analytics.model.Health
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.IvyDark
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Red

@Composable
fun ColumnScope.HealthTab(
    onboardedUsers: Int,
    localAccountCount: Int?,
    health: Health
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = health.usersWithTransactionLast24h,
            metric = "Users made\ntrn last 24h",
            circleColor = Ivy,
            parentCount = onboardedUsers,
            avgCount = health.avgTransactionsLast24
        )

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = health.usersWithTransactionLast7Days,
            metric = "Users made\ntrn last 7 days",
            circleColor = IvyDark,
            parentCount = onboardedUsers,
            avgCount = health.avgTransactionsLast7Days
        )

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = health.usersWithTransactionLast30Days,
            metric = "Users made\ntrn last 30 days",
            circleColor = Orange,
            parentCount = onboardedUsers,
            avgCount = health.avgTransactionsLast30Days
        )

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = health.loggedFromSettings,
            metric = "Login from Settings",
            circleColor = Red,
            parentCount = localAccountCount,
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        Column(Modifier.fillMaxSize()) {
            HealthTab(
                onboardedUsers = 1024,
                localAccountCount = 120,
                health = Health(
                    usersWithTransactionLast24h = 24,
                    avgTransactionsLast24 = 2.1,

                    usersWithTransactionLast7Days = 108,
                    avgTransactionsLast7Days = 16.7,

                    usersWithTransactionLast30Days = 130,
                    avgTransactionsLast30Days = 19.7,

                    loggedFromSettings = 3,
                )
            )
        }
    }
}