package llxbh.zeropointone

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.dao.Task
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * 清单任务的详细界面
 */
class TaskContentActivity: AppCompatActivity() {

    companion object {
        val MODE_CREATE = "Task_Create"     // 创建
        val MODE_EXAMINE = "Task_Examine"   // 查看
    }

    private var mMode = MODE_EXAMINE
    private var mTaskId = 0
    private var mSelectDate: Date? = null

    private lateinit var mTaskState: CheckBox
    private lateinit var mTaskTitle: EditText
    private lateinit var mTaskContent: EditText
    private lateinit var mTaskDate: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_content)

        // 根据是否有接收到 id 判断模式
        val taskId: Int = intent.getIntExtra(TaskApi.TASK_PASS, 0)
        mMode = if (taskId == 0) {
            MODE_CREATE
        } else {
            mTaskId = 0;
            MODE_EXAMINE
        }

        // 绑定试图
        mTaskState  = findViewById(R.id.cb_taskState)
        mTaskTitle = findViewById(R.id.et_taskTitle)
        mTaskContent = findViewById(R.id.et_taskTitle)
        mTaskDate = findViewById(R.id.tv_taskDate)

        // 判断是否为 "查看" 模式，如是则需要获取数据
        if (mMode == MODE_EXAMINE) {
            runBlocking {
                val task = TaskApi.get(taskId)
                if (task == null) {
                    mTaskId = 0
                    Toast.makeText(
                        this@TaskContentActivity,
                        "找不到对于的数据！",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    mTaskId = task.id
                    updateUI(task)
                }
            }
        }

        // 点击时间则展示显示日期选
        mTaskDate.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(supportFragmentManager, "datePicker")
        }
    }

    /**
     * 绑定并且显示 Menu 菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_task_content, menu)
        return true     // 显示
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_taskInsert -> insertTask()
            R.id.menu_taskUpdate -> updateTask()
            R.id.menu_taskDelete -> deleteTask()
            else -> Toast.makeText(this, "操作异常！", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 使用输入的数据更新视图
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(data: Task) {
        mTaskState.isChecked = data.state
        mTaskTitle.setText(data.title)
        mTaskContent.setText(data.content)
        mTaskDate.text = data.date?.let { TimeTools.dateToString(it) }
    }

    /**
     * 获取当前显示的 UI 数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUiData(): Task {
        return Task(
            mTaskId,
            mTaskState.isChecked,
            mTaskTitle.text.toString(),
            mTaskContent.text.toString(),
            mSelectDate
        )
    }

    /**
     * 插入新数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertTask() {
        if (mTaskId != 0 && mMode == MODE_EXAMINE) {
            Toast.makeText(this, "当前模式不对劲！", Toast.LENGTH_SHORT)
                .show()
        } else {
            runBlocking {
                TaskApi.insert(getUiData())
                Toast.makeText(this@TaskContentActivity, "插入数据！", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * 更新当前数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTask() {
        if (mTaskId != 0 && mMode == MODE_EXAMINE) {
            runBlocking {
                TaskApi.update(getUiData())
                Toast.makeText(this@TaskContentActivity, "更新数据！", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "当前模式不对劲！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * 删除当前数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun deleteTask() {
        if (mTaskId != 0 && mMode == MODE_EXAMINE) {
            runBlocking {
                TaskApi.delete(getUiData())
                Toast.makeText(this@TaskContentActivity, "删除数据！", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "当前模式不对劲！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDate(year: Int, month: Int, day: Int) {
        mTaskDate.text = TimeTools.toString(year, month, day)
        mSelectDate = TimeTools.localDateToDate(LocalDate.of(year, month, day))
    }

}