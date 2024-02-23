package llxbh.zeropointone

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.ItemTouchHelper
import com.chad.library.adapter4.dragswipe.QuickDragAndSwipe
import com.vladsch.flexmark.util.format.MarkdownParagraph
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.base.BaseActivity
import llxbh.zeropointone.dao.Task
import llxbh.zeropointone.data.TaskCheck
import llxbh.zeropointone.databinding.ActivityTaskContentBinding
import llxbh.zeropointone.tools.MarkdownProcessor
import llxbh.zeropointone.tools.TaskApi
import llxbh.zeropointone.tools.TimeTools
import java.time.LocalDate

/**
 * 清单任务的详细界面
 */
open class TaskContentCreateActivity: BaseActivity() {

    var sCheckAdapter = TaskContentCheckAdapter()
    private val sQuickDragAndSwipe = QuickDragAndSwipe()

    lateinit var mBinding: ActivityTaskContentBinding

    private val sMarkdownProcessor = MarkdownProcessor()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 绑定布局
        mBinding = ActivityTaskContentBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // 绑定数据
        mBinding.apply {
            task = Task(title = "")
            timeTools = TimeTools

            rvTaskCheckList.adapter = sCheckAdapter

            viewEdit = false
            btnTaskContentChange.setOnClickListener {
                // 判断是否是编辑模式
                if (viewEdit != false) {
                    val text = etTaskContent.text.toString()
                    val textMark = sMarkdownProcessor.markdownToHtml(text)
                    wvTaskContent.loadData(
                        textMark,
                        "text/html; charset=UTF-8",
                        null
                    )
                }
                viewEdit = !viewEdit!!
            }
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
            attachToRecyclerView(mBinding.rvTaskCheckList)
            setDataCallback(sCheckAdapter)
            // 设置可以上下拖动
            setDragMoveFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
            // 关闭自带长按拖动功能
            isLongPressDragEnabled = false
        }

        // 添加子项
        mBinding.btnTaskCheckAdd.setOnClickListener {
            sCheckAdapter.add(TaskCheck())
        }

        // 将内容转换为 Check
        mBinding.btnTaskContentSwitchCheck.setOnClickListener {
            val isContentCheck = sCheckAdapter.items.isNullOrEmpty()
            if (isContentCheck) {
                // 为空，将内容转换为子项
                val content = mBinding.etTaskContent.text.toString()
                // 内容为空就只创建个空的子项
                if (content.isEmpty()) {
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
                mBinding.etTaskContent.setText("")
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
                var content = mBinding.etTaskContent.text.toString()
                if (content.isNotEmpty()) {
                    content += "\n"
                }
                mBinding.etTaskContent.setText(content)
                sCheckAdapter.submitList(arrayListOf())
            }
        }

        // 点击时间则展示显示日期选
        mBinding.tvTaskDate.setOnClickListener {
            DatePickerDialogFragment().show(supportFragmentManager, "datePicker")
        }
    }


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
                R.id.menu_taskSave -> onSaveTask()
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
        val task = getUiData()
        if (task.title.isEmpty() && task.content.isEmpty()) {
            return
        } else if (task.title.isNotEmpty()) {
            onSaveTask()
        } else {
            mBinding.etTaskTitle.text = mBinding.etTaskContent.text
            mBinding.etTaskContent.setText("")
            onSaveTask()
        }
    }

    /**
     * 获取当前显示的 UI 数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getUiData(): Task {
        val task = mBinding.task!!
        task.state = mBinding.cbTaskState.isChecked
        if (mBinding.tvTaskDate.text.isNotEmpty()) {
            task.startTimes = TimeTools.stringToTimes(mBinding.tvTaskDate.text.toString()) ?: 0L
        }
        if (mBinding.etTaskNextDate.text.isNotEmpty()) {
            task.addTimeDay = mBinding.etTaskNextDate.text.toString().toInt()
        }
        task.checks = sCheckAdapter.items
        return task
    }

    @RequiresApi(Build.VERSION_CODES.O)
    open suspend fun onSaveTask() {
        TaskApi.insert(getUiData())
        Toast.makeText(this, "插入数据！", Toast.LENGTH_SHORT)
            .show()
    }

    /**
     * 删除当前数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun deleteTask() {
        TaskApi.delete(getUiData())
        Toast.makeText(this@TaskContentCreateActivity, "删除数据！", Toast.LENGTH_SHORT)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun restoreTask() {
        TaskApi.restore(getUiData())
        Toast.makeText(this@TaskContentCreateActivity, "恢复数据！", Toast.LENGTH_SHORT)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDate(year: Int, month: Int, day: Int) {
        val time = LocalDate.of(year, month, day)
        mBinding.tvTaskDate.text = time.toString()
    }

}