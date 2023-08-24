package llxbh.zeropointone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 主界面。主要展示当前全部任务和添加删除
 */
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val taskDataList = TaskApi.getTaskList()
        findViewById<RecyclerView>(R.id.rv_taskList).also {
            it.layoutManager =  LinearLayoutManager(this)
            it.adapter = TaskAdapter(TaskApi.getTaskList()).apply {
                // 当个清单的点击事件
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

        val taskAdd = findViewById<Button>(R.id.btn_taskAdd)
    }

}
