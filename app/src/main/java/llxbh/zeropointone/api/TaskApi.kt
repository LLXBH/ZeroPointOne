package llxbh.zeropointone.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import llxbh.zeropointone.data.repository.AppDatabase
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.util.time.TimeUtil

/**
 * 有关清单的各种接口执行
 */
object TaskApi {

    const val TASK_PASS = "TASK_PASS"

    private val sDB = AppDatabase.appDatabase
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
     * 已经删除的数据
     */
    suspend fun getRecycleBin(): List<Task> {
        return  withContext(Dispatchers.IO) {
            sTaskDao.getRecycleBin()
        }
    }

    /**
     * 插入新的数据
     */
    suspend fun insert(task: Task) {
        return withContext(Dispatchers.IO) {
            onInspectData(task)?.also {
                it.updateTimes = TimeUtil.getNowTime()
                sTaskDao.insert(it)
            }
        }
    }

    /**
     * 更新数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun update(task: Task) {
        return withContext(Dispatchers.IO) {
            onInspectData(task)?.also {
                it.updateTimes = TimeUtil.getNowTime()

                // 检查是否需要循环创建清单
                onCirculateAddNewTask(it)?.also { newData ->
                    insert(newData)
                }

                // 更新数据
                sTaskDao.update(it)
            }
        }
    }

    /**
     * 判断是否需要创建新的清单
     */
    private fun onCirculateAddNewTask(task: Task): Task? {
        return if (task.onLoopInspect()) {
            task.onLoopNewData()
        } else {
            null
        }
    }

    /**
     * 删除数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun delete(task: Task) {
        return withContext(Dispatchers.IO) {
            task.updateTimes = TimeUtil.getNowTime()
            // 不是实际删除，打上标记，进入回收站
            // sTaskDao.delete(task)
            task.isDelete = true
            update(task)
        }
    }

    /**
     * 恢复数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun restore(task: Task) {
        return withContext(Dispatchers.IO) {
            task.updateTimes = TimeUtil.getNowTime()
            task.isDelete = false
            update(task)
        }
    }

    /**
     * 检查数据是否有不规范的地方，且尝试修正
     */
    private fun onInspectData(task: Task): Task? {
        return if (! task.onInspect()) {
            task.onCorrectContent()
                ?.onCorrectDateTime()
        } else {
            task
        }
    }

}