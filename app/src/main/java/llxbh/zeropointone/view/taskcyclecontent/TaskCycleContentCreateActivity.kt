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
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.ItemTouchHelper
import com.chad.library.adapter4.dragswipe.QuickDragAndSwipe
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.R
import llxbh.zeropointone.base.BaseActivity
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.data.model.TaskCheck
import llxbh.zeropointone.databinding.ActivityTaskContentBinding
import llxbh.zeropointone.util.TextUtil
import llxbh.zeropointone.api.TaskApi
import llxbh.zeropointone.base.BindingBaseActivity
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.databinding.ActivityTaskCycleContentBinding
import llxbh.zeropointone.util.TimeUtil
import llxbh.zeropointone.view.taskcontent.DatePickerDialogFragment
import llxbh.zeropointone.view.taskcontent.TaskContentCheckAdapter
import llxbh.zeropointone.view.taskcontent.TaskContentUpdateActivity
import java.time.LocalDate

/**
 * 清单任务的详细界面
 */
open class TaskCycleContentCreateActivity: BindingBaseActivity<ActivityTaskCycleContentBinding>() {

    private var sCheckAdapter = TaskContentCheckAdapter()
    private val sQuickDragAndSwipe = QuickDragAndSwipe()

    private val sMarkdownProcessor = TextUtil()

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
                    val text = binding.taskCycle!!.content
                    val textMark = sMarkdownProcessor.markdownToHtml(text)
                    binding.wvTaskContent.loadData(
                        textMark,
                        "text/html; charset=UTF-8",
                        null
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
                // 判断是否有内容
                val ifContent = binding.etTaskContent.text.toString().isNotEmpty()
                // 判断有子项
                val ifCheck = sCheckAdapter.items.isNotEmpty()

                // 根据详情和子项的内容与否，进行不同的操作
                when {
                    // 有详情，也有子项，将详情续接到子项上
                    (ifContent && ifCheck) -> {
                        // 将内容转为 TaskCheck
                        val contentList = binding.etTaskContent.text.toString().split("\n")
                        val checkList = arrayListOf<TaskCheck>()
                        for (newCheck in contentList) {
                            checkList.add(
                                TaskCheck(
                                    ObservableField(false),
                                    ObservableField(newCheck)
                                )
                            )
                        }

                        // 接上
                        sCheckAdapter.addAll(checkList)
                        binding.etTaskContent.setText("")
                    }

                    // 详情为空，子项不为空：将子项转为内容
                    (!ifContent && ifCheck) -> {
                        // 将子项的内容提取出来，每一个子项的后面添加一个回车（换行）
                        var newContent = ""
                        for ((index, check) in sCheckAdapter.items.withIndex()) {
                            // 从第二个开始，在前面加个回车换行
                            newContent += if (index == 0) {
                                "${check.content.get()}"
                            } else {
                                "\n${check.content.get()}"
                            }
                        }

                        // 更新数据，新的详情，空的子项
                        binding.etTaskContent.setText(newContent)
                        sCheckAdapter.submitList(arrayListOf())
                    }

                    // 详情不为空，子项为空，将内容转换为子项
                    (ifContent && !ifCheck) -> {
                        // 将内容转为 TaskCheck
                        val contentList = binding.etTaskContent.text.toString().split("\n")
                        val checkList = arrayListOf<TaskCheck>()
                        for (newCheck in contentList) {
                            checkList.add(
                                TaskCheck(
                                    ObservableField(false),
                                    ObservableField(newCheck)
                                )
                            )
                        }

                        // 更新数据，空的详情，新的子项
                        binding.etTaskContent.setText("")
                        sCheckAdapter.addAll(checkList)
                    }
                }

                // 显示详情的状态，而不是编辑，需要同步刷新一下
                if (binding.viewEdit == false) {
                    binding.wvTaskContent.loadData(
                        sMarkdownProcessor.markdownToHtml(binding.taskCycle?.content ?: ""),
                        "text/html; charset=UTF-8",
                        null
                    )
                }

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

//    /**
//     * 绑定并且显示 Menu 菜单
//     */
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.task_content_fragment_menu, menu)
//        return true     // 显示
//    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        runBlocking {
//            when(item.itemId) {
//                R.id.menu_taskSave -> onSaveTask()
//                R.id.menu_taskDelete -> deleteTask()
//                R.id.menu_taskRestore -> restoreTask()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

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

//    /**
//     * 获取当前显示的 UI 数据
//     */
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getUiData(): Task {
//        val task = mBinding.task!!
//        task.state = mBinding.cbTaskState.isChecked
//        if (mBinding.tvTaskDate.text.isNotEmpty()) {
//            task.startTimes = TimeUtil.stringToTimes(mBinding.tvTaskDate.text.toString()) ?: 0L
//        } else {
//            task.startTimes = 0L
//            task.endTimes = 0L
//        }
//        if (mBinding.etTaskNextDate.text.isNotEmpty()) {
//            task.addTimeDay = mBinding.etTaskNextDate.text.toString().toInt()
//        }
//        task.checks = sCheckAdapter.items
//        return task
//    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    open suspend fun onSaveTask() {
//        TaskApi.insert(getUiData())
//        Toast.makeText(this, "插入数据！", Toast.LENGTH_SHORT)
//            .show()
//    }

//    /**
//     * 删除当前数据
//     */
//    @RequiresApi(Build.VERSION_CODES.O)
//    private suspend fun deleteTask() {
//        TaskApi.delete(getUiData())
//        Toast.makeText(this@TaskCycleContentCreateActivity, "删除数据！", Toast.LENGTH_SHORT)
//            .show()
//    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private suspend fun restoreTask() {
//        TaskApi.restore(getUiData())
//        Toast.makeText(this@TaskCycleContentCreateActivity, "恢复数据！", Toast.LENGTH_SHORT)
//            .show()
//    }

    /**
     * 弹出时间选择器，并显示用户点击的时间
     */
    private fun onSetData(timeTextView: TextView) {
        // 当前日期
        val calendar = Calendar.getInstance()

        // 时间选择器
        DatePickerDialog(
            this,
            object : DatePickerDialog.OnDateSetListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
                    val time = LocalDate.of(year, month, day)
                    timeTextView.text = time.toString()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

}