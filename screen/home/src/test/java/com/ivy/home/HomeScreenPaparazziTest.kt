package com.ivy.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.base.legacy.Theme
import com.ivy.legacy.data.AppBaseData
import com.ivy.legacy.data.BufferInfo
import com.ivy.legacy.data.LegacyDueSection
import com.ivy.legacy.ivyWalletCtx
import com.ivy.testing.PaparazziScreenshotTest
import com.ivy.testing.PaparazziTheme
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@RunWith(TestParameterInjector::class)
class HomeScreenPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `home snapshot`() {
        snapshot(theme) {
            BoxWithConstraints {
                HomeUi(
                    uiState = HomeState(
                        theme = Theme.AUTO,
                        name = "",
                        baseData = AppBaseData(
                            baseCurrency = "",
                            accounts = persistentListOf(),
                            categories = persistentListOf()
                        ),
                        balance = BigDecimal.ZERO,
                        buffer = BufferInfo(
                            amount = BigDecimal.ZERO,
                            bufferDiff = BigDecimal.ZERO,
                        ),
                        customerJourneyCards = persistentListOf(),
                        history = persistentListOf(),
                        stats = IncomeExpensePair.zero(),
                        upcoming = LegacyDueSection(
                            trns = persistentListOf(),
                            stats = IncomeExpensePair.zero(),
                            expanded = false,
                        ),
                        overdue = LegacyDueSection(
                            trns = persistentListOf(),
                            stats = IncomeExpensePair.zero(),
                            expanded = false,
                        ),
                        period = ivyWalletCtx().selectedPeriod,
                        hideBalance = false,
                        hideIncome = false,
                        expanded = false
                    ),
                    onEvent = {}
                )
            }
        }
    }
}