package com.ivy.importdata.csv

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.legacy.ivyWalletCtx
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.colorAs
import com.ivy.importdata.csvimport.flow.ImportProcessing
import com.ivy.importdata.csvimport.flow.ImportResultUI
import com.ivy.navigation.CSVScreen
import com.ivy.onboarding.viewmodel.OnboardingViewModel
import com.ivy.legacy.utils.thenIf
import kotlin.math.abs

@Composable
fun CSVScreen(
    screen: CSVScreen
) {
    val viewModel: CSVViewModel = viewModel()
    val state = viewModel.uiState()
    val onboardingViewModel: OnboardingViewModel = viewModel()

    when (val ui = state.uiState) {
        UIState.Idle -> ImportUI(
            state = state,
            launchedFromOnboarding = screen.launchedFromOnboarding,
            onEvent = viewModel::onEvent
        )

        is UIState.Processing -> ImportProcessing(progressPercent = ui.percent)
        is UIState.Result -> ImportResultUI(
            result = ui.importResult,
            isManualCsvImport = true,
            launchedFromOnboarding = screen.launchedFromOnboarding,
            onTryAgain = null,
            onFinish = {
                viewModel.onEvent(
                    CSVEvent.FinishImport(
                        launchedFromOnboarding = screen.launchedFromOnboarding,
                        onboardingViewModel = onboardingViewModel,
                    )
                )
            }
        )
    }
}

@Composable
private fun ImportUI(
    state: CSVState,
    launchedFromOnboarding: Boolean,
    onEvent: (com.ivy.importdata.csv.CSVEvent) -> Unit,
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
                    onEvent(com.ivy.importdata.csv.CSVEvent.FilePicked(it))
                }
            )
            if (!launchedFromOnboarding) {
                Spacer8()
                Text(
                    text = """
                !!!⚠️WARNING: Importing may duplicate transactions!!!
                Duplicate transactions can NOT be easily deleted and you'll need to remove manually each one of them! 
                Reason: We can't parse transaction ids because Ivy Wallet works only with UUID and other apps don't.
                If you're starting fresh, no worries - kindly ignore this message.
                    """.trimIndent(),
                    style = UI.typo.c.colorAs(UI.colors.red),
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Spacer8()
                Text(
                    text = "Import a CSV file to continue.",
                    style = UI.typo.b2,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        if (state.csv != null) {
            spacer8()
            csvTable(state.csv)
        }
        if (state.columns != null && state.important != null) {
            importantFields(state.columns, importantFields = state.important, onEvent = onEvent)
        }
        if (state.columns != null && state.transfer != null) {
            transferFields(state.columns, state.transfer, onEvent = onEvent)
        }
        if (state.columns != null && state.optional != null) {
            optionalFields(state.columns, state.optional, onEvent = onEvent)
        }
        continueButton(state.continueEnabled, onEvent)
    }
}

@Composable
private fun ImportButton(
    onFilePicked: (Uri) -> Unit,
) {
    val ivyContext = com.ivy.legacy.ivyWalletCtx()
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
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
    item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            val visibleRows = remember(csv) { csv.take(10) }
            visibleRows.forEachIndexed { index, row ->
                CSVRow(row = row, header = index == 0, even = index % 2 == 0)
            }
        }
    }
}

@Composable
private fun CSVRow(
    row: CSVRow,
    header: Boolean,
    even: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
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
            .width(140.dp)
            .border(1.dp, UI.colors.pureInverse)
            .thenIf(even) {
                this.background(UI.colors.medium)
            }
            .padding(all = 4.dp),
        text = text.ifEmpty { " " },
        style = UI.typo.nB1,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}

private fun <M> LazyListScope.mappingRow(
    columns: CSVRow,
    mapping: ColumnMapping<M>,
    status: MappingStatus,
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
                        mapping.required && !status.success -> UI.colors.red
                        status.success -> UI.colors.green
                        else -> UI.colors.medium
                    },
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            Text(
                text = mapping.ivyColumn,
                style = UI.typo.b1.colorAs(UI.colors.primary),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = mapping.helpInfo, style = UI.typo.c.colorAs(UI.colors.gray))
            Spacer8()
            Text(text = "Choose a matching CSV column:", style = UI.typo.b2)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
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

            if (status.sampleValues.isNotEmpty()) {
                Spacer8()
                CSVRow(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    row = CSVRow(status.sampleValues),
                    header = false,
                    even = true
                )
            }
        }
    }
}

fun LazyListScope.sectionDivider(text: String) {
    item {
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = text, style = UI.typo.b1)
        Text(
            text = """
                Match the CSV column with the appropriate Ivy type.
                If the parsing is successful the border will turn green.
            """.trimIndent(),
            style = UI.typo.nB2,
            color = UI.colors.gray
        )
        Spacer8()
    }
}

// region Important
fun LazyListScope.importantFields(
    columns: CSVRow,
    importantFields: ImportantFields,
    onEvent: (com.ivy.importdata.csv.CSVEvent) -> Unit
) {
    sectionDivider("Important")
    mappingRow(
        columns = columns,
        mapping = importantFields.amount,
        status = importantFields.amountStatus,
        onMapTo = { index, name ->
            onEvent(
                com.ivy.importdata.csv.CSVEvent.MapAmount(
                    index,
                    name
                )
            )
        },
        metadataContent = { multiplier ->
            AmountMetadata(multiplier = multiplier, onMetaChange = {
                onEvent(com.ivy.importdata.csv.CSVEvent.AmountMultiplier(it))
            })
        }
    )
    mappingRow(
        columns = columns,
        mapping = importantFields.type,
        status = importantFields.typeStatus,
        onMapTo = { index, name -> onEvent(com.ivy.importdata.csv.CSVEvent.MapType(index, name)) },
        metadataContent = {
            TypeMetadata(metadata = it, onEvent = onEvent)
        }
    )
    mappingRow(
        columns = columns,
        mapping = importantFields.date,
        status = importantFields.dateStatus,
        onMapTo = { index, name -> onEvent(com.ivy.importdata.csv.CSVEvent.MapDate(index, name)) },
        metadataContent = {
            DateMetadataUI(metadata = it, onEvent = onEvent)
        }
    )
    mappingRow(
        columns = columns,
        mapping = importantFields.account,
        status = importantFields.accountStatus,
        onMapTo = { index, name ->
            onEvent(
                com.ivy.importdata.csv.CSVEvent.MapAccount(
                    index,
                    name
                )
            )
        },
    )
    mappingRow(
        columns = columns,
        mapping = importantFields.accountCurrency,
        status = importantFields.accountCurrencyStatus,
        onMapTo = { index, name ->
            onEvent(
                com.ivy.importdata.csv.CSVEvent.MapAccountCurrency(
                    index,
                    name
                )
            )
        },
    )
}

@Composable
private fun AmountMetadata(
    multiplier: Int,
    onMetaChange: (Int) -> Unit,
) {
    Text(text = "Multiplier", style = UI.typo.nB2)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = {
            onMetaChange(
                when {
                    multiplier < 0 -> multiplier * 10
                    multiplier == 1 -> -10
                    else -> multiplier / 10
                }
            )
        }) {
            Text(text = "/10")
        }
        Spacer8(horizontal = true)
        Text(
            text = when {
                multiplier < 0 -> "/${abs(multiplier)}"
                multiplier > 1 -> "*${abs(multiplier)}"
                else -> "None"
            },
            style = UI.typo.nB2,
            color = UI.colors.primary,
        )
        Spacer8(horizontal = true)
        Button(onClick = {
            onMetaChange(
                when {
                    multiplier == -10 -> 1
                    multiplier == -1 -> 10
                    multiplier > 0 -> multiplier * 10
                    else -> multiplier / 10
                }
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
    onEvent: (com.ivy.importdata.csv.CSVEvent) -> Unit
) {
    val onTypeMetaEvent = { newMeta: TrnTypeMetadata ->
        onEvent(com.ivy.importdata.csv.CSVEvent.TypeMetaChange(newMeta))
    }

    LabelContainsField(
        label = "Income",
        value = metadata.income,
        onValueChange = {
            onTypeMetaEvent(metadata.copy(income = it))
        }
    )
    Spacer8()
    LabelContainsField(
        label = "Expense",
        value = metadata.expense,
        onValueChange = {
            onTypeMetaEvent(metadata.copy(expense = it))
        }
    )
    Spacer8()
    Text(text = "(optional)", style = UI.typo.c)
    LabelContainsField(
        label = "Transfer",
        value = metadata.transfer ?: "",
        onValueChange = {
            onTypeMetaEvent(metadata.copy(transfer = it))
        }
    )
}

@Composable
fun LabelContainsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = UI.colors.primary, style = UI.typo.nB1)
        Text(text = " contains ", style = UI.typo.c)
        Spacer8(horizontal = true)
        var textField by remember {
            // move the cursor at the end of the text
            mutableStateOf(TextFieldValue(value, TextRange(value.length)))
        }
        TextField(
            value = textField,
            onValueChange = {
                textField = it
                onValueChange(it.text)
            },
            singleLine = true
        )
    }
}
// endregion

@Composable
private fun DateMetadataUI(
    metadata: DateMetadata,
    onEvent: (com.ivy.importdata.csv.CSVEvent) -> Unit,
) {
    Text(text = "Which is first in the format?")
    Row {
        EnabledButton(
            enabled = metadata == DateMetadata.DateFirst,
            text = "Date/Day (1,2,3...31)",
            onClick = {
                onEvent(com.ivy.importdata.csv.CSVEvent.DataMetaChange(DateMetadata.DateFirst))
            }
        )
        Spacer8(horizontal = true)
        EnabledButton(
            enabled = metadata == DateMetadata.MonthFirst,
            text = "Month (1,2..12 / Jan, Feb..Dec)",
            onClick = {
                onEvent(com.ivy.importdata.csv.CSVEvent.DataMetaChange(DateMetadata.MonthFirst))
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

fun LazyListScope.transferFields(
    columns: CSVRow,
    transferFields: TransferFields,
    onEvent: (com.ivy.importdata.csv.CSVEvent) -> Unit
) {
    sectionDivider("Transfer fields")
    mappingRow(
        columns = columns,
        mapping = transferFields.toAccount,
        status = transferFields.toAccountStatus,
        onMapTo = { index, name ->
            onEvent(
                com.ivy.importdata.csv.CSVEvent.MapToAccount(
                    index,
                    name
                )
            )
        },
    )
    mappingRow(
        columns = columns,
        mapping = transferFields.toAccountCurrency,
        status = transferFields.toAccountCurrencyStatus,
        onMapTo = { index, name ->
            onEvent(
                com.ivy.importdata.csv.CSVEvent.MapToAccountCurrency(
                    index,
                    name
                )
            )
        },
    )
    mappingRow(
        columns = columns,
        mapping = transferFields.toAmount,
        status = transferFields.toAmountStatus,
        onMapTo = { index, name ->
            onEvent(
                com.ivy.importdata.csv.CSVEvent.MapToAmount(
                    index,
                    name
                )
            )
        },
        metadataContent = { multiplier ->
            AmountMetadata(
                multiplier = multiplier,
                onMetaChange = {
                    onEvent(com.ivy.importdata.csv.CSVEvent.ToAmountMetaChange(it))
                }
            )
        }
    )
}

fun LazyListScope.optionalFields(
    columns: CSVRow,
    optionalFields: OptionalFields,
    onEvent: (com.ivy.importdata.csv.CSVEvent) -> Unit
) {
    sectionDivider("Optional fields")
    mappingRow(
        columns = columns,
        mapping = optionalFields.category,
        status = optionalFields.categoryStatus,
        onMapTo = { index, name ->
            onEvent(
                com.ivy.importdata.csv.CSVEvent.MapCategory(
                    index,
                    name
                )
            )
        },
    )
    mappingRow(
        columns = columns,
        mapping = optionalFields.title,
        status = optionalFields.titleStatus,
        onMapTo = { index, name -> onEvent(com.ivy.importdata.csv.CSVEvent.MapTitle(index, name)) },
    )
    mappingRow(
        columns = columns,
        mapping = optionalFields.description,
        status = optionalFields.descriptionStatus,
        onMapTo = { index, name ->
            onEvent(
                com.ivy.importdata.csv.CSVEvent.MapDescription(
                    index,
                    name
                )
            )
        },
    )
}

private fun LazyListScope.continueButton(
    enabled: Boolean,
    onEvent: (com.ivy.importdata.csv.CSVEvent) -> Unit
) {
    item {
        Spacer8()
        Spacer8()
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = enabled,
            onClick = {
                onEvent(com.ivy.importdata.csv.CSVEvent.Continue)
            }
        ) {
            Text(text = "Continue")
        }
        Spacer8()
        Spacer8()
    }
}
