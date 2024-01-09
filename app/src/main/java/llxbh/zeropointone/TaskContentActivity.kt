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
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.dragswipe.QuickDragAndSwipe
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.base.BaseActivity
import llxbh.zeropointone.dao.Task
import llxbh.zeropointone.data.TaskCheck
import llxbh.zeropointone.tools.TaskApi
import llxbh.zeropointone.tools.TimeTools
import java.lang.Exception
import java.time.LocalDate
import java.util.Date

/**
 * 清单任务的详细界面
 */
class TaskContentActivity: BaseActivity() {

    companion object {
        const val MODE_CREATE = "Task_Create"     // 创建
        const val MODE_EXAMINE = "Task_Examine"   // 查看
    }

    private var mMode = MODE_EXAMINE
    private var mTaskId = 0
    private var mState = false
    private var mSelectDate: Date? = null
    private var mIsDelete = false
    private var sCheckAdapter = TaskContentCheckAdapter()
    private val sQuickDragAndSwipe = QuickDragAndSwipe()

    private lateinit var mTaskState: CheckBox
    private lateinit var mTaskTitle: EditText
    private lateinit var mTaskContent: EditText
    private lateinit var mTaskDate: TextView
    private lateinit var mTaskNextDate: EditText
    private lateinit var mTaskCheckList: RecyclerView
    private lateinit var mTaskCheckAdd: MaterialButton
    private lateinit var mTaskContentSwitchCheck: MaterialButton

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
        mTaskState.setOnClickListener {
            mState = !mState
            mTaskState.isChecked = mState
        }

        mTaskTitle = findViewById(R.id.et_taskTitle)
        mTaskContent = findViewById(R.id.et_taskContent)
        mTaskDate = findViewById(R.id.tv_taskDate)
        mTaskNextDate = findViewById(R.id.et_taskNextDate)
        // ----- 子项列表（开始） -----
        mTaskCheckList = findViewById(R.id.rv_taskCheckList)
        mTaskCheckList.apply {
            layoutManager = LinearLayoutManager(this@TaskContentActivity)
            adapter = sCheckAdapter
        }
        // 适配器
        sCheckAdapter.apply {
            // 点击删除子项
            addOnItemChildClickListener(R.id.btn_taskContentCheckDelete) { adapter, view, position ->
                adapter.remove(adapter.items[position])
            }
            // 长按拖拽排序
            addOnItemChildLongClickListener(R.id.btn_taskContentCheckSliders) { adapter, view, position ->
                sQuickDragAndSwipe.startDrag(position)
                false
            }
        }
        // 拖拽功能
        sQuickDragAndSwipe.apply {
            // 绑定 recycleView 和 适配器
            attachToRecyclerView(mTaskCheckList)
            setDataCallback(sCheckAdapter)
            // 设置可以上下拖动
            setDragMoveFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
            // 关闭自带长按拖动功能
            isLongPressDragEnabled = false
        }
        // ----- 子项列表（结束） -----
        // 添加子项
        mTaskCheckAdd = findViewById(R.id.btn_taskCheckAdd)
        mTaskCheckAdd.setOnClickListener {
            sCheckAdapter.add(TaskCheck())
        }
        // 将内容转换为 Check
        mTaskContentSwitchCheck = findViewById(R.id.btn_taskContentSwitchCheck)
        mTaskContentSwitchCheck.setOnClickListener {
            val isContentCheck = sCheckAdapter.items.isNullOrEmpty()
            if (isContentCheck) {
                // 为空，将内容转换为子项
                val content = mTaskContent.text.toString()
                // 内容为空就只创建个空的子项
                if (content.isNullOrEmpty()) {
                    sCheckAdapter.add(TaskCheck())
                    return@setOnClickListener
                }
                val contentList = content.split("\n")
                val checkList = arrayListOf<TaskCheck>()
                for (newCheck in contentList) {
                    checkList.add(TaskCheck(
                        ObservableField(false),
                        ObservableField(newCheck)
                    ))
                }
                sCheckAdapter.addAll(checkList)
                mTaskContent.setText("")
            } else {
                // 有了，将子项转换为内容
                var newContentList = ""
                for ((index, newContent) in sCheckAdapter.items.withIndex()) {
                    newContentList += if (index == 0) {
                        "${newContent.content.get()}"
                    } else {
                        "\n${newContent.content.get()}"
                    }
                }
                var content = mTaskContent.text.toString()
                if (! content.isNullOrEmpty()) {
                    content += "\n"
                }
                content += newContentList
                mTaskContent.setText(content)
                sCheckAdapter.submitList(arrayListOf())
            }
        }

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
            DatePickerDialogFragment().show(supportFragmentManager, "datePicker")
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onStop() {
//        super.onStop()
//        runBlocking {
//            onBackOrUpdateData()
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
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
                R.id.menu_taskRestore -> restoreTask()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 关闭该界面时，需要执行的操作
     * 创建，检查标题或内容是否为空，都为空的话则无需保存数据
     * 修改，实时更新当前的数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun onBackOrUpdateData() {
        when (mMode) {
            // 创建
            MODE_CREATE -> {
                val task = getUiData()
                if (task.title.isNullOrEmpty() && task.content.isNullOrEmpty()) {
                    return
                } else if (!task.title.isNullOrEmpty()) {
                    insertTask()
                } else {
                    mTaskTitle.text = mTaskContent.text
                    mTaskContent.setText("")
                    insertTask()
                }
            }
            // 修改
            MODE_EXAMINE -> {
                updateTask()
            }
        }
    }

    /**
     * 使用输入的数据更新视图
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(task: Task) {
        mState = task.state
        mTaskState.isChecked = task.state
        mTaskTitle.setText(task.title)
        mTaskContent.setText(task.content)
        mTaskDate.text = task.startTimes.let {
            if (it != 0L) {
                TimeTools.timesToString(it)
            } else {
                ""
            }
        }
        mTaskNextDate.setText(task.addTimeDay.toString())
        sCheckAdapter.submitList(task.checks ?: arrayListOf(TaskCheck()))
        mIsDelete = task.isDelete
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
            mTaskContent.text.toString(),
            sCheckAdapter.items,
            TimeTools.getNowTime(),
            TimeTools.stringToTimes(mTaskDate.text.toString()) ?: 0L,
            0L,
            try {
              mTaskNextDate.text.toString().toInt()
            } catch (e: Exception) {
              0
            },
            mIsDelete
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
            mIsDelete = true
            TaskApi.delete(getUiData())
            Toast.makeText(this@TaskContentActivity, "删除数据！", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "当前模式不对劲！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun restoreTask() {
        if (mTaskId != 0 && mMode == MODE_EXAMINE) {
            mIsDelete = false
            TaskApi.restore(getUiData())
            Toast.makeText(this@TaskContentActivity, "恢复数据！", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "当前模式不对劲！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDate(year: Int, month: Int, day: Int) {
        val time = LocalDate.of(year, month, day)
        mTaskDate.text = time.toString()
        mSelectDate = TimeTools.localDateToDate(time)
    }

}