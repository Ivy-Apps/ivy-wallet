package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.planned.IntervalType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IntervalPickerRow
import com.ivy.wallet.ui.theme.components.IvyCircleButton
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class RecurringRuleModalData(
    val initialStartDate: LocalDateTime?,
    val initialIntervalN: Int?,
    val initialIntervalType: IntervalType?,
    val initialOneTime: Boolean = false,
    val id: UUID = UUID.randomUUID()
)

@Composable
fun BoxWithConstraintsScope.RecurringRuleModal(
    modal: RecurringRuleModalData?,

    dismiss: () -> Unit,
    onRuleChanged: (LocalDateTime, oneTime: Boolean, Int?, IntervalType?) -> Unit,
) {
    var startDate by remember(modal) {
        mutableStateOf(modal?.initialStartDate ?: timeNowUTC())
    }
    var oneTime by remember(modal) {
        mutableStateOf(modal?.initialOneTime ?: false)
    }
    var intervalN by remember(modal) {
        mutableStateOf(modal?.initialIntervalN ?: 1)
    }
    var intervalType by remember(modal) {
        mutableStateOf(modal?.initialIntervalType ?: IntervalType.MONTH)
    }

    val modalScrollState = rememberScrollState()

    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        scrollState = modalScrollState,
        PrimaryAction = {
            ModalSet(
                modifier = Modifier.testTag("recurringModalSet"),
                enabled = validate(oneTime, intervalN, intervalType)
            ) {
                dismiss()
                onRuleChanged(
                    startDate,
                    oneTime,
                    intervalN,
                    intervalType
                )
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        val rootView = LocalView.current
        onScreenStart {
            hideKeyboard(rootView)
        }

        ModalTitle(text = stringResource(R.string.plan_for))

        Spacer(Modifier.height(16.dp))

        //One-time & Multiple Times
        TimesSelector(oneTime = oneTime) {
            oneTime = it
        }

        if (oneTime) {
            OneTime(
                date = startDate,
                onDatePicked = {
                    startDate = it
                }
            )
        } else {
            MultipleTimes(
                startDate = startDate,
                intervalN = intervalN,
                intervalType = intervalType,

                modalScrollState = modalScrollState,

                onSetStartDate = {
                    startDate = it
                },
                onSetIntervalN = {
                    intervalN = it
                },
                onSetIntervalType = {
                    intervalType = it
                }
            )
        }
    }
}

private fun validate(
    oneTime: Boolean,
    intervalN: Int?,
    intervalType: IntervalType?
): Boolean {
    return oneTime || intervalN != null && intervalN > 0 && intervalType != null
}

@Composable
private fun TimesSelector(
    oneTime: Boolean,

    onSetOneTime: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .background(UI.colors.medium, UI.shapes.r2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))

        TimesSelectorButton(
            selected = oneTime,
            label = stringResource(R.string.one_time)
        ) {
            onSetOneTime(true)
        }

        Spacer(Modifier.width(8.dp))

        TimesSelectorButton(
            selected = !oneTime,
            label = stringResource(R.string.multiple_times)
        ) {
            onSetOneTime(false)
        }

        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun RowScope.TimesSelectorButton(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .weight(1f)
            .clip(UI.shapes.rFull)
            .clickable {
                onClick()
            }
            .padding(vertical = 8.dp)
            .thenIf(selected) {
                background(GradientIvy.asHorizontalBrush(), UI.shapes.rFull)
            }
            .padding(vertical = 8.dp),
        text = label,
        style = UI.typo.b2.style(
            color = if (selected) White else Gray,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
private fun OneTime(
    date: LocalDateTime,
    onDatePicked: (LocalDateTime) -> Unit
) {
    Spacer(Modifier.height(44.dp))

    DateRow(dateTime = date) {
        onDatePicked(it)
    }

    Spacer(Modifier.height(64.dp))
}

@Composable
private fun MultipleTimes(
    startDate: LocalDateTime,
    intervalN: Int,
    intervalType: IntervalType,

    modalScrollState: ScrollState,

    onSetStartDate: (LocalDateTime) -> Unit,
    onSetIntervalN: (Int) -> Unit,
    onSetIntervalType: (IntervalType) -> Unit
) {
    Spacer(Modifier.height(40.dp))

    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = stringResource(R.string.starts_on),
        style = UI.typo.b2.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(12.dp))

    DateRow(dateTime = startDate) {
        onSetStartDate(it)
    }

    Spacer(Modifier.height(32.dp))

    IvyDividerLine(
        modifier = Modifier.padding(horizontal = 24.dp)
    )

    Spacer(Modifier.height(32.dp))

    Text(
        modifier = Modifier
            .padding(start = 32.dp),
        text = stringResource(R.string.repeats_every_text),
        style = UI.typo.b2.style(
            fontWeight = FontWeight.ExtraBold,
            color = UI.colors.pureInverse
        )
    )

    Spacer(Modifier.height(16.dp))

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

    IntervalPickerRow(
        intervalN = intervalN,
        intervalType = intervalType,
        onSetIntervalN = onSetIntervalN,
        onSetIntervalType = onSetIntervalType
    )

    Spacer(Modifier.height(48.dp))
}

@Composable
private fun DateRow(
    dateTime: LocalDateTime,
    onDatePicked: (LocalDateTime) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(32.dp))

        val ivyContext = com.ivy.core.ui.temp.ivyWalletCtx()

        Column(
            modifier = Modifier.clickableNoIndication {
                ivyContext.pickDate(dateTime.toLocalDate(), onDatePicked)
            }
        ) {
            val date = dateTime.toLocalDate()
            val closeDay = date.closeDay()

            Text(
                text = closeDay ?: date.formatNicely(
                    pattern = "EEEE, dd MMM"
                ),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Normal,
                    color = UI.colors.pureInverse
                )
            )

            if (closeDay != null) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = date.formatDateWeekDayLong(),
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.SemiBold,
                        color = Gray
                    )
                )
            }
        }

        Spacer(Modifier.width(24.dp))
        Spacer(Modifier.weight(1f))

        IvyCircleButton(
            modifier = Modifier
                .size(48.dp)
                .testTag("recurring_modal_pick_date"),
            backgroundPadding = 4.dp,
            icon = R.drawable.ic_calendar,
            backgroundGradient = Gradient.solid(UI.colors.pureInverse),
            tint = UI.colors.pure
        ) {
            ivyContext.pickDate(dateTime.toLocalDate(), onDatePicked)
        }

        Spacer(Modifier.width(32.dp))
    }
}

private fun com.ivy.core.ui.temp.IvyWalletCtx.pickDate(
    initialDate: LocalDate,
    onDatePicked: (
        LocalDateTime
    ) -> Unit
) {
    datePicker(
        initialDate = initialDate
    ) {
        onDatePicked(it.atTime(12, 0))
    }
}


@Preview
@Composable
private fun Preview_oneTime() {
    com.ivy.core.ui.temp.Preview {
        BoxWithConstraints(Modifier.padding(bottom = 48.dp)) {

            RecurringRuleModal(
                modal = RecurringRuleModalData(
                    initialStartDate = null,
                    initialIntervalN = null,
                    initialIntervalType = null,
                    initialOneTime = true
                ),
                dismiss = {},
                onRuleChanged = { _, _, _, _ -> }
            )
        }
    }
}

@Preview
@Composable
private fun Preview_multipleTimes() {
    com.ivy.core.ui.temp.Preview {
        BoxWithConstraints(Modifier.padding(bottom = 48.dp)) {
            RecurringRuleModal(
                modal = RecurringRuleModalData(
                    initialStartDate = null,
                    initialIntervalN = null,
                    initialIntervalType = null,
                    initialOneTime = false
                ),
                dismiss = {},
                onRuleChanged = { _, _, _, _ -> }
            )
        }

    }
}