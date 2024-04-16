package llxbh.zeropointone.view.taskcyclelist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.databinding.ObservableBoolean
import com.chad.library.adapter4.BaseQuickAdapter
import llxbh.zeropointone.base.BindingBaseFragment
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.databinding.FragmentTaskListBinding
import llxbh.zeropointone.view.taskcontent.TaskContentCreateActivity
import llxbh.zeropointone.view.taskcontent.TaskContentUpdateActivity
import llxbh.zeropointone.view.taskcyclecontent.TaskCycleContentCreateActivity

/**
 * 周期任务列表
 */
class TaskCycleListFragment: BindingBaseFragment<FragmentTaskListBinding>() {

    private var mSelectDeleteMode = ObservableBoolean(false)
    private var sTaskCycleListAdapter = TaskCycleListAdapter()

    companion object {

        fun newInstance(): TaskCycleListFragment {
            return TaskCycleListFragment()
        }

    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentTaskListBinding {
        return FragmentTaskListBinding.inflate(
            layoutInflater,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBinding().also { binding ->
            // 绑定适配器
            binding.rvTaskList.adapter = sTaskCycleListAdapter.also { adapter ->
                // 打开对应的内容界面
                adapter.setOnItemClickListener { adapter, view, position ->
                    onOpenTaskCycleContent(false, adapter.getItem(position))
                }

                // 长按列表，进入多选删除模式
                adapter.setOnItemLongClickListener { adapter, view, position ->
                    sTaskCycleListAdapter.onSelectDeleteMode()
                    true
                }
            }

            // 增加任务
            binding.fabtnTaskAdd.setOnClickListener {
                onOpenTaskCycleContent(true, null)
            }
        }

    }

    private fun onOpenTaskCycleContent(create: Boolean, taskData: TaskCycle?) {
        activity?.also {
            if (create || (taskData == null)) {
                TaskCycleContentCreateActivity.start(it)
            } else {
//                TaskContentUpdateActivity.start(taskData.id, it)
            }
//            sTaskCycleListAdapter.addAll(listOf(
//                TaskCycle(id = 0, title = "Test1"),
//                TaskCycle(id = 0, title = "Test2"),
//                TaskCycle(id = 0, title = "Test3")
//            ))
        }
    }

}