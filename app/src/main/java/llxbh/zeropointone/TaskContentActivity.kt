package llxbh.zeropointone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.dao.Task
import java.time.LocalDate
import java.util.Date

/**
 * 清单任务的详细界面
 */
class TaskContentActivity: BaseActivity() {

    companion object {
        val MODE_CREATE = "Task_Create"     // 创建
        val MODE_EXAMINE = "Task_Examine"   // 查看
    }

    private var mMode = MODE_EXAMINE
    private var mTaskId = 0
    private var mState = false
    private var mSelectDate: Date? = null

    private lateinit var mTaskState: TaskStateButton
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
        mTaskState  = findViewById(R.id.ibtn_taskState)
        mTaskState.setOnClickListener {
            mState = !mState
            mTaskState.state = mState
        }

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
            val newFragment = DatePickerDialogFragment()
            newFragment.show(supportFragmentManager, "datePicker")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()
        runBlocking {
            onBackOrUpdateData()
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
        runBlocking {
            when(item.itemId) {
                R.id.menu_taskInsert -> insertTask()
                R.id.menu_taskUpdate -> updateTask()
                R.id.menu_taskDelete -> deleteTask()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 关闭该界面时，需要执行的操作
     * 修改，实时更新当前的数据
     * 创建，检查标题或内容是否为空，都为空的话则无需保存数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun onBackOrUpdateData() {
        when (mMode) {
            MODE_CREATE -> {
                insertTask()
            }
            MODE_EXAMINE -> {
                updateTask()
            }
        }
    }

    /**
     * 使用输入的数据更新视图
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(data: Task) {
        mState = data.state
        mTaskState.state = data.state
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
            mState,
            mTaskTitle.text.toString(),
            mTaskContent.text.toString()
        )
    }

    /**
     * 插入新数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun insertTask() {
        if (mTaskId != 0 && mMode == MODE_EXAMINE) {
            Toast.makeText(this, "当前模式不对劲！", Toast.LENGTH_SHORT)
                .show()
        } else {
            TaskApi.insert(getUiData())
            Toast.makeText(this@TaskContentActivity, "插入数据！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * 更新当前数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateTask() {
        if (mTaskId != 0 && mMode == MODE_EXAMINE) {
            TaskApi.update(getUiData())
            Toast.makeText(this@TaskContentActivity, "更新数据！", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "当前模式不对劲！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * 删除当前数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun deleteTask() {
        if (mTaskId != 0 && mMode == MODE_EXAMINE) {
            TaskApi.delete(getUiData())
            Toast.makeText(this@TaskContentActivity, "删除数据！", Toast.LENGTH_SHORT)
                .show()
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