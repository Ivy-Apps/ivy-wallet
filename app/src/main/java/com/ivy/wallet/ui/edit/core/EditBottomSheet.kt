package com.ivy.wallet.ui.edit.core

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.test.TestingContext
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.DURATION_MODAL_ANIM
import com.ivy.wallet.ui.theme.modal.ModalSave
import com.ivy.wallet.ui.theme.modal.ModalSet
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.utils.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

@Composable
fun BoxWithConstraintsScope.EditBottomSheet(
    initialTransactionId: UUID?,
    type: TransactionType,
    accounts: List<Account>,
    selectedAccount: Account?,
    toAccount: Account?,
    amount: Double,
    currency: String,
    convertedAmount: Double? = null,
    convertedAmountCurrencyCode: String? = null,

    amountModalShown: Boolean,
    setAmountModalShown: (Boolean) -> Unit,
    ActionButton: @Composable () -> Unit,

    onAmountChanged: (Double) -> Unit,
    onSelectedAccountChanged: (Account) -> Unit,
    onToAccountChanged: (Account) -> Unit,
    onAddNewAccount: () -> Unit
) {
    val rootView = LocalView.current
    var keyboardShown by remember { mutableStateOf(false) }

    onScreenStart {
        rootView.addKeyboardListener {
            keyboardShown = it
        }
    }

    val keyboardShownInsetDp by animateDpAsState(
        targetValue = densityScope {
            if (keyboardShown) keyboardOnlyWindowInsets().bottom.toDp() else 0.dp
        },
        animationSpec = tween(DURATION_MODAL_ANIM)
    )
    val navBarPadding by animateDpAsState(
        targetValue = densityScope {
            if (keyboardShown) 0.dp else navigationBarInsets().bottom.toDp()
        },
        animationSpec = tween(DURATION_MODAL_ANIM)
    )

    var bottomBarHeight by remember { mutableStateOf(0) }

    var internalExpanded by remember { mutableStateOf(true) }
    val expanded = internalExpanded && !keyboardShown

    val percentExpanded by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = springBounce()
    )
    val percentCollapsed = 1f - percentExpanded

    val showConvertedAmountText by remember(convertedAmount) {
        if (type == TransactionType.TRANSFER && convertedAmount != null && convertedAmountCurrencyCode != null)
            mutableStateOf("${convertedAmount.format(2)} $convertedAmountCurrencyCode")
        else
            mutableStateOf(null)
    }

    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 24.dp)
//            .drawColoredShadow(
//                color = UI.colors.mediumInverse,
//                alpha = if (UI.colors.isLight) 0.3f else 0.2f,
//                borderRadius = 24.dp,
//                shadowRadius = 24.dp
//            )
            .border(
                width = 2.dp,
                color = UI.colors.medium,
                shape = UI.shapes.r2Top
            )
            .background(UI.colors.pure, UI.shapes.r2Top)
            .verticalSwipeListener(
                sensitivity = Constants.SWIPE_UP_EXPANDED_THRESHOLD,
                onSwipeUp = {
                    hideKeyboard(rootView)
                    internalExpanded = true
                },
                onSwipeDown = {
                    internalExpanded = false
                }
            )
            .consumeClicks()
    ) {
        //Accounts label
        val label = when (type) {
            TransactionType.INCOME -> stringResource(R.string.add_money_to)
            TransactionType.EXPENSE -> stringResource(R.string.pay_with)
            TransactionType.TRANSFER -> stringResource(R.string.from)
        }

        SheetHeader(
            percentExpanded = percentExpanded,
            label = label,
            type = type,
            accounts = accounts,
            selectedAccount = selectedAccount,
            toAccount = toAccount,
            onSelectedAccountChanged = onSelectedAccountChanged,
            onToAccountChanged = onToAccountChanged,
            onAddNewAccount = onAddNewAccount
        )

        val spacerAboveAmount = lerp(40, 16, percentCollapsed)
        Spacer(Modifier.height(spacerAboveAmount.dp))

        if (type == TransactionType.TRANSFER && percentExpanded < 1f) {
            TransferRowMini(
                percentCollapsed = percentCollapsed,
                fromAccount = selectedAccount,
                toAccount = toAccount,
                onSetExpanded = {
                    internalExpanded = true
                }
            )
        }

        Amount(
            type = type,
            amount = amount,
            currency = currency,
            label = label,
            account = selectedAccount,
            showConvertedAmountText = showConvertedAmountText,
            percentExpanded = percentExpanded,
            onShowAmountModal = {
                setAmountModalShown(true)
            },
            onAccountMiniClick = {
                hideKeyboard(rootView)
                internalExpanded = true
            },
        )

        val lastSpacer = lerp(20f, 8f, percentCollapsed)
        if (lastSpacer > 0) {
            Spacer(Modifier.height(lastSpacer.dp))
        }
//
        //system stuff + keyboard padding
        Spacer(Modifier.height(densityScope { bottomBarHeight.toDp() }))
        Spacer(Modifier.height(keyboardShownInsetDp))
    }

    BottomBar(
        keyboardShown = keyboardShown,
        expanded = expanded,
        internalExpanded = internalExpanded,
        setInternalExpanded = {
            internalExpanded = it
        },
        setBottomBarHeight = {
            bottomBarHeight = it
        },

        keyboardShownInsetDp = keyboardShownInsetDp,
        navBarPadding = navBarPadding,

        ActionButton = ActionButton
    )

    val amountModalId = remember(initialTransactionId, amount) {
        UUID.randomUUID()
    }
    AmountModal(
        id = amountModalId,
        visible = amountModalShown,
        currency = currency,
        initialAmount = amount.takeIf { it > 0 },
        Header = {
            Spacer(Modifier.height(24.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = stringResource(R.string.account),
                style = UI.typo.b1.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(16.dp))

            AccountsRow(
                accounts = accounts,
                selectedAccount = selectedAccount,
                onSelectedAccountChanged = onSelectedAccountChanged,
                onAddNewAccount = onAddNewAccount,
                childrenTestTag = "amount_modal_account"
            )
        },
        amountSpacerTop = 48.dp,
        dismiss = {
            setAmountModalShown(false)
        }
    ) {
        onAmountChanged(it)
    }
}

@Composable
private fun BottomBar(
    keyboardShown: Boolean,
    keyboardShownInsetDp: Dp,
    setBottomBarHeight: (Int) -> Unit,
    expanded: Boolean,
    internalExpanded: Boolean,
    setInternalExpanded: (Boolean) -> Unit,
    navBarPadding: Dp,
    ActionButton: @Composable () -> Unit
) {
    val ivyContext = ivyWalletCtx()

    ActionsRow(
        modifier = Modifier
            .onSizeChanged {
                setBottomBarHeight(it.height)
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val systemOffsetBottom = keyboardShownInsetDp.toPx()
                val visibleHeight = placeable.height * 1f
                val y = ivyContext.screenHeight - visibleHeight - systemOffsetBottom

                layout(placeable.width, placeable.height) {
                    placeable.place(
                        0,
                        y.roundToInt()
                    )
                }
            }
//            .gradientCutBackground()
            .padding(bottom = 12.dp)
            .padding(bottom = navBarPadding),
        lineColor = UI.colors.medium
    ) {
        Spacer(Modifier.width(24.dp))

        val expandRotation by animateFloatAsState(
            targetValue = if (expanded) 0f else -180f,
            animationSpec = springBounce()
        )

        val rootView = LocalView.current
        CircleButton(
            modifier = Modifier.rotate(expandRotation),
            icon = R.drawable.ic_expand_more,
        ) {
            setInternalExpanded(!internalExpanded || keyboardShown)
            hideKeyboard(rootView)
        }

        Spacer(Modifier.weight(1f))

        ActionButton()

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun TransferRowMini(
    percentCollapsed: Float,
    fromAccount: Account?,
    toAccount: Account?,
    onSetExpanded: () -> Unit
) {
    Row(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val height = placeable.height * (percentCollapsed)

                layout(placeable.width, height.roundToInt()) {
                    placeable.placeRelative(
                        x = 0,
                        y = 0
                    )
                }
            }
            .alpha(percentCollapsed)
            .clickableNoIndication {
                onSetExpanded()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val fromColor = fromAccount?.color?.toComposeColor() ?: Ivy
        val fromContrastColor = findContrastTextColor(fromColor)
        IvyButton(
            text = fromAccount?.name ?: "Null",
            iconStart = R.drawable.ic_accounts,
            backgroundGradient = Gradient.solid(fromColor),
            iconTint = fromContrastColor,
            textStyle = UI.typo.b2.style(
                color = fromContrastColor,
                fontWeight = FontWeight.ExtraBold
            ),
            padding = 10.dp,
        ) {
            onSetExpanded()
        }

        IvyIcon(
            icon = R.drawable.ic_arrow_right,
            tint = UI.colors.pureInverse
        )

        val toColor = toAccount?.color?.toComposeColor() ?: Ivy
        val toContrastColor = findContrastTextColor(toColor)
        IvyButton(
            text = toAccount?.name ?: "Null",
            iconStart = R.drawable.ic_accounts,
            backgroundGradient = Gradient.solid(toColor),
            iconTint = toContrastColor,
            textStyle = UI.typo.b2.style(
                color = toContrastColor,
                fontWeight = FontWeight.ExtraBold
            ),
            padding = 10.dp,
        ) {
            onSetExpanded()
        }
    }

    val transferMiniBottomSpacer = 20 * percentCollapsed
    if (transferMiniBottomSpacer > 0f) {
        Spacer(modifier = Modifier.height(transferMiniBottomSpacer.dp))
    }
}

@Composable
private fun SheetHeader(
    percentExpanded: Float,
    label: String,
    type: TransactionType,
    accounts: List<Account>,
    selectedAccount: Account?,
    toAccount: Account?,
    onSelectedAccountChanged: (Account) -> Unit,
    onToAccountChanged: (Account) -> Unit,
    onAddNewAccount: () -> Unit,
) {
    if (percentExpanded > 0.01f) {
        Column(
            modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

//                    val x = lerp(0, ivyContext.screenWidth, (1f - percentExpanded))
                    val height = placeable.height * percentExpanded

                    layout(placeable.width, height.roundToInt()) {
                        placeable.placeRelative(
                            x = 0,
                            y = -(height * (1f - percentExpanded)).roundToInt(),
                        )
                    }
                }
                .alpha(percentExpanded)
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = label,
                style = UI.typo.b1.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(if (type == TransactionType.TRANSFER) 8.dp else 16.dp))

            AccountsRow(
                accounts = accounts,
                selectedAccount = selectedAccount,
                onSelectedAccountChanged = onSelectedAccountChanged,
                onAddNewAccount = onAddNewAccount,
                childrenTestTag = "from_account"
            )

            if (type == TransactionType.TRANSFER) {
                Spacer(Modifier.height(24.dp))

                Text(
                    modifier = Modifier.padding(start = 32.dp),
                    text = stringResource(R.string.to),
                    style = UI.typo.b1.style(
                        color = UI.colors.pureInverse,
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.height(8.dp))

                AccountsRow(
                    accounts = accounts,
                    selectedAccount = toAccount,
                    onSelectedAccountChanged = onToAccountChanged,
                    onAddNewAccount = onAddNewAccount,
                    childrenTestTag = "to_account",
                )
            }
        }
    }
}

@Composable
private fun AccountsRow(
    modifier: Modifier = Modifier,
    accounts: List<Account>,
    selectedAccount: Account?,
    childrenTestTag: String? = null,
    onSelectedAccountChanged: (Account) -> Unit,
    onAddNewAccount: () -> Unit
) {
    val lazyState = rememberLazyListState()

    LaunchedEffect(accounts, selectedAccount) {
        if (selectedAccount != null) {
            val selectedIndex = accounts.indexOf(selectedAccount)
            if (selectedIndex != -1) {
                launch {
                    if (TestingContext.inTest) return@launch //breaks UI tests

                    lazyState.scrollToItem(
                        index = selectedIndex, //+1 because Spacer width 24.dp
                    )
                }
            }
        }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        state = lazyState
    ) {
        item {
            Spacer(Modifier.width(24.dp))
        }

        itemsIndexed(accounts) { _, account ->
            Account(
                account = account,
                selected = selectedAccount == account,
                testTag = childrenTestTag ?: "account"
            ) {
                onSelectedAccountChanged(account)
            }
        }

        item {
            AddAccount {
                onAddNewAccount()
            }
        }

        item {
            Spacer(Modifier.width(24.dp))
        }
    }
}

@Composable
private fun Account(
    account: Account,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val accountColor = account.color.toComposeColor()
    val textColor =
        if (selected) findContrastTextColor(accountColor) else UI.colors.pureInverse

    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .thenIf(!selected) {
                border(2.dp, UI.colors.medium, UI.shapes.rFull)
            }
            .thenIf(selected) {
                background(accountColor, UI.shapes.rFull)
            }
            .clickable(onClick = onClick)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        ItemIconSDefaultIcon(
            iconName = account.icon,
            defaultIcon = R.drawable.ic_custom_account_s,
            tint = textColor
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = account.name,
            style = UI.typo.b2.style(
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }

    Spacer(Modifier.width(8.dp))
}

@Composable
private fun AddAccount(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .border(2.dp, UI.colors.medium, UI.shapes.rFull)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIcon(
            icon = R.drawable.ic_plus,
            tint = UI.colors.pureInverse
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = stringResource(R.string.add_account),
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }

    Spacer(Modifier.width(8.dp))
}

@Composable
private fun Amount(
    type: TransactionType,
    amount: Double,
    currency: String,
    percentExpanded: Float,
    label: String,
    account: Account?,
    showConvertedAmountText: String? = null,
    onShowAmountModal: () -> Unit,
    onAccountMiniClick: () -> Unit,
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val percentCollapsed = 1f - percentExpanded
        val integerFontSize = lerp(40, 30, percentCollapsed)
        val spacerInteger = lerp(4, 0, percentCollapsed)
        val currencyPaddingTop = lerp(8, 4, percentCollapsed)
        val currencyFontSize = lerp(30, 18, percentCollapsed)

        Spacer(Modifier.width(32.dp))

        if (percentExpanded > 0.01f) {
            Spacer(
                Modifier.weight(
                    (1f * percentExpanded).coerceAtLeast(0.01f)
                )
            )
        }

        Column() {
            BalanceRow(
                modifier = Modifier
                    .clickableNoIndication {
                        onShowAmountModal()
                    }
                    .testTag("edit_amount_balance_row"),
                currency = currency,
                balance = amount,

                decimalPaddingTop = currencyPaddingTop.dp,
                spacerDecimal = spacerInteger.dp,
                spacerCurrency = 8.dp,


                integerFontSize = integerFontSize.sp,
                decimalFontSize = 18.sp,
                currencyFontSize = currencyFontSize.sp,

                currencyUpfront = false
            )
            if (showConvertedAmountText != null) {
                Text(
                    text = showConvertedAmountText,
                    style = UI.typo.nB2.style(
                        color = UI.colors.pureInverse,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        if (percentExpanded < 1f && type != TransactionType.TRANSFER) {
            LabelAccountMini(
                percentExpanded = percentExpanded,
                label = label,
                account = account,
                onClick = onAccountMiniClick
            )
        }

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun LabelAccountMini(
    percentExpanded: Float,
    label: String,
    account: Account?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val width = placeable.width * (1f - percentExpanded)

                layout(width.roundToInt(), placeable.height) {
                    placeable.placeRelative(
                        x = 0,
                        y = 0
                    )
                }
            }
            .alpha(1f - percentExpanded)
            .clickableNoIndication(
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = UI.typo.nC.style(
                color = UI.colors.mediumInverse,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = account?.name?.toUpperCase(Locale.getDefault()) ?: "",
            style = UI.typo.nB2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        val acc1 = Account("Cash", color = Green.toArgb())

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            EditBottomSheet(
                amountModalShown = false,
                setAmountModalShown = {},
                initialTransactionId = null,
                type = TransactionType.INCOME,
                ActionButton = {
                    ModalSet() {

                    }
                },
                accounts = listOf(
                    acc1,
                    Account("DSK", color = GreenDark.toArgb()),
                    Account("phyre", color = GreenLight.toArgb()),
                    Account("Revolut", color = IvyDark.toArgb()),
                ),
                selectedAccount = acc1,
                toAccount = null,
                amount = 12350.0,
                currency = "BGN",
                onAmountChanged = {},
                onSelectedAccountChanged = {},
                onToAccountChanged = {},
                onAddNewAccount = {}
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Transfer() {
    IvyWalletPreview {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            EditBottomSheet(
                amountModalShown = false,
                setAmountModalShown = {},
                initialTransactionId = UUID.randomUUID(),
                ActionButton = {
                    ModalSave {

                    }
                },
                type = TransactionType.TRANSFER,
                accounts = listOf(
                    acc1,
                    acc2,
                    Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                    Account("Revolut", color = IvyDark.toArgb()),
                ),
                selectedAccount = acc1,
                toAccount = acc2,
                amount = 12350.0,
                currency = "BGN",
                onAmountChanged = {},
                onSelectedAccountChanged = {},
                onToAccountChanged = {},
                onAddNewAccount = {}
            )
        }
    }
}