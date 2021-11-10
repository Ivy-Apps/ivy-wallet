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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.model.IntervalType
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.onboarding.model.LastNTimeRange
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.CircleButtonFilled
import com.ivy.wallet.ui.theme.components.IntervalPickerRow
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.ui.theme.modal.model.Month
import com.ivy.wallet.ui.theme.modal.model.Month.Companion.fromMonthValue
import com.ivy.wallet.ui.theme.modal.model.Month.Companion.monthsList
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

    val ivyContext = LocalIvyContext.current
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
            selectedMonth = period?.month
        ) {
            period = TimePeriod(
                month = it
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
    selectedMonth: Month?,
    onSelected: (Month) -> Unit,
) {
    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = "Choose month",
        style = Typo.body1.style(
            color = if (selectedMonth != null) IvyTheme.colors.pureInverse else Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(24.dp))

    val months = monthsList()

    val state = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()
    onScreenStart {
        if (selectedMonth != null) {
            val selectedMonthIndex = months.indexOf(selectedMonth)
            if (selectedMonthIndex != -1) {
                coroutineScope.launch {
                    state.scrollToItem(selectedMonthIndex)
                }
            }
        } else {
            val currentMonthIndex = months.indexOf(fromMonthValue(dateNowUTC().monthValue))
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

        items(items = months) { month ->
            MonthButton(
                selected = month == selectedMonth,
                text = month.name
            ) {
                onSelected(month)
            }

            Spacer(Modifier.width(12.dp))
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
    val background = if (selected) GradientIvy else Gradient.solid(IvyTheme.colors.medium)
    Text(
        modifier = modifier
            .clip(Shapes.roundedFull)
            .background(
                brush = background.asHorizontalBrush(),
                shape = Shapes.roundedFull
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 24.dp)
            .padding(
                top = 10.dp,
                bottom = 14.dp
            ),
        text = text,
        style = Typo.body2.style(
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
        text = "or custom range",
        style = Typo.body1.style(
            color = if (timeRange != null) IvyTheme.colors.pureInverse else Gray,
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
    val ivyContext = LocalIvyContext.current

    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clip(Shapes.roundedFull)
            .border(2.dp, IvyTheme.colors.medium, Shapes.roundedFull)
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
                    initialDate = null
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
                    top = 14.dp,
                    bottom = 18.dp
                ),
            text = if (border == IntervalBorder.FROM) "From" else "To",
            style = Typo.body2.style(
                fontWeight = FontWeight.ExtraBold,
                color = if (dateTime != null) Green else IvyTheme.colors.pureInverse
            )
        )

        if (dateTime != null) {
            Spacer(Modifier.width(16.dp))
        } else {
            Spacer(Modifier.weight(1f))
        }

        Text(
            text = dateTime?.toLocalDate()?.formatDateOnlyWithYear() ?: "Add date",
            style = Typo.numberBody2.style(
                fontWeight = FontWeight.Bold,
                color = if (dateTime != null) IvyTheme.colors.pureInverse else Gray
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
        text = "or in the last",
        style = Typo.body1.style(
            color = if (lastNTimeRange != null) IvyTheme.colors.pureInverse else Gray,
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
        timeRange.to != null && timeRange.to.isAfter(timeNowUTC())

    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = "or all time",
        style = Typo.body1.style(
            color = if (active) IvyTheme.colors.pureInverse else Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(16.dp))

    MonthButton(
        modifier = Modifier.padding(start = 32.dp),
        selected = active,
        text = if (active) "Unselect All Time" else "Select All Time"
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
    IvyAppPreview {
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
    IvyAppPreview {
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
    IvyAppPreview {
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