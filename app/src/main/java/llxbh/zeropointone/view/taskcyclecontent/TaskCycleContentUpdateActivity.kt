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
import llxbh.zeropointone.api.TaskCycleApi
import llxbh.zeropointone.base.BindingBaseActivity
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.databinding.ActivityTaskCycleContentBinding
import llxbh.zeropointone.util.TimeUtil
import llxbh.zeropointone.view.taskcontent.DatePickerDialogFragment
import llxbh.zeropointone.view.taskcontent.TaskContentCheckAdapter
import llxbh.zeropointone.view.taskcontent.TaskContentCreateActivity
import llxbh.zeropointone.view.taskcontent.TaskContentUpdateActivity
import java.time.LocalDate

/**
 * 清单任务的详细界面
 */
open class TaskCycleContentUpdateActivity: TaskCycleContentCreateActivity() {

    companion object {

        fun start(activity: Activity, taskId: Int) {
            val intent = Intent(
                activity,
                TaskCycleContentUpdateActivity::class.java
            )
            intent.putExtra(
                TaskApi.TASK_PASS,
                taskId
            )
            activity.startActivity(intent)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskId = intent.getIntExtra(TaskApi.TASK_PASS, 0)
        if (taskId != 0) {
            runBlocking {
                getBinding().also { binding ->
                    binding.taskCycle = TaskCycleApi.get(taskId)
                    sCheckAdapter.submitList(binding.taskCycle!!.checks ?: arrayListOf())
                    binding.wvTaskContent.loadData(
                        sMarkdownProcessor.markdownToHtml(binding.taskCycle?.content ?: ""),
                        "text/html; charset=UTF-8",
                        null
                    )
                }
            }
        } else {
            runBlocking {
                Thread.sleep(1000)
                Toast.makeText(this@TaskCycleContentUpdateActivity, "无法获取到信息！", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun onSaveTask() {
        val data = getUiData()
        if (data != null) {
            TaskCycleApi.update(data)
            Toast.makeText(this, "更新数据！", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "无法获取数据！", Toast.LENGTH_SHORT)
                .show()
        }
    }



}