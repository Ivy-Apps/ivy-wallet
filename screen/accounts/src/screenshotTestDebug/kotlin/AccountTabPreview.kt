import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.accounts.AccountsState
import com.ivy.accounts.UI
import com.ivy.base.legacy.Theme
import com.ivy.data.model.AccountId
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.legacy.IvyWalletPreview
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.GreenLight
import kotlinx.collections.immutable.persistentListOf
import java.time.Instant
import java.util.UUID


@Preview
@Composable
private fun PreviewAccountsTab(theme: Theme = Theme.LIGHT) {
    IvyWalletPreview(theme = theme) {
        val acc1 = com.ivy.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("Phyre"),
            color = ColorInt(Green.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )

        val acc2 = com.ivy.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("DSK"),
            color = ColorInt(GreenLight.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )

        val acc3 = com.ivy.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("Revolut"),
            color = ColorInt(Green.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = IconAsset.unsafe("revolut"),
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )

        val acc4 = com.ivy.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("Cash"),
            color = ColorInt(Green.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = IconAsset.unsafe("cash"),
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )
        val state = AccountsState(
            baseCurrency = "BGN",
            accountsData = persistentListOf(
                com.ivy.legacy.data.model.AccountData(
                    account = acc1,
                    balance = 2125.0,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 920.0,
                    monthlyIncome = 3045.0
                ),
                com.ivy.legacy.data.model.AccountData(
                    account = acc2,
                    balance = 12125.21,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 1350.50,
                    monthlyIncome = 8000.48
                ),
                com.ivy.legacy.data.model.AccountData(
                    account = acc3,
                    balance = 1200.0,
                    balanceBaseCurrency = 1979.64,
                    monthlyExpenses = 750.0,
                    monthlyIncome = 1000.30
                ),
                com.ivy.legacy.data.model.AccountData(
                    account = acc4,
                    balance = 820.0,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 340.0,
                    monthlyIncome = 400.0
                ),
            ),
            totalBalanceWithExcluded = "25.54",
            totalBalanceWithExcludedText = "BGN 25.54",
            totalBalanceWithoutExcluded = "25.54",
            totalBalanceWithoutExcludedText = "BGN 25.54",
            reorderVisible = false
        )
        UI(state = state)
    }
}

@Preview
@Composable
private fun PreviewAccountsTab1(theme: Theme = Theme.DARK) {
    IvyWalletPreview(theme = theme) {
        val acc1 = com.ivy.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("Phyre"),
            color = ColorInt(Green.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )

        val acc2 = com.ivy.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("DSK"),
            color = ColorInt(GreenLight.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )

        val acc3 = com.ivy.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("Revolut"),
            color = ColorInt(Green.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = IconAsset.unsafe("revolut"),
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )

        val acc4 = com.ivy.data.model.Account(
            id = AccountId(UUID.randomUUID()),
            name = NotBlankTrimmedString.unsafe("Cash"),
            color = ColorInt(Green.toArgb()),
            asset = AssetCode.unsafe("USD"),
            icon = IconAsset.unsafe("cash"),
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )
        val state = AccountsState(
            baseCurrency = "BGN",
            accountsData = persistentListOf(
                com.ivy.legacy.data.model.AccountData(
                    account = acc1,
                    balance = 2125.0,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 920.0,
                    monthlyIncome = 3045.0
                ),
                com.ivy.legacy.data.model.AccountData(
                    account = acc2,
                    balance = 12125.21,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 1350.50,
                    monthlyIncome = 8000.48
                ),
                com.ivy.legacy.data.model.AccountData(
                    account = acc3,
                    balance = 1200.0,
                    balanceBaseCurrency = 1979.64,
                    monthlyExpenses = 750.0,
                    monthlyIncome = 1000.30
                ),
                com.ivy.legacy.data.model.AccountData(
                    account = acc4,
                    balance = 820.0,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 340.0,
                    monthlyIncome = 400.0
                ),
            ),
            totalBalanceWithExcluded = "25.54",
            totalBalanceWithExcludedText = "BGN 25.54",
            totalBalanceWithoutExcluded = "25.54",
            totalBalanceWithoutExcludedText = "BGN 25.54",
            reorderVisible = false
        )
        UI(state = state)
    }
}

