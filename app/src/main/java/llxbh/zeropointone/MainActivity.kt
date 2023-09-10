package llxbh.zeropointone

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
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
        runBlocking {
            findViewById<RecyclerView>(R.id.rv_taskList).also {
                it.layoutManager =  LinearLayoutManager(this@MainActivity)
                it.adapter = sTaskListAdapter.apply {
                    // 清单的点击事件
                    setTaskClick(object: TaskAdapter.OnTaskClick {
                        override fun setOnTaskClick(position: Int) {
                            // 传递当前的清单数据给 “详细” 界面展现
                            val intent = Intent(
                                this@MainActivity,
                                TaskContentActivity::class.java
                            )
                            intent.putExtra(
                                TaskApi.TASK_PASS,
                                sTaskDataList[position].id
                            )
                            startActivity(intent)
                        }

                        override fun setOnTaskStateClick(position: Int, isChecked: Boolean) {
                            runBlocking {
                                TaskApi.update(sTaskDataList[position].apply {
                                    state = isChecked
                                })
                            }
                        }

                    })
                }
                updateDataOrUI()
            }
        }

        // 点击按钮（+）进入 “内容” 界面创建新的任务
        findViewById<Button>(R.id.btn_taskAdd).also {
            it.setOnClickListener {
                val intent = Intent(
                    this@MainActivity,
                    TaskContentActivity::class.java
                )
                startActivity(intent)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        runBlocking { updateDataOrUI() }
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

    private suspend fun updateDataOrUI() {
        updateDataOrUI(TaskApi.getAll())
    }
    private fun updateDataOrUI(list: List<Task>) {
        sTaskDataList.clear()
        sTaskDataList.addAll(list)
        sTaskListAdapter.notifyDataSetChanged()
    }

}
