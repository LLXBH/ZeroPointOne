package llxbh.zeropointone.util

import android.webkit.WebView
import android.widget.TextView
import androidx.databinding.ObservableField
import llxbh.zeropointone.data.model.TaskCheck
import llxbh.zeropointone.view.taskcontent.TaskContentCheckAdapter

class TaskCheckUtil {

    private val sMarkdownProcessor = TextUtil()

    /**
     * 将 TaskCheck 转换为 文本
     *
     * @param checks 列表
     *
     * @return 文本
     */
    fun checkToString(checks: List<TaskCheck>): String {
        var resultText = ""

        // 除第一个数据外，其他数据后面空一行
        resultText += checks[0].content.get()
        for (index in 1 until checks.size) {
            resultText += "\n\n${checks[index].content.get()}"
        }

        return resultText
    }

    /**
     * 将 文本 转换为 TaskCheck
     *
     * @param text 要转换的文本
     *
     * @return Check 列表
     */
    fun stringToCheck(text: String): List<TaskCheck> {
        // 每一行作为一个 Check
        val checkTextList = text.split("\n")
        val resultChecks = arrayListOf<TaskCheck>()

        for (checkText in checkTextList) {
            // 空白数据就无需操作了
            if (checkText.isEmpty()) {
                continue
            }

            resultChecks.add(
                TaskCheck(
                    ObservableField(false),
                    ObservableField(checkText)
                )
            )
        }

        return resultChecks
    }

    /**
     * “文本” 与 "子项任务" 的相互转换
     *
     * 逻辑内部以定义，负责调用就行，自动判断如何转换
     *
     * @param textView 文本控件
     * @param checkAdapter 文本列表适配器
     * @param webView 浏览器
     *
     */
    fun onSwitchStrongOrCheck(textView: TextView, checkAdapter: TaskContentCheckAdapter, webView: WebView?) {
        // 判断当前状态
        val haveContent = textView.text.toString().isNotEmpty()
        val haveCheck = checkAdapter.items.isNotEmpty()

        // 根据详情和子项的内容与否，进行不同的操作
        when {
            // 有详情，也有子项，将详情续接到子项上
            (haveContent && haveCheck) -> {
                // 将内容转为 TaskCheck
                val checkListString = textView.text.toString()
                val newCheck = this.stringToCheck(checkListString)

                // 接上
                checkAdapter.addAll(newCheck)
                textView.setText("")
            }

            // 详情为空，子项不为空：将子项转为内容
            (!haveContent && haveCheck) -> {
                // 将子项的内容提取出来
                val newContent = this.checkToString(checkAdapter.items)

                // 更新数据，新的详情，空的子项
                textView.setText(newContent)
                checkAdapter.submitList(arrayListOf())
            }

            // 详情不为空，子项为空，将内容转换为子项
            (haveContent && !haveCheck) -> {
                // 将内容转为 TaskCheck
                val checkListString = textView.text.toString()
                val newCheck = this.stringToCheck(checkListString)

                // 更新数据，空的详情，追加子项任务
                textView.setText("")
                checkAdapter.addAll(newCheck)
            }
        }

        // 显示详情的状态，而不是编辑，需要同步刷新一下
        webView?.loadData(
            sMarkdownProcessor.onMarkdownToHtml(textView.text.toString() ?: ""),
            "text/html; charset=UTF-8",
            null
        )

    }

}