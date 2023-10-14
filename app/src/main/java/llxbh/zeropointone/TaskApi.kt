package llxbh.zeropointone

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.room.util.copy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import llxbh.zeropointone.app.appContext
import llxbh.zeropointone.dao.AppDatabase
import llxbh.zeropointone.dao.Task
import llxbh.zeropointone.dao.TaskDao
import java.time.LocalDate
import java.util.Date
import kotlin.jvm.internal.Intrinsics.Kotlin

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
                .sortedWith(compareBy(
                    // 先按任务的状态区分开（未完成在前，已完成在后）
                    { it.state },
                    // 未完成的任务，按开始时间来排序（升序）
                    { if (!it.state) it.startTimes else -1},
                    // 已完成的任务，按更新的时间排序（降序）
                    { if (it.state) -it.updateTimes else 0}
                ))
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
            if (! onInspectData(task)) {
                Log.e("Task", "数据检查不通过。")
            }
            task.updateTimes = TimeTools.getNowTime()
            sTaskDao.insert(task)
        }
    }

    /**
     * 更新数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun update(task: Task) {
        return withContext(Dispatchers.IO) {
            if (! onInspectData(task)) {
                Log.e("Task", "数据检查不通过。")
            }
            task.updateTimes = TimeTools.getNowTime()
            sTaskDao.update(task)
            // 检查是否需要循环创建清单
            onCirculateAddNewTask(task)?.also {
                insert(it)
            }
        }
    }

    /**
     * 判断是否需要创建新的清单
     */
    private fun onCirculateAddNewTask(task: Task): Task? {
        if (task.state
            && task.startTimes != 0L
            && task.endTimes != 0L
            && task.addTimeDay != 0)
        {
            return task.copy()
                .apply {
                    id = 0
                    state = false
                    updateTimes = TimeTools.getNowTime()
                    startTimes = TimeTools.getNewTime(startTimes, addTimeDay)
                    endTimes = TimeTools.getNewTime(endTimes, addTimeDay)
                }
        } else {
            return null
        }
    }

    /**
     * 删除数据
     */
    suspend fun delete(task: Task) {
        return withContext(Dispatchers.IO) {
            task.updateTimes = TimeTools.getNowTime()
            sTaskDao.delete(task)
        }
    }

    /**
     * 检查数据是否有不规范的dif
     */
    private fun onInspectData(task: Task): Boolean {
        // 标题和内容不能都为空
        try {
            if (task.title.isNullOrEmpty() && task.content.isNullOrEmpty()) {
                throw Exception("清单：标题和内容不能都为空。")
            }
            if (task.addTimeDay != 0 && task.startTimes == 0L && task.endTimes == 0L) {
                throw Exception("清单：若设置了自增天数，开始时间和结束时间都不为 0 。")
            }
        } catch (e: Exception) {
            Log.e("Task", e.message.toString())
            return false
        }
        onInspectDataTime(task)
        return true
    }

    /**
     * 检查且修正有关于时间的设置
     */
    private fun onInspectDataTime(task: Task) {
        if (task.addTimeDay != 0 && task.startTimes == 0L && task.endTimes == 0L) {
            // 若设置了自增天数，开始时间和结束时间都不为 0 ；
            task.addTimeDay == 0
        } else if (task.startTimes != 0L && task.endTimes == 0L) {
            // 开始时间和结束时间不能一边不为 0 ，一边为 0 ，如果设置同一天，则开始和结束需要相同
            task.endTimes = task.startTimes
        } else if (task.startTimes == 0L && task.endTimes != 0L) {
            task.startTimes = task.endTimes
        }
    }

}