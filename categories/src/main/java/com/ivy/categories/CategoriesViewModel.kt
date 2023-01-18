package com.ivy.categories

import com.ivy.categories.data.CategoryListItemUi
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.calculate.Stats
import com.ivy.core.domain.action.calculate.category.CatStatsFlow
import com.ivy.core.domain.action.category.CategoriesListFlow
import com.ivy.core.domain.action.data.CategoryListItem
import com.ivy.core.domain.action.period.SelectedPeriodFlow
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.domain.pure.util.combineList
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.action.mapping.MapSelectedPeriodUiAct
import com.ivy.data.Value
import com.ivy.data.category.Category
import com.ivy.data.time.TimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesListFlow: CategoriesListFlow,
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val mapSelectedPeriodUiAct: MapSelectedPeriodUiAct,
    private val categoryStatsFlow: CatStatsFlow,
    private val mapCategoryUiAct: MapCategoryUiAct,
) : SimpleFlowViewModel<CategoriesState, CategoriesEvent>() {
    override val initialUi = CategoriesState(
        selectedPeriod = null,
        items = listOf(),
        emptyState = true,
    )

    override val uiFlow: Flow<CategoriesState> = combine(
        selectedPeriodFlow(), categoryItemsUi()
    ) { period, items ->
        CategoriesState(
            selectedPeriod = mapSelectedPeriodUiAct(period),
            items = items,
            emptyState = items.isEmpty(),
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun categoryItemsUi(): Flow<List<CategoryListItemUi>> = combine(
        selectedPeriodFlow(),
        categoriesListFlow(CategoriesListFlow.Input(trnType = null))
    ) { period, list ->
        val range = period.range
        combineList(list.map { item ->
            when (item) {
                is CategoryListItem.Archived -> {
                    combineList(
                        item.categories.map { category ->
                            categoryCardFlow(category, range)
                        }
                    ).map { cards ->
                        CategoryListItemUi.Archived(
                            categoryCards = cards,
                            count = cards.size
                        )
                    }
                }
                is CategoryListItem.CategoryHolder -> categoryCardFlow(item.category, range)
                is CategoryListItem.ParentCategory -> {
                    combine(
                        categoryStatsFlow(
                            CatStatsFlow.Input(
                                category = item.parent,
                                range = range,
                            )
                        ),
                        combineList(item.children.map { category ->
                            categoryStatsFlow(
                                CatStatsFlow.Input(
                                    category = category,
                                    range = range,
                                )
                            ).map { stats ->
                                CategoryListItemUi.CategoryCard(
                                    category = mapCategoryUiAct(category),
                                    balance = format(stats.balance, shortenFiat = true),
                                ) to stats
                            }
                        })
                    ) { parentStats, children ->
                        CategoryListItemUi.ParentCategory(
                            parentCategory = mapCategoryUiAct(item.parent),
                            balance = parentCategoryBalance(
                                parentStats,
                                children.map { it.second }
                            ),
                            categoryCards = children.map { it.first },
                            categoriesCount = children.size
                        )
                    }
                }

            }
        }).map { it }
    }.flatMapLatest { it }

    private fun parentCategoryBalance(parentStats: Stats, children: List<Stats>): ValueUi {
        val totalBalance = parentStats.balance.amount + children.sumOf { it.balance.amount }
        return format(Value(totalBalance, parentStats.balance.currency), shortenFiat = true)
    }

    private fun categoryCardFlow(
        category: Category, range: TimeRange
    ): Flow<CategoryListItemUi.CategoryCard> = categoryStatsFlow(
        CatStatsFlow.Input(
            category = category,
            range = range
        )
    ).map { stats ->
        CategoryListItemUi.CategoryCard(
            category = mapCategoryUiAct(category),
            balance = format(stats.balance, shortenFiat = true),
        )
    }


    // region Event handling
    override suspend fun handleEvent(event: CategoriesEvent) = when (event) {
        is CategoriesEvent.CategoryClick -> handleCategoryClick(event)
    }

    private fun handleCategoryClick(event: CategoriesEvent.CategoryClick) {
        // TODO: Implement
    }
    // endregion
}
