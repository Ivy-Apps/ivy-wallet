package com.ivy.pie_charts

import com.ivy.base.*
import com.ivy.data.Category
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.pie_charts.action.PieChartAct
import com.ivy.pie_charts.action.SubCategoryAct
import com.ivy.pie_charts.model.CategoryAmount
import com.ivy.screens.PieChartStatistic
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import com.ivy.wallet.utils.dateNowUTC
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.readOnly
import com.ivy.wallet.utils.replace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PieChartStatisticViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val pieChartAct: PieChartAct,
    private val subCategoryAct: SubCategoryAct,
    private val sharedPrefs: SharedPrefs
) : FRPViewModel<PieChartStatisticState, PieChartStatisticEvent>() {

    override val _state: MutableStateFlow<PieChartStatisticState> = MutableStateFlow(
        PieChartStatisticState()
    )

    private val _treatTransfersAsIncomeExpense = MutableStateFlow(false)
    private val treatTransfersAsIncomeExpense = _treatTransfersAsIncomeExpense.readOnly()

    override suspend fun handleEvent(event: PieChartStatisticEvent): suspend () -> PieChartStatisticState =
        withContext(Dispatchers.Default) {
            when (event) {
                is PieChartStatisticEvent.Start -> startNew(event.screen)
                is PieChartStatisticEvent.OnSelectNextMonth -> nextMonthNew()
                is PieChartStatisticEvent.OnSelectPreviousMonth -> previousMonthNew()
                is PieChartStatisticEvent.OnSetPeriod -> onSetPeriodNew(event.timePeriod)
                is PieChartStatisticEvent.OnShowMonthModal -> configureMonthModalNew(event.timePeriod)
                is PieChartStatisticEvent.OnCategoryClicked -> onCategoryClickedNew(event.category)
                is PieChartStatisticEvent.OnSubCategoryListExpanded -> onSubcategoryListExpandNew(
                    event.parentCategoryAmount,
                    event.expandedState
                )
                is PieChartStatisticEvent.OnUnpackSubCategories -> onSubcategoriesUnpackedNew(event.unpackAllSubCategories)
            }
        }

    private suspend fun startNew(screen: PieChartStatistic) = suspend {
        updateGlobalVariables(screen)
    } then {
        initializePreliminaryData(screen, ivyContext)
    } then {
        loadNew(ivyContext.selectedPeriod)
    }

    private fun updateGlobalVariables(screen: PieChartStatistic) {
        _treatTransfersAsIncomeExpense.value = screen.treatTransfersAsIncomeExpense
    }

    private suspend fun initializePreliminaryData(
        screen: PieChartStatistic,
        ivyContext: IvyWalletCtx
    ) = suspend {
        val baseCurrency = ioThread { settingsDao.findFirst() }.currency
        baseCurrency
    } thenInvokeAfter { baseCurrency ->
        updateState {
            it.copy(
                period = ivyContext.selectedPeriod,
                transactionType = screen.type,
                accountIdFilterList = screen.accountList,
                filterExcluded = screen.filterExcluded,
                transactions = screen.transactions,
                showCloseButtonOnly = screen.transactions.isNotEmpty(),
                baseCurrency = baseCurrency,
                unpackAllSubCategories = false,
                showUnpackOption = false
            )
        }
    }

    //-----------------------------------------------------------------------------------
    private suspend fun loadNew(timePeriod: TimePeriod) =
        suspend { timePeriod } then
                ::computePieChartActInputParams then
                ::computePieChartAct then
                ::applySubCategoriesTransformation then
                ::computeChartSpecificUIList thenInvokeAfter {
            finishLoading(timePeriod, input = it)
        }

    private fun computePieChartActInputParams(
        period: TimePeriod,
        state: PieChartStatisticState = stateVal()
    ): Pair<Boolean, FromToTimeRange> {
        val treatTransferAsIncExp =
            sharedPrefs.getBoolean(
                SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                false
            ) && state.accountIdFilterList.isNotEmpty() && treatTransfersAsIncomeExpense.value

        val range = period.toRange(ivyContext.startDayOfMonth)

        return Pair(treatTransferAsIncExp, range)
    }

    private suspend fun computePieChartAct(
        input: Pair<Boolean, FromToTimeRange>,
        state: PieChartStatisticState = stateVal()
    ): PieChartAct.Output {
        val (treatTransferAsIncExp, timePeriodRange) = input

        return ioThread {
            pieChartAct(
                PieChartAct.Input(
                    baseCurrency = state.baseCurrency,
                    range = timePeriodRange,
                    type = state.transactionType,
                    accountIdFilterList = state.accountIdFilterList,
                    treatTransferAsIncExp = treatTransferAsIncExp,
                    existingTransactions = state.transactions,
                    showAccountTransfersCategory = state.accountIdFilterList.isNotEmpty(),
                    filterEmptyCategoryAmounts = false
                )
            )
        }
    }

    private suspend fun applySubCategoriesTransformation(input: PieChartAct.Output) =
        suspend { input.categoryAmounts } then subCategoryAct thenInvokeAfter { list ->
            input.copy(categoryAmounts = list)
        }

    private fun computeChartSpecificUIList(
        input: PieChartAct.Output
    ): Pair<List<CategoryAmount>, PieChartAct.Output> {
        val chartUISpecificList =
            input.categoryAmounts.map { cat -> cat.copy(amount = cat.totalAmount()) }

        return Pair(chartUISpecificList, input)
    }

    private suspend fun finishLoading(
        timePeriod: TimePeriod,
        input: Pair<List<CategoryAmount>, PieChartAct.Output>
    ) = suspend {
        computeShowUnpackAllCategoriesOption(
            chartUISpecificList = input.first,
            categoryAmountList = input.second.categoryAmounts
        )
    } thenInvokeAfter { showUnpackAllCategoriesOption ->

        val (chartUISpecificList, output) = input
        updateState {
            it.copy(
                period = timePeriod,
                totalAmount = output.totalAmount,
                categoryAmounts = output.categoryAmounts,
                pieChartCategoryAmount = chartUISpecificList,
                selectedCategory = null,
                showUnpackOption = showUnpackAllCategoriesOption
            )
        }
    }

    private fun computeShowUnpackAllCategoriesOption(
        chartUISpecificList: List<CategoryAmount>,
        categoryAmountList: List<CategoryAmount>
    ) = chartUISpecificList.size !=
            categoryAmountList.filter { c -> c.subCategoryState.subCategoriesList.isEmpty() }.size

    //-----------------------------------------------------------------------------------
    private suspend fun onSetPeriodNew(period: TimePeriod) = suspend {
        ivyContext.updateSelectedPeriodInMemory(period)
    } then { period } then ::loadNew

    //-----------------------------------------------------------------------------------
    private suspend fun nextMonthNew() = suspend {
        val month = stateVal().period.month
        val year = stateVal().period.year ?: dateNowUTC().year

        Pair(month, year)
    } then {
        val (month, year) = it

        if (month != null) {
            loadNew(month.incrementMonthPeriod(ivyContext, 1L, year))
        } else
            stateVal()
    }

    //-----------------------------------------------------------------------------------
    private suspend fun previousMonthNew() = suspend {
        val month = stateVal().period.month
        val year = stateVal().period.year ?: dateNowUTC().year

        Pair(month, year)
    } then {
        val (month, year) = it

        if (month != null) {
            loadNew(month.incrementMonthPeriod(ivyContext, -1L, year))
        } else
            stateVal()
    }

    //-----------------------------------------------------------------------------------
    private suspend fun configureMonthModalNew(timePeriod: TimePeriod?) = suspend {
        val choosePeriodModalData = timePeriod?.let { ChoosePeriodModalData(period = it) }
        choosePeriodModalData
    } then { choosePeriodModalData ->
        updateState {
            it.copy(choosePeriodModal = choosePeriodModalData)
        }
    }

    //-----------------------------------------------------------------------------------
    private suspend fun onCategoryClickedNew(clickedCategory: Category?) =
        suspend { clickedCategory } then
                ::computeReorderParams then
                ::reorderSelectedCategoryToTop then
                ::finishReordering

    private fun computeReorderParams(clickedCategory: Category?): Pair<SelectedCategory?, SelectedCategory?> {
        val selectedCategory = if (clickedCategory == stateVal().selectedCategory?.category)
            null
        else
            SelectedCategory(category = clickedCategory)

        val categoryToSort =
            if (selectedCategory?.category != null && selectedCategory.category.isSubCategory()
                && !isSubCategoryListUnpacked()
            ) {
                SelectedCategory(findParentCategory(selectedCategory.category))
            } else {
                selectedCategory
            }

        return Pair(selectedCategory, categoryToSort)
    }

    private fun reorderSelectedCategoryToTop(
        input: Pair<SelectedCategory?, SelectedCategory?>
    ): Pair<SelectedCategory?, List<CategoryAmount>> {
        val (selectedCategory, categoryToSort) = input
        val existingCategoryAmounts = stateVal().categoryAmounts
        val newCategoryAmounts = if (categoryToSort != null && selectedCategory != null) {
            existingCategoryAmounts
                .sortedByDescending { it.totalAmount() }
                .sortedByDescending { categoryToSort.category == it.category }
                .map { parentCategory ->
                    val subCatList = parentCategory.subCategoryState.subCategoriesList
                        .sortedByDescending { sc -> sc.amount }
                        .sortedByDescending { sc -> selectedCategory.category == sc.category }

                    parentCategory.copy(
                        subCategoryState = parentCategory.subCategoryState.copy(
                            subCategoriesList = subCatList
                        )
                    )
                }
        } else {
            existingCategoryAmounts.sortedByDescending {
                it.totalAmount()
            }
        }

        return Pair(selectedCategory, newCategoryAmounts)
    }

    private suspend fun finishReordering(input: Pair<SelectedCategory?, List<CategoryAmount>>) =
        suspend {
            updateState {
                it.copy(
                    selectedCategory = input.first,
                    categoryAmounts = input.second
                )
            }
        } thenInvokeAfter { state ->
            state
        }

    //-----------------------------------------------------------------------------------
    private suspend fun onSubcategoryListExpandNew(
        parentCategoryAmt: CategoryAmount,
        expandedState: Boolean
    ) = suspend {
        computeSubcategoryListParams(parentCategoryAmt, expandedState)
    } then ::finishOnSubcategoryListExpand

    private suspend fun computeSubcategoryListParams(
        parentCategoryAmt: CategoryAmount,
        expandedState: Boolean,
        state: PieChartStatisticState = stateVal()
    ) = suspend {
        //Update CategoryExpansion State
        val newCategoryAmount = parentCategoryAmt.copy(
            subCategoryState = parentCategoryAmt.subCategoryState.copy(subCategoryListExpanded = expandedState)
        )
        state.categoryAmounts.replace(parentCategoryAmt, newCategoryAmount)
    } then { newCategoryAmountList ->
        val updatedChartSpecificUIList = computeUpdatedPieChartList(
            parentCategoryAmt = parentCategoryAmt,
            pieChartCategoryAmountList = state.pieChartCategoryAmount,
            expandedState = expandedState
        )
        Pair(newCategoryAmountList, updatedChartSpecificUIList)
    } thenInvokeAfter {
        /**
         * Returns parentCategory if a subcategory is selected,
         * Returns parentCategory if a parent category is Selected,
         * Returns null on subsequent selection of same category
         */
        val newSelectedCategory = computeUpdatedSelectedCategory(
            state.selectedCategory,
            parentCategoryAmt.category
        )

        Triple(newSelectedCategory, it.first, it.second)
    }

    private suspend fun computeUpdatedPieChartList(
        parentCategoryAmt: CategoryAmount,
        pieChartCategoryAmountList: List<CategoryAmount>,
        expandedState: Boolean
    ): List<CategoryAmount> {

        /**
         * For a given parentCategory @param [parentCatAmt]
         * returns a list where all the subcategories are present directly under the parent category
         */
        fun flattenPieChartListWithOrderPreserved(
            parentCatAmt: CategoryAmount,
            pieChartCatAmount: List<CategoryAmount>
        ): List<CategoryAmount> {
            val newPieChartList = mutableListOf<CategoryAmount>()
            pieChartCatAmount.forEach { c ->
                newPieChartList.add(c)
                if (c.category?.id == parentCatAmt.category?.id) {
                    parentCatAmt.subCategoryState.subCategoriesList.forEach { sc ->
                        newPieChartList.add(sc)
                    }
                }
            }
            return newPieChartList
        }

        /**
         * Replace the original object with [replacementCatAmt] parameter,
         * using replacementCatAmt.category.id as the key
         */
        fun replaceCategoryAmount(
            replacementCatAmt: CategoryAmount,
            pieChartCatAmt: List<CategoryAmount> = pieChartCategoryAmountList
        ): List<CategoryAmount> {
            return pieChartCatAmt.replace(
                { c ->
                    c.category?.id == replacementCatAmt.category?.id
                },
                replacementCatAmt
            )
        }


        return if (expandedState)
            suspend { replaceCategoryAmount(parentCategoryAmt) } thenInvokeAfter { list ->
                flattenPieChartListWithOrderPreserved(parentCategoryAmt, list)
            }
        else
            suspend {
                replaceCategoryAmount(
                    parentCategoryAmt.copy(amount = parentCategoryAmt.totalAmount())
                )
            } then {
                it.minus(parentCategoryAmt.subCategoryState.subCategoriesList.toSet())
            } thenInvokeAfter {
                it.sortedByDescending { ca -> ca.amount }
            }
    }

    //clears the selectedCategory if and only if selected category is subcategory of parentCategory
    private fun computeUpdatedSelectedCategory(
        selectedCategory: SelectedCategory?,
        parentCategory: Category?
    ): SelectedCategory? {
        return if (selectedCategory != null && selectedCategory.category?.parentCategoryId == parentCategory?.id)
            null
        else
            selectedCategory
    }

    private suspend fun finishOnSubcategoryListExpand(it: Triple<SelectedCategory?, List<CategoryAmount>, List<CategoryAmount>>): PieChartStatisticState {
        val (selectedCategory, categoryAmounts, chartSpecificUIList) = it

        return updateState {
            it.copy(
                pieChartCategoryAmount = chartSpecificUIList,
                categoryAmounts = categoryAmounts,
                selectedCategory = selectedCategory
            )
        }
    }

    //-----------------------------------------------------------------------------------
    private suspend fun onSubcategoriesUnpackedNew(unpackAllSubCategories: Boolean) = suspend {
        stateVal().categoryAmounts
    } then {
        packUnpackCategoryList(unpackAllSubCategories, it)
    } then { newCategoryList ->
        updateState {
            it.copy(
                pieChartCategoryAmount = newCategoryList.map { cat -> cat.copy(amount = cat.totalAmount()) },
                categoryAmounts = newCategoryList,
                unpackAllSubCategories = unpackAllSubCategories,
                selectedCategory = null
            )
        }
    }

    private suspend fun packUnpackCategoryList(
        unpackAllSubCategories: Boolean,
        categoryAmountList: List<CategoryAmount>
    ): List<CategoryAmount> {
        return if (unpackAllSubCategories)
            categoryAmountList.flatMap {
                val list = mutableListOf<CategoryAmount>()
                list.addAll(it.subCategoryState.subCategoriesList)
                list.add(it.clearSubcategoriesAndGet())

                list
            }.sortedByDescending {
                it.totalAmount()
            }
        else
            suspend { stateVal().categoryAmounts } thenInvokeAfter {
                subCategoryAct(it)
            }
    }

    private fun findParentCategory(subCategory: Category): Category? {
        return stateVal().categoryAmounts.find { it.category?.id == subCategory.parentCategoryId }?.category
    }

    private fun isSubCategoryListUnpacked() =
        stateVal().categoryAmounts.size == stateVal().pieChartCategoryAmount.size
}