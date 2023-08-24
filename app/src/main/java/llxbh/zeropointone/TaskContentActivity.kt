package llxbh.zeropointone

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * 清单任务的详细界面
 */
class TaskContentActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_content)
        val title: String? = intent.getStringExtra(TaskApi.TASK_PASS)

        val taskTitle = findViewById<TextView>(R.id.tv_taskTitle)
        taskTitle.text = title ?: "无"
    }

}