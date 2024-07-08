package llxbh.zeropointone.view.taskcontent

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import com.chad.library.adapter4.dragswipe.QuickDragAndSwipe
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.R
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.data.model.TaskCheck
import llxbh.zeropointone.databinding.ActivityTaskContentBinding
import llxbh.zeropointone.util.TextUtil
import llxbh.zeropointone.api.TaskApi
import llxbh.zeropointone.base.BindingBaseActivity
import llxbh.zeropointone.util.TaskCheckUtil
import llxbh.zeropointone.util.time.DatePickInterface
import llxbh.zeropointone.util.time.TimeUtil
import java.time.LocalDate

/**
 * 清单任务的详细界面
 */
open class TaskContentCreateActivity: BindingBaseActivity<ActivityTaskContentBinding>() {

    var sCheckAdapter = TaskContentCheckAdapter()
    private val sQuickDragAndSwipe = QuickDragAndSwipe()
    
    protected val sMarkdownProcessor = TextUtil()

    private val sTaskCheckUtil = TaskCheckUtil()


    override fun setBinding(): ActivityTaskContentBinding {
        return ActivityTaskContentBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 绑定数据
        getBinding().apply {
            task = Task(title = "")
            timeTools = TimeUtil

            rvTaskCheckList.adapter = sCheckAdapter

            viewEdit = false
            btnTaskContentChange.setOnClickListener {
                // 判断是否是编辑模式
                if (viewEdit != false) {
                    sMarkdownProcessor.onMarkdownToHtmlView(
                        etTaskContent.text.toString(),
                        getBinding().wvTaskContent
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
            attachToRecyclerView(getBinding().rvTaskCheckList)
            setDataCallback(sCheckAdapter)
            // 设置可以上下拖动
            setDragMoveFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
            // 关闭自带长按拖动功能
            isLongPressDragEnabled = false
        }

        // 添加子项
        getBinding().btnTaskCheckAdd.setOnClickListener {
            sCheckAdapter.add(TaskCheck())
        }

        // 将内容转换为 Check
        getBinding().btnTaskContentSwitchCheck.setOnClickListener {
            sTaskCheckUtil.onSwitchStrongOrCheck(
                getBinding().etTaskContent,
                sCheckAdapter,
                getBinding().wvTaskContent
            )
        }

        getBinding().tvTaskDate.apply {
            // 点击时间则展示显示日期选择
            setOnClickListener {
                TimeUtil.showDatePick(
                    this@TaskContentCreateActivity,
                    object : DatePickInterface {
                        override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
                            setDate(year, month, dayOfMonth)
                        }
                    }
                )
            }
            // 长按时间清空已选
            setOnLongClickListener {
                text = ""
                Toast.makeText(this@TaskContentCreateActivity, "清空已选时间！", Toast.LENGTH_SHORT)
                    .show()
                return@setOnLongClickListener true
            }
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
        menuInflater.inflate(R.menu.task_content_fragment_menu, menu)
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
            getBinding().etTaskTitle.text = getBinding().etTaskContent.text
            getBinding().etTaskContent.setText("")
            onSaveTask()
        }
    }

    /**
     * 获取当前显示的 UI 数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getUiData(): Task {
        val task = getBinding().task!!
        task.state = getBinding().cbTaskState.isChecked
        if (getBinding().tvTaskDate.text.isNotEmpty()) {
            task.startTimes = TimeUtil.stringToTimes(getBinding().tvTaskDate.text.toString()) ?: 0L
        } else {
            task.startTimes = 0L
            task.endTimes = 0L
        }
        if (getBinding().etTaskNextDate.text.isNotEmpty()) {
            task.addTimeDay = getBinding().etTaskNextDate.text.toString().toInt()
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
        getBinding().tvTaskDate.text = time.toString()
    }

}