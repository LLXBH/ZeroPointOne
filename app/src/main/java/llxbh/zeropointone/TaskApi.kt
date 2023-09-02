package llxbh.zeropointone

import androidx.room.Room
import llxbh.zeropointone.app.appContext
import llxbh.zeropointone.dao.AppDatabase
import llxbh.zeropointone.dao.Task
import llxbh.zeropointone.dao.TaskDao

/**
 * 有关清单的各种接口执行
 */
object TaskApi {

    const val DATA_BASE = "database-ZeroPointOne"
    const val TASK_PASS = "TASK_PASS"

    private val sDB = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        DATA_BASE
    ).build()
    private val sTaskDao = sDB.taskDao()

    /**
     * 获取当前全部的清单数据
     */
    fun getAll(): List<Task> = sTaskDao.getAll()

}