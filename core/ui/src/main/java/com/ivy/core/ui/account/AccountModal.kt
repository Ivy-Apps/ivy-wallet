package com.ivy.core.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.modal.IvyModal
import com.ivy.core.ui.modal.Modal
import com.ivy.core.ui.temp.Preview
import com.ivy.data.account.Account
import com.ivy.design.l0_system.UI


@Composable
fun BoxScope.AccountModal(
    modal: IvyModal,
    account: Account?
) {
    Modal(
        modal = modal,
        Actions = { /*TODO*/ }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.red)
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_NewAccount() {
    val modal = IvyModal()
    modal.show()

    Preview {
        AccountModal(
            modal = modal,
            account = null,
        )
    }
}