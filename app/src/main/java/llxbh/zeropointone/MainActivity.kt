package llxbh.zeropointone

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import llxbh.zeropointone.dao.Task

/**
 * 主界面。主要展示当前全部任务和添加删除
 */
class MainActivity: BaseActivity() {

    private val sTaskDataList = mutableListOf<Task>()
    private val sTaskListAdapter = TaskAdapter(sTaskDataList)

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
            val taskListData = when(item.itemId) {
                R.id.menu_taskList -> TaskApi.getAll()
                R.id.menu_taskTimeOrder -> TaskApi.getAllAndTimeOrder()
                R.id.menu_taskStateOrder -> TaskApi.getAllAndStateOrder()
                else -> return@runBlocking
            }
            updateDataOrUI(taskListData)
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun updateDataStateOrUI(position: Int, state: Boolean) {
        // 更新数据
        val data = sTaskDataList[position]
        data.state = state
        TaskApi.update(data)

        // 更新UI
        sTaskDataList.remove(data)
        val newPosition = if (state) sTaskDataList.size else 0
        sTaskDataList.add(newPosition, data)
        sTaskListAdapter.notifyItemMoved(position, newPosition)
    }

    private suspend fun updateDataOrUI(list: List<Task>? = null) {
        sTaskDataList.clear()
        if (list == null) {
            sTaskDataList.addAll(TaskApi.getAllAndStateOrder())
        } else {
            sTaskDataList.addAll(list)
        }
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

}
