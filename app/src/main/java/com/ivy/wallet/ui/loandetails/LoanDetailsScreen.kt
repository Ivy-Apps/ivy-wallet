package com.ivy.wallet.ui.loandetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsHeight
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.model.entity.LoanRecord
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.statistic.level2.ItemStatisticToolbar
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.ItemIconMDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.ProgressBar
import com.ivy.wallet.ui.theme.modal.DeleteModal
import com.ivy.wallet.ui.theme.modal.LoanModalData

@Composable
fun BoxWithConstraintsScope.LoanDetailsScreen(screen: Screen.LoanDetails) {
    val viewModel: LoanDetailsViewModel = viewModel()

    val baseCurrency by viewModel.baseCurrency.collectAsState()
    val loan by viewModel.loan.collectAsState()
    val loanRecords by viewModel.loanRecords.collectAsState()
    val amountPaid by viewModel.amountPaid.collectAsState()

    onScreenStart {
        viewModel.start(screen = screen)
    }

    UI(
        baseCurrency = baseCurrency,
        loan = loan,
        loanRecords = loanRecords,
        amountPaid = amountPaid
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    baseCurrency: String,
    loan: Loan?,
    loanRecords: List<LoanRecord>,
    amountPaid: Double,

    onDelete: () -> Unit = {}
) {
    val ivyContext = LocalIvyContext.current
    val itemColor = loan?.color?.toComposeColor() ?: Gray

    var deleteModalVisible by remember { mutableStateOf(false) }
    var loanModalData: LoanModalData? by remember { mutableStateOf(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(itemColor)
    ) {
        val listState = rememberLazyListState()
        val density = LocalDensity.current

        Spacer(Modifier.statusBarsHeight())

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .clip(Shapes.rounded32Top)
                .background(IvyTheme.colors.pure),
            state = listState,
        ) {
            item {
                if (loan != null) {
                    Header(
                        loan = loan,
                        baseCurrency = baseCurrency,
                        amountPaid = amountPaid,
                        itemColor = itemColor,
                        onAmountClick = {
                            //TODO: Handle
                        },
                        onDelete = {
                            deleteModalVisible = true
                        },
                        onEdit = {
                            //TODO: Handle
                        },
                    )
                }
            }

            item {
                //Rounded corners top effect
                Spacer(
                    Modifier
                        .height(32.dp)
                        .fillMaxWidth()
                        .background(itemColor) //itemColor is displayed below the clip
                        .background(IvyTheme.colors.pure, Shapes.rounded32Top)
                )
            }

            loanRecords(
                loanRecords = loanRecords
            )
        }
    }

    DeleteModal(
        visible = deleteModalVisible,
        title = "Confirm deletion",
        description = "Note: Deleting this loan will remove it permanently and delete all associated loan records with it.",
        dismiss = { deleteModalVisible = false }
    ) {
        onDelete()
    }
}

@Composable
private fun Header(
    loan: Loan,
    baseCurrency: String,
    amountPaid: Double,
    itemColor: Color,

    onAmountClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val contrastColor = findContrastTextColor(itemColor)

    val darkColor = isDarkColor(itemColor)
    setStatusBarDarkTextCompat(darkText = !darkColor)

    Column(
        modifier = Modifier.background(itemColor)
    ) {
        Spacer(Modifier.height(20.dp))

        ItemStatisticToolbar(
            contrastColor = contrastColor,
            onEdit = onEdit,
            onDelete = onDelete
        )

        Spacer(Modifier.height(24.dp))

        LoanItem(
            loan = loan,
            contrastColor = contrastColor,
        ) {
            onEdit()
        }

        BalanceRow(
            modifier = Modifier
                .padding(start = 32.dp)
                .testTag("balance")
                .clickableNoIndication {
                    onAmountClick()
                },
            textColor = contrastColor,
            currency = baseCurrency,
            balance = loan.amount,
        )


        Spacer(Modifier.height(20.dp))

        LoanInfoCard(
            loan = loan,
            baseCurrency = baseCurrency,
            amountPaid = amountPaid,
            onAddRecord = {
                //TODO: Handle add loan record
            }
        )

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun LoanItem(
    loan: Loan,
    contrastColor: Color,

    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 22.dp)
            .clickableNoIndication {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemIconMDefaultIcon(
            iconName = loan.icon,
            defaultIcon = R.drawable.ic_custom_loan_m,
            tint = contrastColor
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = loan.name,
            style = Typo.body1.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(bottom = 12.dp),
            text = loan.humanReadableType(),
            style = Typo.caption.style(
                color = loan.color.toComposeColor().dynamicContrast()
            )
        )
    }
}

@Composable
private fun LoanInfoCard(
    loan: Loan,
    baseCurrency: String,
    amountPaid: Double,

    onAddRecord: () -> Unit
) {
    val backgroundColor = if (isDarkColor(loan.color))
        MediumBlack.copy(alpha = 0.9f) else MediumWhite.copy(alpha = 0.9f)

    val contrastColor = findContrastTextColor(backgroundColor)

    val percentPaid = amountPaid / loan.amount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .drawColoredShadow(
                color = backgroundColor,
                alpha = 0.1f
            )
            .background(backgroundColor, Shapes.rounded24),
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = "Paid",
            style = Typo.caption.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = "${amountPaid.format(baseCurrency)} / ${loan.amount.format(baseCurrency)}",
            style = Typo.numberBody1.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = IvyCurrency.fromCode(baseCurrency)?.name ?: "",
            style = Typo.body2.style(
                color = contrastColor,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(Modifier.height(12.dp))

        val leftToPay = loan.amount - amountPaid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${percentPaid.times(100).format(2)}%",
                style = Typo.numberBody1.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "${leftToPay.format(2)} BGN left to pay",
                style = Typo.numberBody2.style(
                    color = Gray,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        ProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .padding(horizontal = 24.dp),
            notFilledColor = IvyTheme.colors.pure,
            percent = percentPaid
        )

        Spacer(Modifier.height(24.dp))

        IvyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = "Add record",
            shadowAlpha = 0.1f,
            backgroundGradient = Gradient.solid(contrastColor),
            textStyle = Typo.body2.style(
                color = findContrastTextColor(contrastColor),
                fontWeight = FontWeight.Bold
            ),
            wrapContentMode = false
        ) {
            onAddRecord()
        }

        Spacer(Modifier.height(12.dp))
    }
}

fun LazyListScope.loanRecords(
    loanRecords: List<LoanRecord>
) {

}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        UI(
            baseCurrency = "BGN",
            loan = Loan(
                name = "Loan 1",
                amount = 4023.54,
                color = Red.toArgb(),
                type = LoanType.LEND
            ),
            loanRecords = emptyList(),
            amountPaid = 2032.12
        )
    }
}