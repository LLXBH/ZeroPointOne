package llxbh.zeropointone

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.dao.Task
import llxbh.zeropointone.databinding.TaskItemBinding
import llxbh.zeropointone.tools.TaskApi
import llxbh.zeropointone.tools.TimeTools

class TaskAdapter: BaseQuickAdapter<Task, TaskAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: TaskItemBinding = TaskItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int, item: Task?) {

        holder.binding.apply {
            if (item == null) {
                return
            }
            task = item
            // 数据为空的时候不显示
            if (item.content.isEmpty()) {
                tvTaskContent.visibility = View.GONE
            } else {
                tvTaskContent.visibility = View.VISIBLE
            }
            if (item.startTimes == 0L) {
                tvTaskDate.visibility = View.GONE
            } else {
                tvTaskDate.visibility = View.VISIBLE
                tvTaskDate.text = TimeTools.timesToString(item.startTimes)
            }
            // 点击完成与否
            cbTaskState.setOnClickListener {
                val isChecked = cbTaskState.isChecked
                cbTaskState.setChecked(isChecked)
                item.state = isChecked
                runBlocking {
                    TaskApi.update(item)
                }
            }
        }
    }

}