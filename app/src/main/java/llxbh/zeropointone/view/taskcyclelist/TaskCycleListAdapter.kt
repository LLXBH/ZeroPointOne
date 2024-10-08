package llxbh.zeropointone.view.taskcyclelist

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.databinding.ItemTaskCycleBinding
import llxbh.zeropointone.util.time.TimeUtil

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int, item: TaskCycle?) {
        holder.binding.apply {
            taskCycle = item
            timeTools = TimeUtil

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
            taskCycle = item?.apply {
                state = TimeUtil.isToDay(taskCycle?.finishedTimes ?: listOf())
            }
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