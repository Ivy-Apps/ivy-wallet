package com.ivy.wallet.ui.csv

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.colorAs
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.utils.thenIf

@Composable
fun CSVScreen() {
    val viewModel: CSVViewModel = viewModel()
    UI(state = viewModel.uiState(), onEvent = viewModel::onEvent)
}

@Composable
private fun UI(
    state: CSVState,
    onEvent: (CSVEvent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 16.dp,
        )
    ) {
        item(key = "import_btn") {
            ImportButton(
                onFilePicked = {
                    onEvent(CSVEvent.FilePicked(it))
                }
            )
        }
        if (state.csv != null) {
            spacer8()
            csvTable(state.csv)
        }
        if (state.columns != null && state.important != null) {
            important(state.columns, importantFields = state.important, onEvent = onEvent)
        }
    }
}

@Composable
private fun ImportButton(
    onFilePicked: (Uri) -> Unit,
) {
    val ivyContext = ivyWalletCtx()
    Button(
        onClick = {
            ivyContext.openFile {
                onFilePicked(it)
            }
        }
    ) {
        Text(text = "Import CSV")
    }
}

fun LazyListScope.spacer8() {
    item {
        Spacer8()
    }
}

@Composable
fun Spacer8(horizontal: Boolean = false) {
    if (horizontal) {
        Spacer(modifier = Modifier.width(8.dp))
    } else {
        Spacer(modifier = Modifier.height(8.dp))
    }
}


private fun LazyListScope.csvTable(
    csv: List<CSVRow>
) {
    itemsIndexed(items = csv.take(10)) { index, row ->
        CSVRow(row = row, header = index == 0, even = index % 2 == 0)
    }
}

@Composable
private fun CSVRow(
    row: CSVRow,
    header: Boolean,
    even: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        row.values.forEach { value ->
            CSVCell(text = value, header = header, even = even)
        }
    }
}

@Composable
private fun CSVCell(
    text: String,
    header: Boolean,
    even: Boolean
) {
    Text(
        modifier = Modifier
            .width(120.dp)
            .border(1.dp, UI.colors.pureInverse)
            .thenIf(even) {
                this.background(UI.colors.medium)
            }
            .padding(all = 4.dp),
        text = text,
        style = UI.typo.nB1,
        fontWeight = if (header) FontWeight.ExtraBold else FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}


private fun <M> LazyListScope.mappingRow(
    columns: CSVRow,
    mapping: ColumnMapping<M>,
    onMapTo: (Int, String) -> Unit,
    metadataContent: (@Composable (M) -> Unit)? = null,
) {
    item {
        Spacer8()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = when {
                        mapping.required && !mapping.success -> UI.colors.red
                        mapping.success -> UI.colors.green
                        else -> UI.colors.medium
                    }
                )
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                text = mapping.ivyColumn,
                style = UI.typo.b1.colorAs(UI.colors.primary),
            )
            Spacer8()
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                columns.values.forEachIndexed { index, column ->
                    EnabledButton(
                        text = column,
                        enabled = column == mapping.name
                    ) {
                        onMapTo(index, column)
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }

            if (metadataContent != null) {
                Spacer8()
                metadataContent(mapping.metadata)
            }

            if (mapping.sampleValues.isNotEmpty()) {
                Spacer8()
                CSVRow(row = CSVRow(mapping.sampleValues), header = false, even = true)
            }
        }
    }
}

fun LazyListScope.sectionDivider(text: String) {
    item {
        Spacer8()
        Text(text = text, style = UI.typo.h2)
        Spacer8()
    }
}

// region Important
fun LazyListScope.important(
    columns: CSVRow,
    importantFields: ImportantFields,
    onEvent: (CSVEvent) -> Unit
) {
    sectionDivider("Important")
    mappingRow(
        columns = columns,
        mapping = importantFields.amount,
        onMapTo = { index, name -> onEvent(CSVEvent.MapAmount(index, name)) },
        metadataContent = { multiplier ->
            AmountMetadata(multiplier = multiplier, onEvent = onEvent)
        }
    )
    mappingRow(
        columns = columns,
        mapping = importantFields.type,
        onMapTo = { index, name -> onEvent(CSVEvent.MapType(index, name)) },
        metadataContent = {
            TypeMetadata(metadata = it, onEvent = onEvent)
        }
    )
    mappingRow(
        columns = columns,
        mapping = importantFields.date,
        onMapTo = { index, name -> onEvent(CSVEvent.MapDate(index, name)) },
        metadataContent = {
            DateMetadataUI(metadata = it, onEvent = onEvent)
        }
    )
    mappingRow(
        columns = columns,
        mapping = importantFields.account,
        onMapTo = { index, name -> onEvent(CSVEvent.MapAccount(index, name)) },
    )
    mappingRow(
        columns = columns,
        mapping = importantFields.accountCurrency,
        onMapTo = { index, name -> onEvent(CSVEvent.MapAccountCurrency(index, name)) },
    )
}

@Composable
private fun AmountMetadata(
    multiplier: Int,
    onEvent: (CSVEvent) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = {
            onEvent(
                CSVEvent.AmountMultiplier(
                    when {
                        multiplier < 0 -> multiplier * 10
                        multiplier == 1 -> -10
                        else -> multiplier / 10
                    }
                )
            )
        }) {
            Text(text = "/10")
        }
        Spacer8(horizontal = true)
        Text(
            text = when {
                multiplier < 0 -> "/$multiplier"
                multiplier > 1 -> "*$multiplier"
                else -> "None"
            }
        )
        Spacer8(horizontal = true)
        Button(onClick = {
            onEvent(
                CSVEvent.AmountMultiplier(
                    when {
                        multiplier == -1 -> 10
                        multiplier > 0 -> multiplier * 10
                        else -> multiplier / 10
                    }
                )
            )
        }) {
            Text(text = "*10")
        }
    }
}

// region Type Metadata
@Composable
private fun TypeMetadata(
    metadata: TrnTypeMetadata,
    onEvent: (CSVEvent) -> Unit
) {
    val onTypeMetaEvent = { newMeta: TrnTypeMetadata ->
        onEvent(CSVEvent.TypeMetaChange(newMeta))
    }

    LabelEqualsField(
        label = "Income",
        value = metadata.income,
        onValueChange = {
            onTypeMetaEvent(metadata.copy(income = it))
        }
    )
    Spacer8()
    LabelEqualsField(
        label = "Expense",
        value = metadata.expense,
        onValueChange = {
            onTypeMetaEvent(metadata.copy(expense = it))
        }
    )
    Spacer8()
    Text(text = "(optional)", style = UI.typo.c)
    LabelEqualsField(
        label = "Transfer",
        value = metadata.transfer ?: "",
        onValueChange = {
            onTypeMetaEvent(metadata.copy(transfer = it))
        }
    )
}

@Composable
fun LabelEqualsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = UI.colors.primary)
        Spacer8(horizontal = true)
        TextField(value = value, onValueChange = onValueChange)
    }
}
// endregion

@Composable
private fun DateMetadataUI(
    metadata: DateMetadata,
    onEvent: (CSVEvent) -> Unit,
) {
    Text(text = "Which is first in the format?")
    Row {
        EnabledButton(
            enabled = metadata == DateMetadata.DateFirst,
            text = "Date/Day (1,2,3...31)",
            onClick = {
                onEvent(CSVEvent.DataMetaChange(DateMetadata.DateFirst))
            }
        )
        Spacer8(horizontal = true)
        EnabledButton(
            enabled = metadata == DateMetadata.MonthFirst,
            text = "Month (1,2..12 / Jan, Feb..Dec)",
            onClick = {
                onEvent(CSVEvent.DataMetaChange(DateMetadata.MonthFirst))
            }
        )
    }
}

@Composable
private fun EnabledButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        colors = if (enabled) {
            ButtonDefaults.buttonColors()
        } else {
            ButtonDefaults.outlinedButtonColors()
        },
        onClick = onClick
    ) {
        Text(text = text)
    }
}
// endregion