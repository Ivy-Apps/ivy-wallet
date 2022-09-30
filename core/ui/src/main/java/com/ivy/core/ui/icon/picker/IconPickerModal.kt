//package com.ivy.core.ui.icon.picker
//
//import com.ivy.core.ui.icon.picker.IconPickerItem.IconUnverified
//import com.ivy.core.ui.icon.picker.IconPickerItem.SectionDivider
//
//package com.ivy.wallet.ui.theme.modal
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyListScope
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalView
//import androidx.compose.ui.platform.testTag
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.ivy.base.R
//import com.ivy.design.l0_system.UI
//import com.ivy.design.l1_buildingBlocks.DividerW
//import com.ivy.design.l1_buildingBlocks.IvyText
//import com.ivy.design.l1_buildingBlocks.SpacerHor
//import com.ivy.design.l1_buildingBlocks.SpacerVer
//import com.ivy.design.util.IvyPreview
//import com.ivy.wallet.ui.theme.components.ItemIconS
//import com.ivy.wallet.ui.theme.dynamicContrast
//import com.ivy.wallet.utils.thenIf
//import java.util.*
//
//private const val ICON_PICKER_ICONS_PER_ROW = 5
//
//@Composable
//fun BoxWithConstraintsScope.ChooseIconModal(
//    visible: Boolean,
//    initialIcon: String?,
//    color: Color,
//
//    id: UUID = UUID.randomUUID(),
//
//    dismiss: () -> Unit,
//    onIconChosen: (String?) -> Unit
//) {
//    var selectedIcon by remember(id) {
//        mutableStateOf(initialIcon)
//    }
//
//    IvyModal(
//        id = id,
//        visible = visible,
//        dismiss = dismiss,
//        scrollState = null,
//        includeActionsRowPadding = false,
//        PrimaryAction = {
//            ModalSave(
//                modifier = Modifier.testTag("choose_icon_save")
//            ) {
//                onIconChosen(selectedIcon)
//                dismiss()
//            }
//        }
//    ) {
//        val view = LocalView.current
//
//        LazyColumn(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            item {
//                Spacer(Modifier.height(32.dp))
//
//                ModalTitle(text = stringResource(R.string.choose_icon))
//
//                Spacer(Modifier.height(4.dp))
//            }
//
//            pickerItems(selectedIcon = selectedIcon, color = color) {
//                selectedIcon = it
//            }
//
//            item {
//                Spacer(Modifier.height(160.dp))
//            }
//        }
//    }
//}
//
//private fun LazyListScope.pickerItems(
//    selectedIcon: String?,
//    color: Color,
//
//    onIconSelected: (String) -> Unit
//) {
//    val icons = ivyIcons()
//
//    iconsR(
//        icons = icons,
//        iconsPerRow = ICON_PICKER_ICONS_PER_ROW,
//        selectedIcon = selectedIcon,
//        color = color,
//        onIconSelected = onIconSelected
//    )
//}
//
//private tailrec fun LazyListScope.iconsR(
//    icons: List<Any>,
//    rowAcc: List<String> = emptyList(),
//
//    iconsPerRow: Int,
//    selectedIcon: String?,
//    color: Color,
//
//    onIconSelected: (String) -> Unit
//) {
//    if (icons.isNotEmpty()) {
//        //recurse
//
//        when (val currentItem = icons.first()) {
//            is IconPickerSection -> {
//                addIconsRowIfNotEmpty(
//                    rowAcc = rowAcc,
//                    selectedIcon = selectedIcon,
//                    color = color,
//                    onIconSelected = onIconSelected
//                )
//
//                item {
//                    Section(title = currentItem.title)
//                }
//
//                //RECURSE
//                iconsR(
//                    icons = icons.drop(1),
//                    rowAcc = emptyList(),
//
//                    iconsPerRow = iconsPerRow,
//                    selectedIcon = selectedIcon,
//                    color = color,
//                    onIconSelected = onIconSelected
//
//                )
//            }
//            is String -> {
//                //icon
//
//                if (rowAcc.size == iconsPerRow) {
//                    //recurse and reset acc
//
//                    addIconsRowIfNotEmpty(
//                        rowAcc = rowAcc,
//                        selectedIcon = selectedIcon,
//                        color = color,
//                        onIconSelected = onIconSelected
//                    )
//
//                    //RECURSE
//                    iconsR(
//                        icons = icons.drop(1),
//                        rowAcc = emptyList(),
//
//                        iconsPerRow = iconsPerRow,
//                        selectedIcon = selectedIcon,
//                        color = color,
//                        onIconSelected = onIconSelected
//
//                    )
//                } else {
//                    //recurse by filling acc
//
//                    //RECURSE
//                    iconsR(
//                        icons = icons.drop(1),
//                        rowAcc = rowAcc + currentItem,
//
//                        iconsPerRow = iconsPerRow,
//                        selectedIcon = selectedIcon,
//                        color = color,
//                        onIconSelected = onIconSelected
//
//                    )
//                }
//            }
//        }
//    } else {
//        //end recursion
//        addIconsRowIfNotEmpty(
//            rowAcc = rowAcc,
//            selectedIcon = selectedIcon,
//            color = color,
//            onIconSelected = onIconSelected
//        )
//    }
//}
//
//private fun LazyListScope.addIconsRowIfNotEmpty(
//    rowAcc: List<String>,
//
//    selectedIcon: String?,
//    color: Color,
//
//    onIconSelected: (String) -> Unit
//) {
//    if (rowAcc.isNotEmpty()) {
//        item {
//            IconsRow(
//                icons = rowAcc,
//                selectedIcon = selectedIcon,
//                color = color
//            ) {
//                onIconSelected(it)
//            }
//
//            Spacer(Modifier.height(16.dp))
//        }
//    }
//}
//
//@Composable
//private fun IconsRow(
//    icons: List<String>,
//    selectedIcon: String?,
//    color: Color,
//
//    onIconSelected: (String) -> Unit
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Spacer(Modifier.width(24.dp))
//
//        for ((index, icon) in icons.withIndex()) {
//            Icon(
//                icon = icon,
//                selected = selectedIcon == icon,
//                color = color
//            ) {
//                onIconSelected(icon)
//            }
//
//            if (index < icons.lastIndex && icons.size >= 5) {
//                Spacer(Modifier.weight(1f))
//            } else {
//                Spacer(Modifier.width(20.dp))
//            }
//        }
//
//        Spacer(Modifier.width(24.dp))
//    }
//}
//
//@Composable
//private fun Icon(
//    icon: String,
//    selected: Boolean,
//    color: Color,
//
//    onClick: () -> Unit,
//) {
//    ItemIconS(
//        modifier = Modifier
//            .clip(CircleShape)
//            .border(2.dp, if (selected) color else UI.colors.medium, CircleShape)
//            .thenIf(selected) {
//                background(color, CircleShape)
//            }
//            .clickable {
//                onClick()
//            }
//            .padding(all = 8.dp)
//            .testTag(icon),
//        iconName = icon,
//        tint = if (selected) color.dynamicContrast() else UI.colorsInverted.medium
//    )
//}
//
//@Composable
//private fun Section(
//    title: String
//) {
//    SpacerVer(height = 20.dp)
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        DividerW()
//
//        SpacerHor(width = 16.dp)
//
//        IvyText(text = title, typo = UI.typo.b1)
//
//        SpacerHor(width = 16.dp)
//
//        DividerW()
//    }
//
//    SpacerVer(height = 20.dp)
//}
//
//
//// region Preview
//@Preview
//@Composable
//private fun Preview() {
//    IvyPreview {
//
//    }
//}
//// endregion