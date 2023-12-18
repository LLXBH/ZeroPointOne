package llxbh.zeropointone

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.dragswipe.listener.DragAndSwipeDataCallback
import llxbh.zeropointone.data.TaskCheck
import llxbh.zeropointone.databinding.TaskContentCheckItemBinding

class TaskContentCheckAdapter:
    // BRVHA
    BaseQuickAdapter<TaskCheck, TaskContentCheckAdapter.VH>(),
    // 拖拽功能接口
    DragAndSwipeDataCallback {

    class VH(
        parent: ViewGroup,
        val binding: TaskContentCheckItemBinding = TaskContentCheckItemBinding.inflate(
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