package llxbh.zeropointone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.dao.AppDatabase
import llxbh.zeropointone.dao.Task

/**
 * 主界面。主要展示当前全部任务和添加删除
 */
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 绑定列表的视图和数据
        runBlocking {
            val taskDataList = TaskApi.getAll()
            findViewById<RecyclerView>(R.id.rv_taskList).also {
                it.layoutManager =  LinearLayoutManager(this@MainActivity)
                it.adapter = TaskAdapter(taskDataList).apply {
                    // 清单的点击事件
                    setTaskClick(object: TaskAdapter.OnTaskClick {
                        override fun setOnTaskClick(position: Int) {
                            // 传递当前的清单数据给 “详细” 界面展现
                            val intent = Intent(this@MainActivity, TaskContentActivity::class.java)
                            intent.putExtra(TaskApi.TASK_PASS, taskDataList[position].title)
                            startActivity(intent)
                        }
                    })
                }
            }
        }


        val taskAdd = findViewById<Button>(R.id.btn_taskAdd)
    }

}
