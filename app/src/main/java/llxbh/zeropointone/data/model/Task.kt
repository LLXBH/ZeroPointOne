package llxbh.zeropointone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import llxbh.zeropointone.data.repository.TaskCheckConverters
import llxbh.zeropointone.util.time.TimeUtil

/**
 * 清单任务数据类
 * Room：数据实体
 */
@Entity(tableName = "Task")
@TypeConverters(TaskCheckConverters::class)
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var state: Boolean = false,
    var title: String,
    var content: String = "",
    var checks: List<TaskCheck> = arrayListOf(),
    var updateTimes: Long = 0,
    var startTimes: Long = 0,
    var endTimes: Long = 0,
    var addTimeDay: Int = 0,
    var isDelete: Boolean = false
): TaskLoopInterface<Task> {
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
            else -> {
                true
            }
        }
    }

    override fun onLoopNewData(): Task {
        val newData = this.copy(
            id = 0,
            state = false,
            updateTimes = TimeUtil.getNowTime(),
            startTimes = TimeUtil.getNewTime(this.startTimes, this.addTimeDay),
            endTimes = TimeUtil.getNewTime(this.endTimes, this.addTimeDay),
            checks = arrayListOf<TaskCheck>().also {
                // 将旧的所有子项都复制一份新的
                for (check in this@Task.checks) {
                    it.add(check.onLoopNewData())
                }
            }
        )
        return newData
    }
}
