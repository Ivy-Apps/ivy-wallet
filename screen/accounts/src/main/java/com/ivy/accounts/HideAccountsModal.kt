package com.ivy.accounts

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IconScale
import com.ivy.design.l1_buildingBlocks.IvyIconScaled
import com.ivy.legacy.data.model.AccountData
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvyCircleButton
import com.ivy.wallet.ui.theme.modal.IvyModal
import java.util.UUID
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.alpha
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.toComposeColor
import kotlinx.collections.immutable.ImmutableList

@Suppress("ModifierMissing")
@SuppressLint("ComposeModifierMissing")
@Composable
fun BoxScope.HideAccountsModal(
    visible: Boolean,
    initialItems: ImmutableList<AccountData>,
    onDismiss: () -> Unit,
    id: UUID = UUID.randomUUID(),
    onComplete: (List<Account>) -> Unit
) {
    val accounts: SnapshotStateList<Account> = remember(id) {
        mutableStateListOf<Account>().apply {
            addAll(initialItems.map { it.account })
        }
    }

    val onUpdateItemVisibility: (AccountId, Boolean) -> Unit =
        { id, isVisible ->
            var selectedIndex = -1
            accounts.forEachIndexed { index, account ->
                if (account.id == id) {
                    selectedIndex = index
                }
            }
            accounts[selectedIndex] = accounts[selectedIndex].copy(isVisible = !isVisible)
        }

    IvyModal(
        id = id,
        visible = visible,
        scrollState = null,
        dismiss = onDismiss,
        PrimaryAction = {
            IvyCircleButton(
                modifier = Modifier
                    .size(48.dp),
                backgroundGradient = GradientGreen,
                icon = R.drawable.ic_check,
                tint = White
            ) {
                onComplete.invoke(accounts)
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = stringResource(R.string.accounts),
            style = UI.typo.b1.style(
                UI.colors.pureInverse,
                FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(accounts) { account ->
                HideAccountsRow(
                    account = account,
                    onClick = onUpdateItemVisibility
                )
            }
            item {
                Spacer(Modifier.height(150.dp))
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun HideAccountsRow(
    account: Account,
    onClick: (AccountId, Boolean) -> Unit
) {
    val contrastColor = findContrastTextColor(account.color.value.toComposeColor())
    val alpha = if (account.isVisible) {
        1.0f
    } else {
        0.6f
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .alpha(alpha)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .background(color = account.color.value.toComposeColor()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        ItemIconSDefaultIcon(
            iconName = account.icon?.id,
            defaultIcon = R.drawable.ic_custom_account_s,
            tint = contrastColor
        )

        Spacer(Modifier.width(8.dp))

        Column(
            Modifier
                .weight(1f)
                .padding(top = 20.dp, bottom = 20.dp, end = 8.dp)
        ) {
            Text(
                text = account.name.value,
                style = UI.typo.b2.style(
                    color = contrastColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        IvyIconScaled(
            modifier = Modifier
                .size(size = 24.dp)
                .clickable {
                    onClick.invoke(account.id, account.isVisible)
                },
            icon = if (account.isVisible) R.drawable.ic_visible else R.drawable.ic_hidden,
            tint = contrastColor,
            iconScale = IconScale.M,
            padding = 0.dp
        )

        Spacer(Modifier.width(16.dp))
    }
}