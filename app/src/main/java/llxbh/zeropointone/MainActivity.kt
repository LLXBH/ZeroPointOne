package llxbh.zeropointone

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val taskList = findViewById<RecyclerView>(R.id.rv_taskList)
        taskList.layoutManager = LinearLayoutManager(this)
        taskList.adapter = TaskAdapter(listOf(
            TaskData(false, "数据1"),
            TaskData(false, "数据2"),
            TaskData(false, "数据3")
        ))

        val taskAdd = findViewById<Button>(R.id.btn_taskAdd)
    }

}