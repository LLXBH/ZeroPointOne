package llxbh.zeropointone.view.taskcyclelist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.databinding.ItemTaskCycleBinding

class TaskCycleListAdapter: BaseQuickAdapter<TaskCycle, TaskCycleListAdapter.VH>() {

    // 根据状态来控制控件的显示状态
    private var mSelectDeleteMode = ObservableBoolean(false)
    private var mDeleteDataMap = hashMapOf<TaskCycle, Boolean>()

    class VH(
        parent: ViewGroup,
        val binding: ItemTaskCycleBinding = ItemTaskCycleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: TaskCycle?) {
        holder.binding.apply {
            // 多选删除功能
            selectDeleteMode = mSelectDeleteMode
            // 通过 Map 保存选择的状态，避免 RecyclerView 的复用机制，导致显示混乱
            cbTaskSelectDelete.isChecked = mDeleteDataMap[item] ?: false
            cbTaskSelectDelete.setOnClickListener {
                if (item != null) {
                    mDeleteDataMap[item] = (it as CheckBox).isChecked
                }
            }

            // 任务变量
            taskCycle = item

            // 根据开始时间和结束时间，获取该范围内已经完成的次数
            val finishedNum = arrayListOf<Long>()
            taskCycle?.also {
                for (times in it.finishedTimes) {
                    if (times >= it.startTimes && times <= it.endTimes) {
                        // 把符合范围内的时间添加进入
                        finishedNum.add(times)
                    }
                }
            }
            tvTaskFinishedNum.text = finishedNum.size.toString()
        }
    }

    /**
     * 进入或退出多选删除状态
     *
     * 通过 ObservableBoolean 提醒各个控件变化
     */
    fun onSelectDeleteMode() {
        mSelectDeleteMode.set(!mSelectDeleteMode.get())
    }
}