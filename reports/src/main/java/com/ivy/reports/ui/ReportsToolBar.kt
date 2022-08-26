package com.ivy.reports.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.core.ui.temp.RootScreen
import com.ivy.core.ui.temp.ivyWalletCtx
import com.ivy.frp.view.navigation.navigation
import com.ivy.reports.ReportScreenEvent
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.components.BackButtonType
import com.ivy.wallet.ui.theme.components.CircleButtonFilled
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.components.IvyToolbar
import com.ivy.wallet.utils.formatNicelyWithTime
import com.ivy.wallet.utils.timeNowUTC

@Composable
private fun ReportsToolBarWrapper(
    onExport: () -> Unit,
    onFilter: () -> Unit,
    onBack:() -> Unit
) {
    IvyToolbar(
        backButtonType = BackButtonType.CLOSE,
        onBack = onBack
    ) {
        Spacer(Modifier.weight(1f))

        //Export CSV
        IvyOutlinedButton(
            text = stringResource(R.string.export),
            iconTint = Green,
            textColor = Green,
            solidBackground = true,
            padding = 8.dp,
            iconStart = R.drawable.ic_export_csv,
            onClick = onExport
        )

        Spacer(Modifier.width(16.dp))

        //Filter
        CircleButtonFilled(
            icon = R.drawable.ic_filter_xs,
            onClick = onFilter
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
fun ReportsToolBar(onEventHandler: (ReportScreenEvent) -> Unit) {
    val ivyContext = ivyWalletCtx()
    val nav = navigation()
    val context = LocalContext.current
    ReportsToolBarWrapper(
        onExport = {
            val fileName =
                "Report (${timeNowUTC().formatNicelyWithTime(noWeekDay = true)}).csv"

            ivyContext.createNewFile(fileName) { fileUri ->
                onEventHandler(
                    ReportScreenEvent.OnExportNew(
                        context = context,
                        fileUri = fileUri,
                        onFinish = {
                            (context as RootScreen).shareCSVFile(it)
                        }
                    )
                )
            }
        },
        onFilter = {
            onEventHandler.invoke(
                ReportScreenEvent.OnFilterOverlayVisible(
                    filterOverlayVisible = true
                )
            )
        },
        onBack = {
            nav.back()
        }
    )
}