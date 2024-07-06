package llxbh.zeropointone.view.taskcyclelist

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.R
import llxbh.zeropointone.api.TaskApi
import llxbh.zeropointone.api.TaskCycleApi
import llxbh.zeropointone.base.BindingBaseFragment
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.databinding.FragmentTaskListBinding
import llxbh.zeropointone.util.MassageUtil
import llxbh.zeropointone.util.TimeUtil
import llxbh.zeropointone.view.taskcontent.TaskContentUpdateActivity
import llxbh.zeropointone.view.taskcyclecontent.TaskCycleContentCreateActivity
import llxbh.zeropointone.view.taskcyclecontent.TaskCycleContentUpdateActivity

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

    @RequiresApi(Build.VERSION_CODES.O)
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

                // 点击完成的事件
                adapter.addOnItemChildClickListener(R.id.cb_taskState) { adapter, view, position ->
                    // 获取当前信息的正确状态（当天状况）
                    val taskId = adapter.items[position].id
                    runBlocking {
                        val task = TaskCycleApi.get(taskId)
                        val isCheck = TimeUtil.isToDay(task.finishedTimes)

                        // 修正当前控件的状态
                        val checkView = view as CheckBox
                        checkView.isChecked = isCheck

                        // 根据完成与否，做出不同的判断
                        if (isCheck) {
                            // 取消 “今天” 的打卡
                            val result = TaskCycleApi.deleteToDay(task)
                            if (result) {
                                TaskCycleApi.update(task)
//                                checkView.isChecked = !false
                                updateDataOrUI()
                            } else {
                                MassageUtil.sendToast("没有检查到当天的打卡记录！")
                            }
                        } else {
                            // 完成 “今天” 的打卡
                            TaskCycleApi.onFinishOnce(task)
//                            checkView.isChecked = !true
                            updateDataOrUI()
                        }
                    }

                }
            }

            // 增加任务
            binding.fabtnTaskAdd.setOnClickListener {
                onOpenTaskCycleContent(true, null)
            }

            // 下拉刷新
            binding.srlTaskList.also {
                it.setOnRefreshListener {
                    GlobalScope.launch {
                        delay(1000)
                        launch(Dispatchers.Main) {
                            updateDataOrUI()
                        }
                        it.isRefreshing = false
                    }
                }
            }
        }

    }

    private fun onOpenTaskCycleContent(create: Boolean, taskData: TaskCycle?) {
        activity?.also {
            if (create || (taskData == null)) {
                TaskCycleContentCreateActivity.start(it)
            } else {
                TaskCycleContentUpdateActivity.start(it, taskData.id)
            }
        }
    }

    private suspend fun updateDataOrUI(list: List<Task>? = null, clear:Boolean = true) {

//        /*
//        先获取数据，对比有所不同了
//         */
//
//        if (clear) {
//            sTaskListAdapter.submitList(arrayListOf())
//            hideList.clear()
//        }
//        if (list == null) {
//            sTaskListAdapter.addAll(TaskApi.getAll())
//        } else {
//            sTaskListAdapter.addAll(list)
//        }
//        onViewComplete()

        sTaskCycleListAdapter.submitList(TaskCycleApi.getAll())
    }

}