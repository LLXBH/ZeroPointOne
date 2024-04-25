package llxbh.zeropointone.view.taskcontent

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.dragswipe.listener.DragAndSwipeDataCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import llxbh.zeropointone.data.model.TaskCheck
import llxbh.zeropointone.databinding.ItemTaskContentItemsBinding

class TaskContentCheckAdapter:
    // BRVHA
    BaseQuickAdapter<TaskCheck, TaskContentCheckAdapter.VH>(),
    // 拖拽功能接口
    DragAndSwipeDataCallback
{

    class VH(
        parent: ViewGroup,
        val binding: ItemTaskContentItemsBinding = ItemTaskContentItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: TaskCheck?) {
        holder.binding.apply {
            check = item
            // 用户点击回车的时候自动创建新的子项
            etTaskContentCheckContent.setOnKeyListener { view, keyCode, keyEvent ->
                // 用户按下了回车
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 会有两次事件，按下和放开
                    if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                        // 返回 true ，消耗掉事件
                        return@setOnKeyListener true
                    }
                    // 添加新的一行子项
                    val newCheckViewPosition = holder.bindingAdapterPosition + 1
                    this@TaskContentCheckAdapter.add(
                        newCheckViewPosition,
                        TaskCheck()
                    )
                    // 将输入焦点传递给下一个
                    // 利用延时暂停一会再传递，避免视图还未创建好
                    GlobalScope.launch {
                        delay(500)
                        withContext(Dispatchers.Main) {
                            this@TaskContentCheckAdapter.recyclerView[newCheckViewPosition].requestFocus()
                        }
                    }
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            executePendingBindings()
        }
    }

    override fun dataMove(fromPosition: Int, toPosition: Int) {
        move(fromPosition, toPosition)
    }

    override fun dataRemoveAt(position: Int) {
        removeAt(position)
    }

}