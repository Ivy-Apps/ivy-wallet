package com.ivy.wallet.domain.deprecated.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.ui.onboarding.model.AccountBalance
import com.ivy.wallet.ui.theme.*

@Deprecated("Migrate to FP Style")
class PreloadDataLogic(
    private val accountsDao: AccountDao,
    private val categoryDao: CategoryDao
) {
    var categoryOrderNum = 0.0

    fun shouldPreloadData(accounts: List<AccountBalance>): Boolean {
        //Preload data only if the user has less than 2 accounts
        return accounts.size < 2
    }

    fun preloadAccounts() {
        val cash = Account(
            name = "Cash",
            currency = null,
            color = Green.toArgb(),
            icon = "cash",
            orderNum = 0.0,
            isSynced = false
        )

        val bank = Account(
            name = "Bank",
            currency = null,
            color = IvyDark.toArgb(),
            icon = "bank",
            orderNum = 1.0,
            isSynced = false
        )

        accountsDao.save(cash.toEntity())
        accountsDao.save(bank.toEntity())
    }

    fun accountSuggestions(baseCurrency: String): List<CreateAccountData> = listOf(
        CreateAccountData(
            name = "Cash",
            currency = baseCurrency,
            color = Green,
            icon = "cash",
            balance = 0.0
        ),
        CreateAccountData(
            name = "Bank",
            currency = baseCurrency,
            color = IvyDark,
            icon = "bank",
            balance = 0.0
        ),
        CreateAccountData(
            name = "Revolut",
            currency = baseCurrency,
            color = Blue,
            icon = "revolut",
            balance = 0.0
        ),
    )

    fun preloadCategories() {
        categoryOrderNum = 0.0

        val categoriesToPreload = preloadCategoriesCreateData()

        for (createData in categoriesToPreload) {
            preloadCategory(createData)
        }
    }

    private fun preloadCategoriesCreateData() = listOf(
        CreateCategoryData(
            name = "Food & Drinks",
            color = Green,
            icon = "fooddrink"
        ),

        CreateCategoryData(
            name = "Bills & Fees",
            color = Red,
            icon = "bills"
        ),

        CreateCategoryData(
            name = "Transport",
            color = YellowLight,
            icon = "transport"
        ),

        CreateCategoryData(
            name = "Groceries",
            color = GreenLight,
            icon = "groceries"
        ),

        CreateCategoryData(
            name = "Entertainment",
            color = Orange,
            icon = "game"
        ),

        CreateCategoryData(
            name = "Shopping",
            color = Ivy,
            icon = "shopping"
        ),

        CreateCategoryData(
            name = "Gifts",
            color = RedLight,
            icon = "gift"
        ),

        CreateCategoryData(
            name = "Health",
            color = IvyLight,
            icon = "health"
        ),

        CreateCategoryData(
            name = "Investments",
            color = IvyDark,
            icon = "leaf"
        ),

        CreateCategoryData(
            name = "Loans",
            color = BlueDark,
            icon = "loan"
        ),
    )

    private fun preloadCategory(
        data: CreateCategoryData
    ) {
        val category = Category(
            name = data.name,
            color = data.color.toArgb(),
            icon = data.icon,
            orderNum = categoryOrderNum++,
            isSynced = false
        )

        categoryDao.save(category.toEntity())
    }

    fun categorySuggestions(): List<CreateCategoryData> = preloadCategoriesCreateData()
        .plus(
            listOf(
                CreateCategoryData(
                    name = "Car",
                    color = Blue3,
                    icon = "vehicle"
                ),

                CreateCategoryData(
                    name = "Work",
                    color = Blue2Light,
                    icon = "work"
                ),

                CreateCategoryData(
                    name = "Home",
                    color = Green2,
                    icon = "house"
                ),

                CreateCategoryData(
                    name = "Restaurant",
                    color = Orange3,
                    icon = "restaurant"
                ),

                CreateCategoryData(
                    name = "Family",
                    color = Red3Light,
                    icon = "family"
                ),

                CreateCategoryData(
                    name = "Social Life",
                    color = Blue2,
                    icon = "people"
                ),

                CreateCategoryData(
                    name = "Order food",
                    color = Orange2,
                    icon = "orderfood2"
                ),

                CreateCategoryData(
                    name = "Travel",
                    color = BlueLight,
                    icon = "travel"
                ),

                CreateCategoryData(
                    name = "Fitness",
                    color = Purple2,
                    icon = "fitness"
                ),

                CreateCategoryData(
                    name = "Self-development",
                    color = Yellow,
                    icon = "selfdevelopment"
                ),

                CreateCategoryData(
                    name = "Clothes",
                    color = Green2Light,
                    icon = "clothes2"
                ),

                CreateCategoryData(
                    name = "Beauty",
                    color = Red3,
                    icon = "makeup"
                ),

                CreateCategoryData(
                    name = "Education",
                    color = Blue,
                    icon = "education"
                ),

                CreateCategoryData(
                    name = "Pet",
                    color = Orange3Light,
                    icon = "pet"
                ),

                CreateCategoryData(
                    name = "Sports",
                    color = Purple1,
                    icon = "sports"
                ),
            )
        )


}

