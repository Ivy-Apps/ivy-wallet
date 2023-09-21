package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.utils.hideKeyboard
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.components.IvyNameTextField
import kotlinx.coroutines.delay
import java.util.UUID

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun BoxWithConstraintsScope.DeleteModal(
    id: UUID = UUID.randomUUID(),
    title: String,
    description: String,
    visible: Boolean,
    buttonText: String = stringResource(R.string.delete),
    iconStart: Int = R.drawable.ic_delete,
    dismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalNegativeButton(
                text = buttonText,
                iconStart = iconStart
            ) {
                onDelete()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = title,
            style = UI.typo.b1.style(
                color = Red,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = description,
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
fun BoxWithConstraintsScope.DeleteConfirmationModal(
    id: UUID = UUID.randomUUID(),
    title: String,
    description: String,
    accountName: TextFieldValue,
    hint: String = stringResource(id = R.string.account_name),
    visible: Boolean,
    enableDeletionButton: Boolean,
    buttonText: String = stringResource(R.string.delete),
    iconStart: Int = R.drawable.ic_delete,
    onAccountNameChange: (String) -> Unit,
    dismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalNegativeButton(
                text = buttonText,
                iconStart = iconStart,
                enabled = enableDeletionButton
            ) {
                onDelete()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = title,
            style = UI.typo.b1.style(
                color = Red,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = description,
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(Modifier.height(12.dp))

        val view = LocalView.current
        val focusRequester = remember { FocusRequester() }

        IvyNameTextField(
            modifier = Modifier
                .padding(start = 28.dp, end = 36.dp),
            focusRequester = focusRequester,
            underlineModifier = Modifier.padding(start = 24.dp, end = 32.dp),
            value = accountName,
            hint = hint,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
                autoCorrect = true
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    hideKeyboard(view)
                }
            ),
        ) { newValue ->
            onAccountNameChange(newValue.text)
        }

        LaunchedEffect(key1 = true){
            delay(50)
            focusRequester.requestFocus()
        }

        Spacer(Modifier.height(48.dp))
    }
}
