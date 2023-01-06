import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview
import com.ivy.resources.R

// TODO: Not refactored legacy code. It can be improved!

@Composable
fun <T> BoxScope.ReorderModal(
    modal: IvyModal,
    level: Int = 1,
    items: List<T>,
    onReorder: (reordered: List<T>) -> Unit,
    itemContent: @Composable RowScope.(Int, T) -> Unit,
) {
    var reorderedList = remember(items) { listOf<T>() }

    Modal(
        modal = modal,
        level = level,
        contentModifier = Modifier, // fixes Compose compiler crash
        actions = {
            Positive(text = stringResource(R.string.reorder)) {
                onReorder(reorderedList)
                modal.hide()
            }
        }
    ) {
        Title(text = stringResource(R.string.reorder))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val mediumColor = UI.colors.medium
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(UI.colors.pure)
                    .padding(vertical = 16.dp),
                factory = {
                    RecyclerView(it).apply {
                        val itemTouchHelper = itemTouchHelper<T>(
                            mediumColor = mediumColor
                        )
                        adapter = ReorderAdapter(
                            itemTouchHelper = itemTouchHelper,
                            itemContent = itemContent,
                            onReorder = { reordered ->
                                reorderedList = reordered
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
}


@Suppress("UNCHECKED_CAST")
class ReorderAdapter<T>(
    private val itemTouchHelper: ItemTouchHelper,
    private val itemContent: @Composable RowScope.(Int, T) -> Unit,
    private val onReorder: (reordered: List<T>) -> Unit
) : RecyclerView.Adapter<ReorderAdapter<T>.ItemViewHolder>() {
    val data = mutableListOf<T>()

    @SuppressLint("NotifyDataSetChanged")
    fun display(items: List<T>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        swap(from, to)
        notifyItemMoved(from, to)
    }

    private fun swap(from: Int, to: Int) {
        val temp = data[from]
        data[from] = data[to]
        data[to] = temp
    }

    fun onItemMoved(item: T, to: Int) {
        data[to] = item
    }

    fun onReorderInternalList() {
        onReorder(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ComposeView(parent.context))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.display(
            item = data[position],
            ItemContent = itemContent,
            position = position
        )
    }

    override fun getItemCount() = data.size

    inner class ItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {

        fun display(
            item: T,
            ItemContent: @Composable RowScope.(Int, T) -> Unit,
            position: Int
        ) {
            (itemView as ComposeView).setContent {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SpacerHor(width = 8.dp)
                    IconRes(
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
                        tint = UI.colors.neutral,
                        contentDescription = "reorder_${position}"
                    )
                    ItemContent(absoluteAdapterPosition, item)
                }
            }
        }
    }
}


@Suppress("UNCHECKED_CAST")
fun <T> itemTouchHelper(
    mediumColor: Color,
): ItemTouchHelper {
    // 1. Note that I am specifying all 4 directions.
    //    Specifying START and END also allows
    //    more organic dragging than just specifying UP and DOWN.
    val simpleItemTouchCallback =
        object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
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

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.setBackgroundColor(mediumColor.toArgb())
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
private fun <T> RecyclerView.adapter() = adapter as? ReorderAdapter<T>
    ?: error("Adapter not set or wrong adapter set to recyclerview.")


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        ReorderModal(
            modal = modal,
            items = (1..100).toList(),
            onReorder = {},
        ) { _, item ->
            SpacerHor(width = 8.dp)
            B1Second(text = "Number: $item")
        }
    }
}
// endregion
