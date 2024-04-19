package llxbh.zeropointone.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.data.repository.AppDatabase
import llxbh.zeropointone.util.TimeUtil

object TaskCycleApi {

    private val sDB = AppDatabase.appDatabase
    private val sTaskCycleDao = sDB.taskCycleDao()

    /**
     * 获得指定的清单
     */
    suspend fun get(taskId: Int): TaskCycle {
        return  withContext(Dispatchers.IO) {
            sTaskCycleDao.get(taskId)
        }
    }

    /**
     * 获取全部清单
     */
    suspend fun getAll(): List<TaskCycle> {
        return withContext(Dispatchers.IO) {
            sTaskCycleDao.getAll()
        }
    }

    /**
     * 插入数据
     */
    suspend fun insert(taskCycle: TaskCycle) {
        return withContext(Dispatchers.IO) {
            if (! onInspectData(taskCycle)) {
                Log.e("TaskCycle", "数据检查不通过。")
            }
            taskCycle.updateTimes = TimeUtil.getNowTime()
            sTaskCycleDao.insert(taskCycle)
        }
    }

    /**
     * 更新数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun update(taskCycle: TaskCycle) {
        return withContext(Dispatchers.IO) {
            if (! onInspectData(taskCycle)) {
                Log.e("TaskCycle", "数据检查不通过。")
            }
            taskCycle.updateTimes = TimeUtil.getNowTime()
            // 检查是否需要循环创建清单
            onCirculateAddNewTask(taskCycle)?.also {
                update(it)
            }
            // 更新数据
            sTaskCycleDao.update(taskCycle)
        }
    }

    /**
     * 删除数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun delete(taskCycle: TaskCycle) {
        return withContext(Dispatchers.IO) {
            taskCycle.updateTimes = TimeUtil.getNowTime()
            // 不是实际删除，打上标记，进入回收站
            // sTaskDao.delete(task)
            taskCycle.isDelete = true
            update(taskCycle)
        }
    }

    /**
     * 恢复数据
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun restore(taskCycle: TaskCycle) {
        return withContext(Dispatchers.IO) {
            taskCycle.updateTimes = TimeUtil.getNowTime()
            taskCycle.isDelete = false
            update(taskCycle)
        }
    }

    /**
     * 判断是否需要创建新的清单
     */
    private suspend fun onCirculateAddNewTask(taskCycle: TaskCycle): TaskCycle? {
        when {
            (! taskCycle.state) -> {
                // 任务未完成
                return null
            }
            (taskCycle.startTimes == 0L || taskCycle.endTimes == 0L || taskCycle.addTimeDay == 0) -> {
                // 时间未设置全面
                return null
            }
            (get(taskCycle.id).state) -> {
                // 在数据库中原数据已经是完成的了
                return null
            }
            else -> {
                // 因为是循环周期的任务，只需要更改时间即可
                taskCycle.apply {
                    state = false
                    updateTimes = TimeUtil.getNowTime()
                    // 向前推进日期
                    startTimes = TimeUtil.getNewTime(startTimes, addTimeDay)
                    endTimes = TimeUtil.getNewTime(endTimes, addTimeDay)
                    // 将子项设置为未完成的状态
                    if (checks.isNotEmpty()) {
                        for (check in checks) {
                            check.state.set(false)
                        }
                    }
                }
                return taskCycle
            }
        }
    }

    /**
     * 检查数据是否有不规范的dif
     */
    private fun onInspectData(task: TaskCycle): Boolean {
        // 标题和内容不能都为空
        try {
            if (task.title.isEmpty() && task.content.isEmpty()) {
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
    private fun onInspectDataTime(task: TaskCycle) {
        if (task.addTimeDay != 0 && task.startTimes == 0L && task.endTimes == 0L) {
            // 若设置了自增天数，开始时间和结束时间都不能为 0 ；
            task.addTimeDay = 0
        } else if (task.startTimes != 0L && task.endTimes == 0L) {
            // 开始时间和结束时间不能一边不为 0 ，一边为 0 ，如果设置同一天，则开始和结束需要相同
            task.endTimes = task.startTimes
        } else if (task.startTimes == 0L && task.endTimes != 0L) {
            task.startTimes = task.endTimes
        }
    }
}