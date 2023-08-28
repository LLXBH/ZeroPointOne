package llxbh.zeropointone

import llxbh.zeropointone.dao.Task
import llxbh.zeropointone.dao.TaskDao

/**
 * 有关清单的各种接口执行
 */
object TaskApi {

    const val TASK_PASS = "TASK_PASS"

    private lateinit var sTaskDao: TaskDao

    fun setTaskData(taskDao: TaskDao) {
        sTaskDao = taskDao
    }

    /**
     * 获取当前全部的清单数据
     */
    fun getAll(): List<Task> = sTaskDao.getAll()

}