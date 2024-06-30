package llxbh.zeropointone.view.taskcontent

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.coroutines.runBlocking
import llxbh.zeropointone.api.TaskApi
import llxbh.zeropointone.util.TextUtil

class TaskContentUpdateActivity: TaskContentCreateActivity() {

    companion object {

        fun start(taskId: Int, activity: Activity) {
            val intent = Intent(
                activity,
                TaskContentUpdateActivity::class.java
            )
            intent.putExtra(
                TaskApi.TASK_PASS,
                taskId
            )
            // 通知内容界面，带个数据回来
            activity.startActivity(intent)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskId = intent.getIntExtra(TaskApi.TASK_PASS, 0)
        if (taskId != 0) {
            runBlocking {
                getBinding().task = TaskApi.get(taskId)
                sCheckAdapter.submitList(getBinding().task!!.checks ?: arrayListOf())
                getBinding().wvTaskContent.loadData(
                    sMarkdownProcessor.onMarkdownToHtml(getBinding().task?.content ?: ""),
                    "text/html; charset=UTF-8",
                    null
                )
                sMarkdownProcessor.onMarkdownToHtmlView(
                    getBinding().task?.content ?: "",
                    getBinding().wvTaskContent
                )
            }
        } else {
            runBlocking {
                Thread.sleep(1000)
                Toast.makeText(this@TaskContentUpdateActivity, "无法获取到信息！", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun onSaveTask() {
        TaskApi.update(getUiData())
        Toast.makeText(this, "更新数据！", Toast.LENGTH_SHORT)
            .show()
    }

}