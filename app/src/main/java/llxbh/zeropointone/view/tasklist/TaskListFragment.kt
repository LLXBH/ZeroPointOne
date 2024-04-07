package llxbh.zeropointone.view.tasklist

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import com.chad.library.adapter4.BaseQuickAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.R
import llxbh.zeropointone.base.BindingBaseFragment
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.databinding.FragmentTaskListBinding
import llxbh.zeropointone.api.TaskApi
import llxbh.zeropointone.view.taskcontent.TaskContentCreateActivity
import llxbh.zeropointone.view.taskcontent.TaskContentUpdateActivity

/**
 * 主界面。主要展示当前全部任务和添加删除
 */
class TaskListFragment: BindingBaseFragment<FragmentTaskListBinding>() {

    companion object {

        fun newInstance(): TaskListFragment {
            return TaskListFragment()
        }

    }

    private val sTaskListAdapter = TaskListAdapter()

    // 是否显示已经完成的任务
    private var viewComplete = false
    private var hideList = mutableListOf<Task>()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 告知 Fragment 此页面有选项菜单
        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 绑定列表的视图和数据
        getBinding().rvTaskList.also {
            it.adapter = sTaskListAdapter.apply {
                setOnItemClickListener { adapter, view, position ->
                    onOpenTaskContent(false, adapter.getItem(position))
                }
                // 点击任务的完成状态
                addOnItemChildClickListener(R.id.cb_taskState) { adapter, view, position ->
                    // 切换控件状态
                    val taskStateView = view as CheckBox
                    val isChecked = taskStateView.isChecked
                    taskStateView.isChecked = isChecked
                    // 更新数据
                    val data = adapter.items[position]
                    data.state = isChecked
                    runBlocking {
                        TaskApi.update(data)
                        updateDataOrUI()
                    }
                }
                // 长按列表，进入多选删除模式
                setOnItemLongClickListener(object : BaseQuickAdapter.OnItemLongClickListener<Task> {

                    override fun onLongClick(
                        adapter: BaseQuickAdapter<Task, *>,
                        view: View,
                        position: Int
                    ): Boolean {
                        onSelectDeleteMode()
                        return true
                    }

                })
            }
        }

        // 下拉刷新
        getBinding().srlTaskList.also {
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

        // 点击按钮（+）进入 “内容” 界面创建新的任务
        getBinding().fabtnTaskAdd.also {
            it.setOnClickListener {
                onOpenTaskContent(true, null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (sTaskListAdapter.items.isEmpty()) {
            getBinding().srlTaskList.isRefreshing = true
            GlobalScope.launch {
                delay(1000)
                launch(Dispatchers.Main) {
                    updateDataOrUI()
                }
                getBinding().srlTaskList.isRefreshing = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // 加载菜单资源
        inflater.inflate(R.menu.task_list_fragment_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        runBlocking {
            when(item.itemId) {
                R.id.menu_taskViewComplete -> {
                    viewComplete = true
                    updateDataOrUI(arrayListOf(), false)
                }
                R.id.menu_taskHideComplete -> {
                    viewComplete = false
                    updateDataOrUI(arrayListOf(), false)
                }
                R.id.menu_taskList -> {
                    updateDataOrUI(TaskApi.getAll())
                }
                R.id.menu_taskTimeOrder -> {
                    updateDataOrUI(TaskApi.getAllAndTimeOrder())
                }
                R.id.menu_taskStateOrder -> {
                    updateDataOrUI(TaskApi.getAllAndStateOrder())
                }
                R.id.menu_taskRecycleBin -> {
                    updateDataOrUI(TaskApi.getRecycleBin())
                }
                R.id.menu_taskSelectDelete -> {
                    sTaskListAdapter.onDeleteSelect()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun updateDataOrUI(list: List<Task>? = null, clear:Boolean = true) {
        /*
        先获取数据，对比有所不同了
         */

        if (clear) {
            sTaskListAdapter.submitList(arrayListOf())
            hideList.clear()
        }
        if (list == null) {
            sTaskListAdapter.addAll(TaskApi.getAll())
        } else {
            sTaskListAdapter.addAll(list)
        }
        onViewComplete()
    }

    /**
     * 以什么样的状态进入内容界面
     */
    private fun onOpenTaskContent(create: Boolean, taskData: Task?) {
        activity?.also {
            if (create || (taskData == null)) {
                startActivity(Intent(
                    it,
                    TaskContentCreateActivity::class.java
                ))
            } else {
                TaskContentUpdateActivity.start(taskData.id, it)
            }
        }
    }

    private fun onViewComplete() {
        if (viewComplete) {
            // 显示
            sTaskListAdapter.addAll(hideList)
            hideList = mutableListOf()
        } else {
            // 隐藏
            val end = sTaskListAdapter.items.size
            for (index in 0 until end) {
                val data = sTaskListAdapter.items[index]
                if (data.state) {
                    hideList.add(data)
                }
            }
            // 剔除掉已经完成的
            val newData = arrayListOf<Task>()
            newData.addAll(sTaskListAdapter.items)
            newData.removeAll(hideList)
            sTaskListAdapter.submitList(newData)
        }
    }

}
