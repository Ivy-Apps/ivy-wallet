package com.ivy.wallet.domain.deprecated.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.stringRes
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

    suspend fun preloadAccounts() {
        val cash = Account(
            name = stringRes(R.string.cash),
            currency = null,
            color = Green.toArgb(),
            icon = "cash",
            orderNum = 0.0,
            isSynced = false
        )

        val bank = Account(
            name = stringRes(R.string.bank),
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
            name = stringRes(R.string.cash),
            currency = baseCurrency,
            color = Green,
            icon = "cash",
            balance = 0.0
        ),
        CreateAccountData(
            name = stringRes(R.string.bank),
            currency = baseCurrency,
            color = IvyDark,
            icon = "bank",
            balance = 0.0
        ),
        CreateAccountData(
            name = stringRes(R.string.revoult),
            currency = baseCurrency,
            color = Blue,
            icon = "revolut",
            balance = 0.0
        ),
    )

    suspend fun preloadCategories() {
        categoryOrderNum = 0.0

        val categoriesToPreload = preloadCategoriesCreateData()

        for (createData in categoriesToPreload) {
            preloadCategory(createData)
        }
    }

    private fun preloadCategoriesCreateData() = listOf(
        CreateCategoryData(
            name = stringRes(R.string.food_drinks),
            color = Green,
            icon = "fooddrink"
        ),

        CreateCategoryData(
            name = stringRes(R.string.bills_fees),
            color = Red,
            icon = "bills"
        ),

        CreateCategoryData(
            name = stringRes(R.string.transport),
            color = YellowLight,
            icon = "transport"
        ),

        CreateCategoryData(
            name = stringRes(R.string.groceries),
            color = GreenLight,
            icon = "groceries"
        ),

        CreateCategoryData(
            name = stringRes(R.string.entertainment),
            color = Orange,
            icon = "game"
        ),

        CreateCategoryData(
            name = stringRes(R.string.shopping),
            color = Ivy,
            icon = "shopping"
        ),

        CreateCategoryData(
            name = stringRes(R.string.gifts),
            color = RedLight,
            icon = "gift"
        ),

        CreateCategoryData(
            name = stringRes(R.string.health),
            color = IvyLight,
            icon = "health"
        ),

        CreateCategoryData(
            name = stringRes(R.string.investments),
            color = IvyDark,
            icon = "leaf"
        ),

        CreateCategoryData(
            name = stringRes(R.string.loans),
            color = BlueDark,
            icon = "loan"
        ),
    )

    private suspend fun preloadCategory(
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
                    name = stringRes(R.string.car),
                    color = Blue3,
                    icon = "vehicle"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.work),
                    color = Blue2Light,
                    icon = "work"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.home_category),
                    color = Green2,
                    icon = "house"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.restaurant),
                    color = Orange3,
                    icon = "restaurant"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.family),
                    color = Red3Light,
                    icon = "family"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.social_life),
                    color = Blue2,
                    icon = "people"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.order_food),
                    color = Orange2,
                    icon = "orderfood2"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.travel),
                    color = BlueLight,
                    icon = "travel"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.fitness),
                    color = Purple2,
                    icon = "fitness"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.self_development),
                    color = Yellow,
                    icon = "selfdevelopment"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.clothes),
                    color = Green2Light,
                    icon = "clothes2"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.beauty),
                    color = Red3,
                    icon = "makeup"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.education),
                    color = Blue,
                    icon = "education"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.pet),
                    color = Orange3Light,
                    icon = "pet"
                ),

                CreateCategoryData(
                    name = stringRes(R.string.sports),
                    color = Purple1,
                    icon = "sports"
                ),
            )
        )


}

