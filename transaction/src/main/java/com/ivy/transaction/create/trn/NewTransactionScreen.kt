package com.ivy.transaction.create.trn

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.ivy.core.ui.category.pick.CategoryPickerModal
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.time.picker.date.DatePickerModal
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.navigation.destinations.transaction.NewTransaction
import java.time.LocalDate

@Composable
fun BoxScope.NewTransactionScreen(arg: NewTransaction.Arg) {
    val categoriesPicker = rememberIvyModal()
    val datePicker = rememberIvyModal()

    ColumnRoot {
        SpacerWeight(weight = 1f)
        Button(onClick = {
            categoriesPicker.show()
        }) {
            Text(text = "Pick category")
        }
        Button(onClick = {
            datePicker.show()
        }) {
            Text(text = "Pick date")
        }
        SpacerWeight(weight = 1f)
    }

    var selectedCategory by remember { mutableStateOf<CategoryUi?>(null) }
    CategoryPickerModal(
        modal = categoriesPicker,
        selected = selectedCategory,
        onPick = { selectedCategory = it }
    )

    DatePickerModal(
        modal = datePicker,
        selected = LocalDate.now(),
        onPick = {}
    )
}