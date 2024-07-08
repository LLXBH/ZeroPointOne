package llxbh.zeropointone.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import llxbh.zeropointone.data.model.TaskCycle
import llxbh.zeropointone.data.repository.AppDatabase
import llxbh.zeropointone.util.MassageUtil
import llxbh.zeropointone.util.time.TimeUtil

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
                // 自定义排序，对数据处理一下顺序
                .sortedWith(compareBy(
                    // 按完成的状态排序
                    { it.state },
                    // 未完成的任务，按开始时间来排序（升序）
                    { if (!it.state) {
                        it.startTimes
                    } else {
                        0
                    }},
                    // 已完成的任务，按更新的时间排序（降序）
                    { if (it.state) {
                        -it.updateTimes
                    } else {
                        0
                    }}
                ))
        }
    }

    /**
     * 完成一次打卡
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun onFinishOnce(task: TaskCycle, data: Long = TimeUtil.getNowTime()) {
        when {
            // 时间设置正确
            (task.startTimes == 0L
                    && task.endTimes == 0L
                    && task.addTimeDay == 0) -> {
                MassageUtil.sendToast("时间设置空白！")
            }
            // 不超出时间范围
            !((task.startTimes <= data) && (data <= task.endTimes)) -> {
                MassageUtil.sendToast("当前时间不在可完成内！")
            }
            // 不超出可完成的次数
            (task.finishedTimes.size+1 > task.needCompleteNum) -> {
                MassageUtil.sendToast("超出了可完成的次数！")
            }
            // 今天是否已经打卡
            (TimeUtil.isToDay(task.finishedTimes)) -> {
                MassageUtil.sendToast("今天已打卡了！")
            }
            else -> {
                task.finishedTimes = arrayListOf<Long>().also {
                    it.addAll(task.finishedTimes)
                    it.add(data)
                }

                task.state = false
                update(task)
            }
        }

        // 检查是否达到了完成任务的标准
        if (task.finishedTimes.size == task.needCompleteNum) {
            task.state = true
            update(task)
//            onCirculateAddNewTask(task)?.also {
//                insert(it)
//            }
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
                insert(it)
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun onCirculateAddNewTask(taskCycle: TaskCycle): TaskCycle? {
        return if (taskCycle.onLoopInspect()) {
            taskCycle.onLoopNewData()
        } else {
            null
        }
    }

    /**
     * 检查数据是否有不规范的地方
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

    /**
     * 删除 “当天” 的打卡日期
     *
     * @param taskCycle 需要操作的打卡
     * @param times 需要删除的某天（默认当天）
     *
     * @return 删除成功与否
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteToDay(taskCycle: TaskCycle, times: Long = TimeUtil.getNowTime()): Boolean {
        // 判断要删除哪一天的，标记出范围
        val timeLocalDate = TimeUtil.getLocalData(times)
        val start = TimeUtil.getSomeDayStartTimers(timeLocalDate)
        val end = TimeUtil.getSomeDayEndTimers(timeLocalDate)

        // 循环检查时间
        for (saveTime in taskCycle.finishedTimes) {
            if (saveTime in start..end) {
                // 删除 “当天” 的打卡
                val newList = arrayListOf<Long>()
                newList.addAll(taskCycle.finishedTimes)
                newList.remove(saveTime)
                // 更新数据过去
                taskCycle.finishedTimes = newList
                return true
            }
        }
        return false
    }
}