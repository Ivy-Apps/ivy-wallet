package com.ivy.wallet.ui.theme.components

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.Reorderable
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.utils.numberBetween
import com.ivy.wallet.utils.swap
import java.util.*

@Suppress("UNCHECKED_CAST")
@Composable
fun <T : Reorderable> BoxScope.ReorderModalSingleType(
    visible: Boolean,
    id: UUID = UUID.randomUUID(),
    TitleContent: @Composable ColumnScope.() -> Unit = {
        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = stringResource(R.string.reorder),
            style = UI.typo.b1.style(
                UI.colors.pureInverse,
                FontWeight.ExtraBold
            )
        )
    },
    initialItems: List<T>,
    dismiss: () -> Unit,
    onUpdateItemOrderNum: (item: T, newOrderNum: Double) -> Unit = { _, _ -> },
    onReordered: ((List<T>) -> Unit)? = null,
    ItemContent: @Composable (Int, T) -> Unit
) {
    ReorderModal<T>(
        visible = visible,
        id = id,
        initialItems = initialItems,
        TitleContent = TitleContent,
        dismiss = dismiss,
        onUpdateItemOrderNum = { _, item, newOrderNum ->
            onUpdateItemOrderNum(item, newOrderNum)
        },
        onReordered = { listAny ->
            onReordered?.invoke(
                listAny as? List<T> ?: error("List<T> cast exception.")
            )
        },
        ItemContent = { index, itemAny ->
            ItemContent(index, itemAny as T)
        }
    )
}

@Composable
fun <T : Reorderable> BoxScope.ReorderModal(
    visible: Boolean,
    id: UUID = UUID.randomUUID(),
    TitleContent: @Composable ColumnScope.() -> Unit = {
        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = stringResource(R.string.reorder),
            style = UI.typo.b1.style(
                UI.colors.pureInverse,
                FontWeight.ExtraBold
            )
        )
    },
    initialItems: List<Any>,
    dismiss: () -> Unit,
    onUpdateItemOrderNum: (
        itemsInNewOrder: List<Any>,
        item: T,
        newOrderNum: Double
    ) -> Unit = { _, _, _ -> },
    onReordered: ((List<Any>) -> Unit)? = null,
    ItemContent: @Composable RowScope.(Int, Any) -> Unit
) {
    var items by remember(id, initialItems) { mutableStateOf(initialItems) }
    var reOrderedList :List<Any>? by remember {
        mutableStateOf(null)
    }
    var orderNumUpdates by remember {
        mutableStateOf(
            mapOf<T, Double>()
        )
    }

    IvyModal(
        id = id,
        visible = visible,
        scrollState = null,
        dismiss = dismiss,
        PrimaryAction = {
            IvyCircleButton(
                modifier = Modifier
                    .size(48.dp)
                    .testTag("reorder_done"),
                backgroundGradient = GradientGreen,
                icon = R.drawable.ic_check,
                tint = White
            ) {
                orderNumUpdates.forEach { (item, newOrderNum) ->
                    onUpdateItemOrderNum(items, item, newOrderNum)
                }

                onReordered?.invoke(reOrderedList ?: items)
                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        TitleContent()

        Spacer(Modifier.height(24.dp))

        val colorMedium = UI.colors.medium
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = {
                RecyclerView(it).apply {
                    val itemTouchHelper = itemTouchHelper<T>(
                        colorMedium = colorMedium
                    )
                    adapter = Adapter<T>(
                        itemTouchHelper = itemTouchHelper,
                        ItemContent = ItemContent,
                        addItemOrderNumUpdate = { item, newOrderNum ->
                            orderNumUpdates = orderNumUpdates
                                .toMutableMap()
                                .apply {
                                    this[item] = newOrderNum
                                }
                        },
                        onReorderInternalList = { reorderedItems ->
                            items = reorderedItems
                            reOrderedList = reorderedItems
                        }
                    )
                    layoutManager = LinearLayoutManager(it)
                    itemTouchHelper.attachToRecyclerView(this)

                    adapter<T>().display(items)
                }
            },
            update = {
            }
        )
    }
}

@Suppress("UNCHECKED_CAST")
private class Adapter<T : Reorderable>(
    private val itemTouchHelper: ItemTouchHelper,
    private val ItemContent: @Composable RowScope.(Int, Any) -> Unit,
    private val addItemOrderNumUpdate: (item: T, orderNum: Double) -> Unit,
    private val onReorderInternalList: (List<Any>) -> Unit
) : RecyclerView.Adapter<Adapter<T>.ItemViewHolder>() {
    val data = mutableListOf<Any>()

    @SuppressLint("NotifyDataSetChanged")
    fun display(items: List<Any>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        data.swap(from, to)
        notifyItemMoved(from, to)
    }

    fun onItemMoved(item: T, to: Int) {
        val newOrderNum = calculateOrderNum<T>(
            itemsInNewOrder = data,
            to = to
        )

        data[to] = item.withNewOrderNum(newOrderNum) as? T
            ?: error("Incorrect Reorderable implementation for $item")
        addItemOrderNumUpdate(item, newOrderNum)
    }

    fun onReorderInternalList() {
        onReorderInternalList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ComposeView(parent.context))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.display(
            item = data[position],
            ItemContent = ItemContent,
            position = position
        )
    }

    override fun getItemCount() = data.size

    inner class ItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {

        fun display(
            item: Any,
            ItemContent: @Composable RowScope.(Int, Any) -> Unit,
            position: Int
        ) {
            (itemView as ComposeView).setContent {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item as? T != null) {
                        Spacer(Modifier.width(24.dp))

                        IvyIcon(
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            itemTouchHelper.startDrag(this@ItemViewHolder)
                                        }
                                    )
                                }
                                .testTag("reorder_drag_handle"),
                            icon = R.drawable.ic_drag_handle,
                            tint = UI.colors.gray,
                            contentDescription = "reorder_${position}"
                        )

                        Spacer(Modifier.width(4.dp))
                    }

                    ItemContent(adapterPosition, item)
                }
            }
        }
    }
}


@Suppress("UNCHECKED_CAST")
private fun <T : Reorderable> itemTouchHelper(
    colorMedium: Color,
): ItemTouchHelper {
    // 1. Note that I am specifying all 4 directions.
    //    Specifying START and END also allows
    //    more organic dragging than just specifying UP and DOWN.
    val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(UP or DOWN, 0) {
        var movedItem: T? = null
        var finalTo: Int? = null


        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val adapter = recyclerView.adapter<T>()

            val from = viewHolder.adapterPosition
            val to = target.adapterPosition

            val targetItem = adapter.data[from] as? T ?: return false

            if (movedItem == null) {
                movedItem = targetItem
            }
            finalTo = to

            adapter.moveItem(from, to)

            return true
        }

        override fun onSwiped(
            viewHolder: RecyclerView.ViewHolder,
            direction: Int
        ) {
            // 4. Code block for horizontal swipe.
            //    ItemTouchHelper handles horizontal swipe as well, but
            //    it is not relevant with reordering. Ignoring here.
        }

        // 1. This callback is called when a ViewHolder is selected.
        //    We highlight the ViewHolder here.
        override fun onSelectedChanged(
            viewHolder: RecyclerView.ViewHolder?,
            actionState: Int
        ) {
            super.onSelectedChanged(viewHolder, actionState)

            if (actionState == ACTION_STATE_DRAG) {
                viewHolder?.itemView?.setBackgroundColor(colorMedium.toArgb())
            }
        }

        override fun clearView(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.background = null
            val adapter = recyclerView.adapter<T>()
            if (movedItem != null && finalTo != null) {
                adapter.onItemMoved(movedItem!!, finalTo!!)
            }
            adapter.onReorderInternalList()

            movedItem = null
            finalTo = null
        }
    }
    return ItemTouchHelper(simpleItemTouchCallback)
}

@Suppress("UNCHECKED_CAST")
private fun <T : Reorderable> RecyclerView.adapter() = adapter as? Adapter<T>
    ?: error("Adapter not set or wrong adapter set to recyclerview.")

@Composable
fun ReorderButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    CircleButtonFilled(
        modifier = modifier
            .testTag("reorder_button"),
        icon = R.drawable.ic_reorder,
        onClick = onClick
    )
}


@Suppress("UNCHECKED_CAST")
private fun <T : Reorderable> calculateOrderNum(
    itemsInNewOrder: List<*>,
    to: Int
): Double {
    val itemBefore = itemsInNewOrder.getOrNull(to - 1) as? T
    val itemAfter = itemsInNewOrder.getOrNull(to + 1) as? T

    return when {
        itemBefore != null && itemAfter != null -> {
            numberBetween(
                itemBefore.getItemOrderNum(),
                itemAfter.getItemOrderNum()
            )
        }
        itemBefore != null && itemAfter == null -> {
            //It's last in it's priority
            itemBefore.getItemOrderNum() + 1
        }
        itemBefore == null && itemAfter != null -> {
            //It's first in it's priority
            itemAfter.getItemOrderNum() - 1
        }
        else -> 0.0
    }
}