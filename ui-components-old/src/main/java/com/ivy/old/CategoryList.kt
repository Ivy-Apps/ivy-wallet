package com.ivy.wallet.ui.category

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.Category
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.old.ListItem
import com.ivy.wallet.ui.theme.toComposeColor
import kotlinx.coroutines.launch

@Composable
internal fun CategoryList(
    categoryList: List<Category>,
    selectedCategory: Category? = null,
    onSelectedCategory: (Category?) -> Unit = {}
) {

    var selectedCat: Category? by remember(selectedCategory) {
        mutableStateOf(selectedCategory)
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    onScreenStart {
        val scrollPos =
            categoryList.indexOfFirst { it.id == selectedCat?.id }
        if (scrollPos != -1) {
            coroutineScope.launch {
                listState.scrollToItem(index = scrollPos)
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        state = listState
    ) {
        item {
            Spacer(Modifier.width(24.dp))
        }

        items(items = categoryList) { category ->
            ListItem(
                icon = category.icon,
                defaultIcon = R.drawable.ic_custom_category_s,
                text = category.name,
                selectedColor = category.color.toComposeColor().takeIf {
                    selectedCat == category
                }
            ) { selected ->
                selectedCat = if (!selected) category else null
                onSelectedCategory(selectedCat)
            }
        }

        item {
            Spacer(Modifier.width(24.dp))
        }
    }
}