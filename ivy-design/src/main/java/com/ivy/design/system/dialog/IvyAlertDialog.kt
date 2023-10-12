package com.ivy.design.system.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.design.system.IvyMaterial3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IvyAlertDialog(
    isVisible: Boolean,
    title: String,
    text: String,
    confirmButtonText: String,
    dismissButtonText: String?,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: (() -> Unit)? = null,
    onDismissRequest: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {
                dismissButtonText?.let {
                    TextButton(onClick = { onDismissButtonClick?.invoke() }) {
                        Text(text = dismissButtonText)
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun IvyAlertDialogPreviewLight() {
    Column {
        IvyMaterial3Theme(dark = false) {
            IvyAlertDialog(
                isVisible = true,
                title = "Alert Dialog",
                text = "Are you sure that you want to delete this?",
                confirmButtonText = "Yes",
                dismissButtonText = "No",
                onConfirmButtonClick = { /*TODO*/ },
                onDismissButtonClick = { /*TODO*/ }) {

            }
        }
    }
}

@Preview
@Composable
fun IvyAlertDialogPreviewNight() {
    Column {
        IvyMaterial3Theme(dark = true) {
            IvyAlertDialog(
                isVisible = true,
                title = "Alert Dialog",
                text = "Are you sure that you want to delete this?",
                confirmButtonText = "Yes",
                dismissButtonText = null,
                onConfirmButtonClick = { /*TODO*/ },
                onDismissButtonClick = { /*TODO*/ }) {

            }
        }
    }
}