package llxbh.zeropointone

import android.content.Intent
import android.os.Bundle
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
class MainActivity: AppCompatActivity() {

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
                    })
                }
                updateDataOrUI()
            }
        }

        // 点击按钮进入 “内容” 界面创建新的任务
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

    private suspend fun updateDataOrUI() {
        withContext(Dispatchers.IO) {
            sTaskDataList.clear()
            sTaskDataList.addAll(TaskApi.getAll())
        }
        sTaskListAdapter.notifyDataSetChanged()
    }

}
