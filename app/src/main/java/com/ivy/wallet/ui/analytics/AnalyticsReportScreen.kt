package com.ivy.wallet.ui.analytics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.wallet.R
import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.formatDateOnly
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.analytics.model.Health
import com.ivy.wallet.ui.analytics.model.KPIs
import com.ivy.wallet.ui.analytics.model.OnboardingReport
import com.ivy.wallet.ui.analytics.model.UserStats
import com.ivy.wallet.ui.analytics.tab.HealthTab
import com.ivy.wallet.ui.analytics.tab.KPIsTab
import com.ivy.wallet.ui.analytics.tab.OnboardingReportTab
import com.ivy.wallet.ui.analytics.tab.UserStatsTab
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.CircleButton
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import java.time.LocalDateTime

@Composable
fun BoxWithConstraintsScope.AnalyticsReport(screen: Screen.AnalyticsReport) {
    val viewModel: AnalyticsReportViewModel = viewModel()

    val selectedTab by viewModel.selectedTab.observeAsState(AnalyticsTab.KPIs)
    val startDate by viewModel.startDate.observeAsState()
    val endDate by viewModel.endDate.observeAsState()

    val onboardingReport by viewModel.onboardingReport.observeAsState()
    val kpis by viewModel.kpis.observeAsState()
    val health by viewModel.health.observeAsState()
    val userStats by viewModel.userStats.observeAsState()
    val loading by viewModel.loading.observeAsState(false)

    onScreenStart {
        viewModel.start()
    }

    UI(
        selectedTab = selectedTab,
        startDate = startDate,
        endDate = endDate,

        onboardingReport = onboardingReport,
        kpis = kpis,
        health = health,
        userStats = userStats,
        loading = loading,

        onRefresh = viewModel::start,
        onSetStartDate = viewModel::setStartDate,
        onSetEndDate = viewModel::setEndDate,
        onSelectTab = viewModel::selectTab,
        onNukeTestEvents = viewModel::nukeTestEvents
    )
}

@Composable
private fun UI(
    selectedTab: AnalyticsTab,
    startDate: LocalDateTime?,
    endDate: LocalDateTime?,

    onboardingReport: OnboardingReport?,
    kpis: KPIs?,
    health: Health?,
    userStats: UserStats?,
    loading: Boolean,

    onRefresh: () -> Unit = {},
    onSetStartDate: (LocalDateTime?) -> Unit = {},
    onSetEndDate: (LocalDateTime?) -> Unit = {},
    onSelectTab: (AnalyticsTab) -> Unit = {},
    onNukeTestEvents: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .align(Alignment.Start)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(24.dp))

            CircleButton(
                icon = R.drawable.ic_sync
            ) {
                onRefresh()
            }

            Spacer(Modifier.width(16.dp))

            val ivyContext = LocalIvyContext.current
            IvyOutlinedButton(
                iconStart = R.drawable.ic_calendar,
                text = startDate?.toLocalDate()?.formatDateOnly() ?: "Not set",
            ) {
                ivyContext.datePicker {
                    onSetStartDate(it.atStartOfDay())
                }
            }

            Spacer(Modifier.width(8.dp))

            IvyOutlinedButton(
                iconStart = R.drawable.ic_calendar,
                text = endDate?.toLocalDate()?.formatDateOnly() ?: "Not set",
            ) {
                ivyContext.datePicker {
                    onSetEndDate(it.atStartOfDay())
                }
            }

            Spacer(Modifier.width(8.dp))

            IvyOutlinedButton(
                iconStart = R.drawable.ic_calendar,
                text = "Today",
            ) {
                onSetStartDate(dateNowUTC().atStartOfDay())
                onSetEndDate(dateNowUTC().plusDays(1).atStartOfDay())
            }

            Spacer(Modifier.width(8.dp))

            IvyOutlinedButton(
                iconStart = R.drawable.ic_calendar,
                text = "Clear",
            ) {
                onSetStartDate(null)
                onSetEndDate(null)
            }

            Spacer(Modifier.width(8.dp))

            IvyOutlinedButton(
                iconStart = R.drawable.ic_delete,
                text = "Nuke Test",
            ) {
                onNukeTestEvents()
            }

            Spacer(Modifier.width(24.dp))
        }


        Spacer(Modifier.height(8.dp))

        if (loading || onboardingReport == null || kpis == null ||
            health == null || userStats == null
        ) {
            Text(
                text = "Loading...",
                style = Typo.h2.colorAs(Orange)
            )
        } else {
            Content(
                selectedTab = selectedTab,
                onboardingReport = onboardingReport,
                kpis = kpis,
                health = health,
                userStats = userStats,
                setSelectedTab = onSelectTab
            )
        }
    }
}

@Composable
private fun ColumnScope.Content(
    selectedTab: AnalyticsTab,
    onboardingReport: OnboardingReport,
    kpis: KPIs,
    health: Health,
    userStats: UserStats,

    setSelectedTab: (AnalyticsTab) -> Unit,
) {

    Spacer(Modifier.height(24.dp))

    when (selectedTab) {
        AnalyticsTab.KPIs -> KPIsTab(
            kpis = kpis,
            activeUsersCount = userStats.activeUsers + userStats.powerUsers,
            onboardingCompletedEventCount = onboardingReport.onboardingCompleted ?: 0,
            onboardingStartedEventCount = onboardingReport.onboardingStarted ?: 0
        )
        AnalyticsTab.Onboarding -> OnboardingReportTab(
            report = onboardingReport
        )
        AnalyticsTab.Health -> HealthTab(
            onboardedUsers = kpis.onboardedUsers,
            localAccountCount = onboardingReport.onboardingLocalAccount,
            health = health
        )
        AnalyticsTab.UserStats -> UserStatsTab(
            onboardedUsersGoogle = kpis.onboardedUsers,
            userStats = userStats
        )
    }

    Spacer(Modifier.height(24.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Spacer(Modifier.width(24.dp))

        IvyButton(
            text = "KPIs",
            backgroundGradient = if (selectedTab == AnalyticsTab.KPIs) GradientIvy else Gradient.solid(
                Gray
            )
        ) {
            setSelectedTab(AnalyticsTab.KPIs)
        }

        Spacer(Modifier.width(16.dp))

        IvyButton(
            text = "Onboarding",
            backgroundGradient = if (selectedTab == AnalyticsTab.Onboarding) GradientIvy else Gradient.solid(
                Gray
            )
        ) {
            setSelectedTab(AnalyticsTab.Onboarding)
        }

        Spacer(Modifier.width(16.dp))

        IvyButton(
            text = "Health",
            backgroundGradient = if (selectedTab == AnalyticsTab.Health) GradientIvy else Gradient.solid(
                Gray
            )
        ) {
            setSelectedTab(AnalyticsTab.Health)
        }

        Spacer(Modifier.width(16.dp))

        IvyButton(
            text = "User Stats",
            backgroundGradient = if (selectedTab == AnalyticsTab.UserStats) GradientIvy else Gradient.solid(
                Gray
            )
        ) {
            setSelectedTab(AnalyticsTab.UserStats)
        }

        Spacer(Modifier.width(24.dp))
    }

    Spacer(Modifier.height(12.dp))
}


@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        UI(
            selectedTab = AnalyticsTab.KPIs,
            startDate = dateNowUTC().atStartOfDay(),
            endDate = dateNowUTC().plusDays(1).atStartOfDay(),
            onboardingReport = null,
            health = null,
            userStats = null,
            kpis = null,
            loading = true
        )
    }
}
