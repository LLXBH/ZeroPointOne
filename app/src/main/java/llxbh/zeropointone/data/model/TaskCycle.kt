package llxbh.zeropointone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import llxbh.zeropointone.data.repository.ListLongConverters
import llxbh.zeropointone.data.repository.TaskCheckConverters
import llxbh.zeropointone.util.time.TimeUtil

@Entity(tableName = "TaskCycle")
@TypeConverters(TaskCheckConverters::class, ListLongConverters::class)
data class TaskCycle(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var state: Boolean = false,
    var title: String,
    var content: String = "",
    var checks: List<TaskCheck> = arrayListOf(),
    var updateTimes: Long = 0,
    var startTimes: Long = 0,
    var endTimes: Long = 0,
    var finishedTimes: List<Long> = arrayListOf(),
    var needCompleteNum: Int = 0,
    var addTimeDay: Int = 0,
    var isDelete: Boolean = false,
): TaskLoopInterface<TaskCycle> {
    override fun onLoopInspect(): Boolean {
        return when {
            // 任务未完成
            (!state) -> {
                false
            }
            // 没有设置要循环的天数
            (addTimeDay == 0) -> {
                false
            }
            // 没有设置开始、结束时间
            (startTimes == 0L && endTimes == 0L) -> {
                false
            }
            // 需要完成的次数未达到
            (finishedTimes.size < needCompleteNum) -> {
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onLoopNewData(): TaskCycle {
        val newData = this.copy(
            id = 0,
            state = false,
            updateTimes = TimeUtil.getNowTime(),
            startTimes = TimeUtil.getNewTime(this.startTimes, addTimeDay),
            endTimes = TimeUtil.getNewTime(this.endTimes, addTimeDay),
            finishedTimes = listOf(),
            checks = arrayListOf<TaskCheck>().also {
                // 将旧的所有子项都复制一份新的
                for (check in this@TaskCycle.checks) {
                    it.add(check.onLoopNewData())
                }
            }
        )
        return newData
    }

}
