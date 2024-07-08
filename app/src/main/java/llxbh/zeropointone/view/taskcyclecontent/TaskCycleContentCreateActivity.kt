package llxbh.zeropointone.view.taskcyclecontent

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import com.chad.library.adapter4.dragswipe.QuickDragAndSwipe
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.R
import llxbh.zeropointone.data.model.TaskCheck
import llxbh.zeropointone.util.TextUtil
import llxbh.zeropointone.api.TaskCycleApi
import llxbh.zeropointone.base.BindingBaseActivity
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.databinding.ActivityTaskCycleContentBinding
import llxbh.zeropointone.util.TaskCheckUtil
import llxbh.zeropointone.util.time.DatePickInterface
import llxbh.zeropointone.util.time.TimeUtil
import llxbh.zeropointone.view.taskcontent.TaskContentCheckAdapter
import java.time.LocalDate

/**
 * 清单任务的详细界面
 */
open class TaskCycleContentCreateActivity: BindingBaseActivity<ActivityTaskCycleContentBinding>() {

    var sCheckAdapter = TaskContentCheckAdapter()
    private val sQuickDragAndSwipe = QuickDragAndSwipe()

    protected val sMarkdownProcessor = TextUtil()

    private val sTaskCheckUtil = TaskCheckUtil()

    override fun setBinding(): ActivityTaskCycleContentBinding {
        return ActivityTaskCycleContentBinding.inflate(layoutInflater)
    }

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(
                activity,
                TaskCycleContentCreateActivity::class.java
            )
            activity.startActivity(intent)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getBinding().also { binding ->
            // 绑定数据
            binding.taskCycle = TaskCycle(title = "")
            binding.timeTools = TimeUtil
            binding.viewEdit = false

            // 时间选择
            binding.tvTaskDateStart.apply {
                // 点击时间则展示显示日期选择
                setOnClickListener {
                    onSetData(binding.tvTaskDateStart)
                }
                // 长按时间清空已选
                setOnLongClickListener {
                    text = ""
                    Toast.makeText(this@TaskCycleContentCreateActivity, "清空已选时间！", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnLongClickListener true
                }
            }
            binding.tvTaskDateEnd.apply {
                // 点击时间则展示显示日期选择
                setOnClickListener {
                    onSetData(binding.tvTaskDateEnd)
                }
                // 长按时间清空已选
                setOnLongClickListener {
                    text = ""
                    Toast.makeText(this@TaskCycleContentCreateActivity, "清空已选时间！", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnLongClickListener true
                }
            }

            // 内容编辑（或显示）
            binding.btnTaskContentChange.setOnClickListener {
                // 判断是否是编辑模式
                if (binding.viewEdit != false) {
                    // 显示模式，将编辑的内容转换且显示
                    sMarkdownProcessor.onMarkdownToHtmlView(
                        binding.taskCycle!!.content,
                        getBinding().wvTaskContent
                    )

                }
                binding.viewEdit = !binding.viewEdit!!
            }

            // 列表适配
            binding.rvTaskCheckList.adapter = sCheckAdapter.also { checkAdapter ->
                // 点击删除子项
                checkAdapter.addOnItemChildClickListener(R.id.btn_taskContentCheckDelete) { adapter, view, position ->
                    adapter.remove(adapter.items[position])
                }
                // 长按拖拽排序
                checkAdapter.addOnItemChildLongClickListener(R.id.btn_taskContentCheckSliders) { adapter, view, position ->
                    sQuickDragAndSwipe.startDrag(position)
                    false
                }
            }

            // 添加子项任务
            binding.btnTaskCheckAdd.setOnClickListener {
                sCheckAdapter.add(TaskCheck())
            }

            // 内容 与 Check 相互转换
            binding.btnTaskContentSwitchCheck.setOnClickListener {
                sTaskCheckUtil.onSwitchStrongOrCheck(
                    getBinding().etTaskContent,
                    sCheckAdapter,
                    getBinding().wvTaskContent
                )
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

    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onDestroy() {
//        super.onDestroy()
//        runBlocking {
//            onBackOrUpdateData()
//        }
//    }

    /**
     * 绑定并且显示 Menu 菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_cycle_content_fragment_menu, menu)
        return true     // 显示
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        runBlocking {
            when(item.itemId) {
                R.id.menu_taskSave -> {
                    onSaveTask()
                }
                R.id.menu_taskDelete -> {
                    onDeleteTask()
                }
                R.id.menu_taskRestore -> {
                    onRestoreTask()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    /**
//     * 关闭该界面时，需要执行的操作
//     * 创建，检查标题或内容是否为空，都为空的话则无需保存数据
//     * 修改，实时更新当前的数据
//     */
//    @RequiresApi(Build.VERSION_CODES.O)
//    private suspend fun onBackOrUpdateData() {
//        val task = getUiData()
//        if (task.title.isEmpty() && task.content.isEmpty()) {
//            return
//        } else if (task.title.isNotEmpty()) {
//            onSaveTask()
//        } else {
//            mBinding.etTaskTitle.text = mBinding.etTaskContent.text
//            mBinding.etTaskContent.setText("")
//            onSaveTask()
//        }
//    }

    /**
     * 获取当前显示的 UI 数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getUiData(): TaskCycle? {
        val task = getBinding().taskCycle!!
        getBinding().apply {
            task.state = cbTaskState.isChecked
            task.addTimeDay = etTaskNextDate.text?.toString()?.toInt() ?: 0
            task.startTimes = TimeUtil.stringToTimes(tvTaskDateStart.text.toString()) ?: 0L
            task.endTimes = TimeUtil.stringToTimes(tvTaskDateEnd.text.toString())?.let {
                // 转换为 "当天" 的最后一刻
                val endTime = TimeUtil.getNewTime(it, 1)
                endTime - 1
            } ?: 0L
            task.needCompleteNum = etTaskNeedCompleteNum.text?.toString()?.toInt() ?: 0
            task.checks = sCheckAdapter.items
        }
        return task
    }

    @RequiresApi(Build.VERSION_CODES.O)
    open suspend fun onSaveTask() {
        val data = getUiData()
        if (data != null) {
            TaskCycleApi.insert(data)
            Toast.makeText(this, "插入数据！", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "无法获取数据！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * 删除当前数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun onDeleteTask() {
        val data = getUiData()
        if (data != null) {
            TaskCycleApi.delete(data)
            Toast.makeText(this, "删除数据！", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "无法获取数据！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun onRestoreTask() {
        val data = getUiData()
        if (data != null) {
            TaskCycleApi.restore(data)
            Toast.makeText(this, "恢复数据！", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "无法获取数据！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * 弹出时间选择器，并显示用户点击的时间
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSetData(timeTextView: TextView) {
        TimeUtil.showDatePick(
            this,
            object : DatePickInterface {

                override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
                    val time = LocalDate.of(year, month, dayOfMonth)
                    timeTextView.text = time.toString()
                }

            }
        )
    }

}