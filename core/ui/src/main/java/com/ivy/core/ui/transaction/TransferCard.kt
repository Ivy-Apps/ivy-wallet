package com.ivy.core.ui.transaction

//
//@Composable
//private fun TransferHeader(
//    account: Account,
//    toAccount: Account
//) {
//    @Composable
//    fun Account.IconName() {
//        icon.ItemIcon(
//            size = IconSize.S,
//            tint = UI.colors.pureInverse,
//        )
//        SpacerHor(width = 4.dp)
//        IvyText(
//            text = name,
//            typo = UI.typo.c.style(
//                color = UI.colors.pureInverse,
//                fontWeight = FontWeight.ExtraBold
//            )
//        )
//    }
//
//    Row(
//        modifier = Modifier
//            .background(UI.colors.pure, UI.shapes.rFull)
//            .padding(start = 8.dp, end = 20.dp)
//            .padding(vertical = 4.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        account.IconName()
//
//        Spacer(Modifier.width(12.dp))
//        IvyIcon(icon = R.drawable.ic_arrow_right)
//        Spacer(Modifier.width(8.dp))
//
//        toAccount.IconName()
//    }
//}

//@Composable
//private fun TransferAmountDifferentCurrency(
//    type: TransactionType,
//    value: Value
//) {
//    if (type is TransactionType.Transfer &&
//        value.currency != type.toValue.currency
//    ) {
//        val toAmountFormatted = type.toValue.formatAmount(shortenBigNumbers = false)
//
//        Text(
//            modifier = Modifier.padding(start = 54.dp),
//            text = "$toAmountFormatted ${type.toValue.currency}",
//            style = UI.typo.nB2.style(
//                color = Gray,
//                fontWeight = FontWeight.Normal
//            )
//        )
//    }
//}