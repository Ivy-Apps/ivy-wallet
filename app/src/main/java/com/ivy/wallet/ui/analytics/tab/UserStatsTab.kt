package com.ivy.wallet.ui.analytics.tab

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.analytics.DataCircle
import com.ivy.wallet.ui.analytics.model.UserStats
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.IvyDark
import com.ivy.wallet.ui.theme.Red

@Composable
fun ColumnScope.UserStatsTab(
    onboardedUsersGoogle: Int,
    userStats: UserStats
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = userStats.powerUsers,
            metric = "Power users",
            circleColor = Ivy,
            parentCount = onboardedUsersGoogle,
            avgCount = userStats.avgPowerUsersTrns
        )

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = userStats.activeUsers,
            metric = "Active users",
            circleColor = IvyDark,
            parentCount = onboardedUsersGoogle,
            avgCount = userStats.avgActiveUsersTrns
        )
        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = userStats.dyingUsers,
            metric = "Dying users",
            circleColor = Red,
            parentCount = onboardedUsersGoogle,
            avgCount = userStats.avgDyingUsers
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        Column(Modifier.fillMaxSize()) {
            UserStatsTab(
                onboardedUsersGoogle = 1000,
                userStats = UserStats(
                    powerUsers = 50,
                    avgPowerUsersTrns = 15.1,
                    activeUsers = 100,
                    avgActiveUsersTrns = 6.12,
                    dyingUsers = 300,
                    avgDyingUsers = 1.0
                )
            )
        }
    }
}