package llxbh.zeropointone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.base.BaseActivity
import llxbh.zeropointone.dao.Task
import llxbh.zeropointone.tools.TaskApi

/**
 * 主界面。主要展示当前全部任务和添加删除
 */
class MainActivity: BaseActivity() {

    private val sTaskListAdapter = TaskAdapter()

    // 是否显示已经完成的任务
    private var viewComplete = false
    private var hideList = mutableListOf<Task>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 绑定列表的视图和数据
        findViewById<RecyclerView>(R.id.rv_taskList).also {
            it.layoutManager =  LinearLayoutManager(this@MainActivity)
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
            }
        }

        // 点击按钮（+）进入 “内容” 界面创建新的任务
        findViewById<FloatingActionButton>(R.id.fabtn_taskAdd).also {
            it.setOnClickListener {
                onOpenTaskContent(true, null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            delay(1000)
            launch(Dispatchers.Main) {
                updateDataOrUI()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

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
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun updateDataOrUI(list: List<Task>? = null, clear:Boolean = true) {
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
        val intent = Intent(
            this@MainActivity,
            TaskContentActivity::class.java
        )
        if (create || (taskData == null)) {
            // 无需操作
        } else {
            intent.putExtra(
                TaskApi.TASK_PASS,
                taskData.id
            )
        }
        // 通知内容界面，带个数据回来
        startActivity(intent)
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
