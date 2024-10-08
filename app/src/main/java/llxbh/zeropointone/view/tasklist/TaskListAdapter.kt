package llxbh.zeropointone.view.tasklist

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.databinding.ItemTaskBinding
import llxbh.zeropointone.api.TaskApi
import llxbh.zeropointone.util.time.TimeUtil

class TaskListAdapter: BaseQuickAdapter<Task, TaskListAdapter.VH>() {

    // 根据状态来控制控件的显示
    private var mSelectDeleteMode = ObservableBoolean(false)
    private var mDeleteDataMap = hashMapOf<Task, Boolean>()

    class VH(
        parent: ViewGroup,
        val binding: ItemTaskBinding = ItemTaskBinding.inflate(
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
            selectDeleteMode = mSelectDeleteMode
            timeTools = TimeUtil

            // 通过 Map 保存选择的状态，避免 RecyclerView 的复用机制，导致显示混乱
            cbTaskSelectDelete.isChecked = mDeleteDataMap[item] ?: false
            cbTaskSelectDelete.setOnClickListener {
                if (item != null) {
                    mDeleteDataMap[item] = (it as CheckBox).isChecked
                }
            }

            if (item == null) {
                return
            }
            task = item
        }

    }

    fun onSelectDeleteMode() {
        mSelectDeleteMode.set(!mSelectDeleteMode.get())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDeleteSelect() {
        for (data in mDeleteDataMap) {
            if (data.value) {
                remove(data.key)
                GlobalScope.launch {
                    TaskApi.delete(data.key)
                }
            }
        }
        mDeleteDataMap.clear()
    }

}