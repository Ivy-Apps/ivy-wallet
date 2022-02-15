package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.wallet.base.hideKeyboard
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.base.thenIf
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.components.ItemIconS
import com.ivy.wallet.ui.theme.dynamicContrast
import java.util.*


@Composable
fun BoxWithConstraintsScope.ChooseIconModal(
    visible: Boolean,
    initialIcon: String?,
    color: Color,

    id: UUID = UUID.randomUUID(),

    dismiss: () -> Unit,
    onIconChosen: (String?) -> Unit
) {
    var selectedIcon by remember(id) {
        mutableStateOf(initialIcon)
    }

    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        scrollState = null,
        includeActionsRowPadding = false,
        PrimaryAction = {
            ModalSave(
                modifier = Modifier.testTag("choose_icon_save")
            ) {
                onIconChosen(selectedIcon)
                dismiss()
            }
        }
    ) {
        val view = LocalView.current
        onScreenStart {
            hideKeyboard(view)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(Modifier.height(32.dp))

                ModalTitle(text = "Choose icon")

                Spacer(Modifier.height(32.dp))
            }

            icons(selectedIcon = selectedIcon, color = color) {
                selectedIcon = it
            }

            item {
                Spacer(Modifier.height(160.dp))
            }
        }
    }
}

private fun LazyListScope.icons(
    selectedIcon: String?,
    color: Color,

    onIconSelected: (String) -> Unit
) {
    val icons = listOf(
        "account", "category", "cash", "bank", "revolut",
        "clothes2", "clothes", "family", "star",
        "education", "fitness", "loan", "orderfood", "orderfood2",
        "pet", "restaurant", "selfdevelopment", "work", "vehicle",
        "atom", "bills", "birthday", "calculator", "camera",
        "chemistry", "coffee", "connect", "dna", "doctor",
        "document", "drink", "farmacy", "fingerprint", "fishfood",
        "food2", "fooddrink", "furniture", "gambling", "game",
        "gears", "gift", "groceries", "hairdresser", "health",
        "hike", "house", "insurance", "label", "leaf",
        "location", "makeup", "music", "notice", "people",
        "plant", "programming", "relationship", "rocket", "safe",
        "sail", "server", "shopping2", "shopping", "sports",
        "stats", "tools", "transport", "travel", "trees",
        "zeus", "calendar", "crown", "diamond", "palette"
//        "ada", "btc", "eth", "xrp", "doge"
    )

    val rowsCount = icons.size / 5 + 1

    for (row in 0 until rowsCount) {
        val toIndex = (row * 5) + 5
        val rowIcons = icons.subList(
            fromIndex = row * 5,
            toIndex = if (toIndex < icons.size - 1) toIndex else icons.size
        )

        item {
            IconsRow(
                icons = rowIcons, selectedIcon = selectedIcon, color = color
            ) {
                onIconSelected(it)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun IconsRow(
    icons: List<String>,
    selectedIcon: String?,
    color: Color,

    onIconSelected: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        for ((index, icon) in icons.withIndex()) {
            Icon(
                icon = icon,
                selected = selectedIcon == icon,
                color = color
            ) {
                onIconSelected(icon)
            }

            if (index < icons.lastIndex && icons.size >= 5) {
                Spacer(Modifier.weight(1f))
            } else {
                Spacer(Modifier.width(20.dp))
            }
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun Icon(
    icon: String,
    selected: Boolean,
    color: Color,

    onClick: () -> Unit,
) {
    ItemIconS(
        modifier = Modifier
            .clip(CircleShape)
            .border(2.dp, if (selected) color else UI.colors.medium, CircleShape)
            .thenIf(selected) {
                background(color, CircleShape)
            }
            .clickable {
                onClick()
            }
            .padding(all = 8.dp)
            .testTag(icon),
        iconName = icon,
        tint = if (selected) color.dynamicContrast() else UI.colors.mediumInverse
    )
}

@Preview
@Composable
private fun ChooseIconModal() {
    IvyAppPreview {
        ChooseIconModal(
            visible = true,
            initialIcon = "gift",
            color = Ivy,
            dismiss = {}
        ) {

        }
    }
}