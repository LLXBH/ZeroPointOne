package llxbh.zeropointone

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import llxbh.zeropointone.app.appContext
import llxbh.zeropointone.dao.AppDatabase
import llxbh.zeropointone.dao.Task
import llxbh.zeropointone.dao.TaskDao
import java.time.LocalDate
import java.util.Date

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
    suspend fun getAll(): List<Task> {
        return withContext(Dispatchers.IO) {
            sTaskDao.getAll()
        }
    }
    suspend fun getAllAndTimeOrder(): List<Task> {
        return withContext(Dispatchers.IO) {
            sTaskDao.getAllAndTimeOrder()
        }
    }
    suspend fun getAllAndStateOrder(): List<Task> {
        return withContext(Dispatchers.IO) {
            sTaskDao.getAllAndStateOrder()
        }
    }

    /**
     * 根据 id 去找对于的任务
     */
    suspend fun get(taskId: Int): Task? {
        return withContext(Dispatchers.IO) {
            sTaskDao.get(taskId)
        }
    }

    /**
     * 插入新的数据
     */
    suspend fun insert(task: Task) {
        return withContext(Dispatchers.IO) {
            sTaskDao.insert(task)
        }
    }

    /**
     * 更新数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun update(task: Task) {
        return withContext(Dispatchers.IO) {
            sTaskDao.update(task)
            // 任务完成且自增日期不为 0 ，需要创建个相同的任务（日期不同）
            if (task.state && task.date != null && task.dateAddDay != 0) {
                val newTask = Task(
                    state = false,
                    title = task.title,
                    content = task.content,
                    date = TimeTools.onDateOnAddDay(task.date, task.dateAddDay),
                    dateAddDay = task.dateAddDay
                )
                insert(newTask)
            }
        }
    }

    /**
     * 删除数据
     */
    suspend fun delete(task: Task) {
        return withContext(Dispatchers.IO) {
            sTaskDao.delete(task)
        }
    }

}