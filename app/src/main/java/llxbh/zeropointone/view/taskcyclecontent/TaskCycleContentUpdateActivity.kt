package llxbh.zeropointone.view.taskcyclecontent

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.api.TaskApi
import llxbh.zeropointone.api.TaskCycleApi

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
                getBinding().taskCycle = TaskCycleApi.get(taskId)
                sCheckAdapter.submitList(getBinding().taskCycle!!.checks ?: arrayListOf())
                sMarkdownProcessor.onMarkdownToHtmlView(
                    getBinding().taskCycle?.content ?: "",
                    getBinding().wvTaskContent
                )
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