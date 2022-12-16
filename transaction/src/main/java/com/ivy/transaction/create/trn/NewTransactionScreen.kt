package com.ivy.transaction.create.trn

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.ivy.core.ui.category.pick.CategoryPickerModal
import com.ivy.core.ui.data.CategoryUi
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.navigation.destinations.transaction.NewTransaction

@Composable
fun BoxScope.NewTransactionScreen(arg: NewTransaction.Arg) {
    val categoriesPicker = rememberIvyModal()

    ColumnRoot {
        SpacerWeight(weight = 1f)
        Button(onClick = {
            categoriesPicker.show()
        }) {
            Text(text = "Pick category")
        }
        SpacerWeight(weight = 1f)
    }

    var selectedCategory by remember { mutableStateOf<CategoryUi?>(null) }
    CategoryPickerModal(
        modal = categoriesPicker,
        selected = selectedCategory,
        onPick = { selectedCategory = it }
    )
}