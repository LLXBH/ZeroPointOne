package llxbh.zeropointone

/**
 * 有关清单的各种接口执行
 */
object TaskApi {

    const val TASK_PASS = "TASK_PASS"

    /**
     * 获取当前全部的清单数据
     */
    fun getTaskList(): List<TaskData> {
        return listOf(
            TaskData(false, "数据1"),
            TaskData(false, "数据2"),
            TaskData(false, "数据3")
        )
    }

}