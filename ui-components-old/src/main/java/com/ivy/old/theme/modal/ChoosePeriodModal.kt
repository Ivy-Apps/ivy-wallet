package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.*
import com.ivy.base.Month.Companion.fromMonthValue
import com.ivy.base.Month.Companion.monthsList
import com.ivy.base.R
import com.ivy.data.planned.IntervalType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.CircleButtonFilled
import com.ivy.wallet.ui.theme.components.IntervalPickerRow
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.utils.addKeyboardListener
import com.ivy.wallet.utils.dateNowUTC
import com.ivy.wallet.utils.formatDateOnlyWithYear
import com.ivy.wallet.utils.timeNowUTC
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

data class ChoosePeriodModalData(
    val id: UUID = UUID.randomUUID(),
    val period: TimePeriod
)

@Composable
fun BoxWithConstraintsScope.ChoosePeriodModal(
    modal: ChoosePeriodModalData?,

    dismiss: () -> Unit,
    onPeriodSelected: (TimePeriod) -> Unit
) {
    var period by remember(modal) {
        mutableStateOf(modal?.period)
    }

    val ivyContext = ivyWalletCtx()
    val modalScrollState = rememberScrollState()

    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        scrollState = modalScrollState,
        PrimaryAction = {
            ModalSet(
                enabled = period != null && period!!.isValid()
            ) {
                if (period != null) {
                    ivyContext.updateSelectedPeriodInMemory(period!!)
                    dismiss()
                    onPeriodSelected(period!!)
                }
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ChooseMonth(
            selectedMonthYear = period?.month?.let {
                MonthYear(month = it, year = period?.year ?: dateNowUTC().year)
            }
        ) {
            period = TimePeriod(
                month = it.month,
                year = it.year
            )
        }

        Spacer(Modifier.height(32.dp))

        IvyDividerLine(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(32.dp))

        FromToRange(
            timeRange = period?.fromToRange
        ) {
            period = TimePeriod(
                fromToRange = it
            )
        }

        Spacer(Modifier.height(32.dp))

        LastNPeriod(
            modalScrollState = modalScrollState,
            lastNTimeRange = period?.lastNRange,
        ) {
            period = TimePeriod(
                lastNRange = it
            )
        }

        Spacer(Modifier.height(32.dp))

        AllTime(
            timeRange = period?.fromToRange
        ) {
            period = TimePeriod(
                fromToRange = it
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ChooseMonth(
    selectedMonthYear: MonthYear?,
    onSelected: (MonthYear) -> Unit,
) {
    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = stringResource(R.string.choose_month),
        style = UI.typo.b1.style(
            color = if (selectedMonthYear != null) UI.colors.pureInverse else Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(24.dp))

    val currentYear = dateNowUTC().year
    val months = remember(currentYear) {
        monthsList()
            .map {
                MonthYear(month = it, year = currentYear - 1)
            }
            .plus(
                monthsList().map { MonthYear(month = it, year = currentYear) }
            )
            .plus(
                monthsList().map { MonthYear(month = it, year = currentYear + 1) }
            )
    }

    val state = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()
    onScreenStart {
        if (selectedMonthYear != null) {
            val selectedMonthIndex = months.indexOf(selectedMonthYear)
            if (selectedMonthIndex != -1) {
                coroutineScope.launch {
                    state.scrollToItem(selectedMonthIndex)
                }
            }
        } else {
            val currentMonthYear = MonthYear(
                month = fromMonthValue(dateNowUTC().monthValue),
                year = currentYear
            )
            val currentMonthIndex = months.indexOf(currentMonthYear)
            if (currentMonthIndex != -1) {
                coroutineScope.launch {
                    state.scrollToItem(currentMonthIndex)
                }
            }
        }
    }

    LazyRow(
        state = state,
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            Spacer(Modifier.width(12.dp))
        }

        items(items = months) { monthYear ->
            MonthButton(
                selected = monthYear == selectedMonthYear,
                text = monthYear.forDisplay(currentYear = currentYear)
            ) {
                onSelected(monthYear)
            }

            Spacer(Modifier.width(12.dp))
        }
    }
}

data class MonthYear(
    val month: Month,
    val year: Int
) {
    fun forDisplay(
        currentYear: Int
    ): String {
        return if (year != currentYear) {
            //not current year
            "${month.name}, $year"
        } else {
            //current year
            month.name
        }
    }
}

@Composable
private fun MonthButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    val background = if (selected) GradientIvy else Gradient.solid(UI.colors.medium)
    Text(
        modifier = modifier
            .clip(UI.shapes.rFull)
            .background(
                brush = background.asHorizontalBrush(),
                shape = UI.shapes.rFull
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 24.dp)
            .padding(
                vertical = 12.dp,
            ),
        text = text,
        style = UI.typo.b2.style(
            fontWeight = FontWeight.Bold,
            color = if (selected) White else Gray
        )
    )
}

@Composable
private fun FromToRange(
    timeRange: FromToTimeRange?,
    onSelected: (FromToTimeRange?) -> Unit,
) {
    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = stringResource(R.string.or_custom_range),
        style = UI.typo.b1.style(
            color = if (timeRange != null) UI.colors.pureInverse else Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(16.dp))

    IntervalFromToDate(
        border = IntervalBorder.FROM,
        dateTime = timeRange?.from,
        otherEndDateTime = timeRange?.to
    ) { from ->
        onSelected(
            if (from == null && timeRange?.to == null) {
                null
            } else {
                timeRange?.copy(
                    from = from
                ) ?: FromToTimeRange(
                    from = from,
                    to = null
                )
            }
        )
    }

    Spacer(Modifier.height(12.dp))

    IntervalFromToDate(
        border = IntervalBorder.TO,
        dateTime = timeRange?.to,
        otherEndDateTime = timeRange?.from
    ) { to ->
        onSelected(
            if (timeRange?.from == null && to == null) {
                null
            } else {
                timeRange?.copy(
                    to = to
                ) ?: FromToTimeRange(
                    from = null,
                    to = to
                )
            }
        )
    }
}

@Composable
private fun IntervalFromToDate(
    border: IntervalBorder,
    dateTime: LocalDateTime?,
    otherEndDateTime: LocalDateTime?,
    onSelected: (LocalDateTime?) -> Unit
) {
    val ivyContext = ivyWalletCtx()

    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clip(UI.shapes.rFull)
            .border(2.dp, UI.colors.medium, UI.shapes.rFull)
            .clickable {
                ivyContext.datePicker(
                    minDate = if (border == IntervalBorder.TO)
                        otherEndDateTime
                            ?.toLocalDate()
                            ?.plusDays(1) else null,
                    maxDate = if (border == IntervalBorder.FROM)
                        otherEndDateTime
                            ?.toLocalDate()
                            ?.minusDays(1) else null,
                    initialDate = dateTime?.toLocalDate()
                ) {
                    onSelected(it.atStartOfDay())
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(32.dp))

        Text(
            modifier = Modifier
                .padding(
                    vertical = 16.dp,
                ),
            text = if (border == IntervalBorder.FROM) stringResource(R.string.from) else stringResource(
                R.string.to
            ),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.ExtraBold,
                color = if (dateTime != null) Green else UI.colors.pureInverse
            )
        )

        if (dateTime != null) {
            Spacer(Modifier.width(16.dp))
        } else {
            Spacer(Modifier.weight(1f))
        }

        Text(
            text = dateTime?.toLocalDate()?.formatDateOnlyWithYear()
                ?: stringResource(R.string.add_date),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Bold,
                color = if (dateTime != null) UI.colors.pureInverse else Gray
            )
        )

        if (dateTime != null) {
            Spacer(Modifier.weight(1f))

            CircleButtonFilled(
                icon = R.drawable.ic_dismiss
            ) {
                onSelected(null)
            }

            Spacer(Modifier.width(4.dp))
        } else {
            Spacer(Modifier.width(32.dp))
        }
    }
}

private enum class IntervalBorder {
    FROM, TO
}

@Composable
private fun LastNPeriod(
    modalScrollState: ScrollState,
    lastNTimeRange: LastNTimeRange?,

    onSelected: (LastNTimeRange) -> Unit
) {
    val rootView = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    onScreenStart {
        rootView.addKeyboardListener { keyboardShown ->
            if (keyboardShown) {
                coroutineScope.launch {
                    delay(200)
                    modalScrollState.animateScrollTo(modalScrollState.maxValue)
                }
            }
        }
    }

    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = stringResource(R.string.or_in_the_last),
        style = UI.typo.b1.style(
            color = if (lastNTimeRange != null) UI.colors.pureInverse else Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(16.dp))

    IntervalPickerRow(
        intervalN = lastNTimeRange?.periodN ?: 0,
        intervalType = lastNTimeRange?.periodType ?: IntervalType.WEEK,
        onSetIntervalN = {
            onSelected(
                lastNTimeRange?.copy(
                    periodN = it
                ) ?: LastNTimeRange(
                    periodN = it,
                    periodType = IntervalType.WEEK
                )
            )
        },
        onSetIntervalType = {
            onSelected(
                lastNTimeRange?.copy(
                    periodType = it
                ) ?: LastNTimeRange(
                    periodN = 1,
                    periodType = it
                )
            )
        }
    )
}

@Composable
private fun AllTime(
    timeRange: FromToTimeRange?,
    onSelected: (FromToTimeRange?) -> Unit,
) {
    val active = timeRange != null && timeRange.from == null &&
            timeRange.to != null && timeRange.to!!.isAfter(timeNowUTC())

    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = stringResource(R.string.or_all_time),
        style = UI.typo.b1.style(
            color = if (active) UI.colors.pureInverse else Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(16.dp))

    MonthButton(
        modifier = Modifier.padding(start = 32.dp),
        selected = active,
        text = if (active) stringResource(R.string.unselect_all_time) else stringResource(R.string.select_all_time)
    ) {
        onSelected(
            if (active) {
                null
            } else {
                FromToTimeRange(
                    from = null,
                    to = timeNowUTC().plusDays(1)
                )
            }
        )
    }
}

@Preview
@Composable
private fun Preview_MonthSelected() {
    IvyWalletPreview {
        ChoosePeriodModal(
            modal = ChoosePeriodModalData(
                period = TimePeriod(
                    month = fromMonthValue(3)
                )
            ),
            dismiss = {}
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_FromTo() {
    IvyWalletPreview {
        ChoosePeriodModal(
            modal = ChoosePeriodModalData(
                period = TimePeriod(
                    fromToRange = FromToTimeRange(
                        from = timeNowUTC(),
                        to = timeNowUTC().plusDays(35)
                    )
                )
            ),
            dismiss = {}
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_LastN() {
    IvyWalletPreview {
        ChoosePeriodModal(
            modal = ChoosePeriodModalData(
                period = TimePeriod(
                    lastNRange = LastNTimeRange(
                        periodN = 1,
                        periodType = IntervalType.WEEK
                    )
                )
            ),
            dismiss = {}
        ) {

        }
    }
}