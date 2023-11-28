package llxbh.zeropointone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private val sTaskDataList = mutableListOf<Task>()
    private val sTaskListAdapter = TaskAdapter(sTaskDataList)

    // 是否显示已经完成的任务
    private var viewComplete = false
    private var hideList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 绑定列表的视图和数据
        findViewById<RecyclerView>(R.id.rv_taskList).also {
            it.layoutManager =  LinearLayoutManager(this@MainActivity)
            it.adapter = sTaskListAdapter.apply {
                // 清单的点击事件
                setTaskClick(object: TaskAdapter.OnTaskClick {
                    override fun setOnTaskClick(position: Int) {
                        // 传递当前的清单数据给 “详细” 界面展现
                        onOpenTaskContent(false, sTaskDataList[position])
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun setOnTaskStateClick(position: Int, isChecked: Boolean) {
                        runBlocking {
                            updateDataStateOrUI(position, isChecked)
                        }
                    }

                })
            }
        }

        // 点击按钮（+）进入 “内容” 界面创建新的任务
        findViewById<Button>(R.id.btn_taskAdd).also {
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
                    updateDataOrUI(listOf(), false)
                }
                R.id.menu_taskHideComplete -> {
                    viewComplete = false
                    updateDataOrUI(listOf(), false)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateDataStateOrUI(position: Int, state: Boolean) {
        // 更新数据
        val data = sTaskDataList[position]
        data.state = state
        TaskApi.update(data)

        // 更新UI
        GlobalScope.launch {
            delay(1000)
            launch(Dispatchers.Main) {
                updateDataOrUI()
            }
        }
    }

    private suspend fun updateDataOrUI(list: List<Task>? = null, clear:Boolean = true) {
        if (clear) {
            sTaskDataList.clear()
            hideList.clear()
        }
        if (list == null) {
            sTaskDataList.addAll(TaskApi.getAll())
        } else {
            sTaskDataList.addAll(list)
        }
        onViewComplete()
        sTaskListAdapter.notifyDataSetChanged()
    }

    /**
     * 以什么样的状态进入内容界面
     */
    private fun onOpenTaskContent(create: Boolean, taskData: Task?) {
        val intent = Intent(
            this@MainActivity,
            TaskContentActivity::class.java
        )
        if (create || taskData == null) {
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
            sTaskDataList.addAll(hideList)
            hideList = mutableListOf()
        } else {
            // 隐藏
            for (index in 0 until sTaskDataList.size) {
                val data = sTaskDataList[index]
                if (data.state) {
                    hideList.add(data)
                }
            }
            sTaskDataList.removeAll(hideList)
        }
    }

}
