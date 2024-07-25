package llxbh.zeropointone.data.model

import android.util.Log
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
): TaskLoopInterface<Task>, TaskStandardInterface<Task> {
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

    override fun onInspectContent(): Boolean {
        try {
            if (title.isEmpty() && content.isEmpty()) {
                throw Exception("清单：标题和内容不能都为空。")
            }
            if (title.isEmpty() && content.isNotEmpty()) {
                throw Exception("清单：标题不能为空。")
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
            return false
        }
        return true
    }

    override fun onCorrectContent(): Task? {
        this.copy().apply {
            try {
                // 内容不为空的的时候，把内容赋予给标题
                if (title.isEmpty() && content.isNotEmpty()) {
                    title = content
                    content = ""
                }
                if (! onInspectContent()) {
                    throw Exception("清单：数据修正失败。")
                }
            } catch (e: Exception) {
                Log.e(this.javaClass.name, e.message.toString())
                return null
            }
            return this
        }
    }

    override fun onInspectDateTime(): Boolean {
        try {
            if (addTimeDay != 0 && startTimes == 0L && endTimes == 0L) {
                throw Exception("清单：若设置了自增天数，开始时间和结束时间都不得为 0 。")
            }
            if (startTimes != 0L && endTimes == 0L) {
                throw Exception("清单：开始时间和结束时间不能一边不为 0 ，一边为 0 ，如果设置同一天，则开始和结束需要相同。")
            }
            if (startTimes == 0L && endTimes != 0L) {
                throw Exception("清单：开始时间和结束时间不能一边不为 0 ，一边为 0 ，如果设置同一天，则开始和结束需要相同。")
            }
            if (startTimes > endTimes) {
                throw Exception("清单：结束时间不能比开始时间早。")
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
            return false
        }
        return true
    }

    override fun onCorrectDateTime(): Task? {
        this.copy().apply {
            try {
                if (addTimeDay != 0 && startTimes == 0L && endTimes == 0L) {
                    addTimeDay = 0
                }
                if (startTimes != 0L && endTimes == 0L) {
                    endTimes = startTimes
                }
                if (startTimes == 0L && endTimes != 0L) {
                    startTimes = endTimes
                }
                if (startTimes > endTimes) {
                    endTimes = startTimes
                }
                if (! onInspectDateTime()) {
                    throw Exception("清单：数据修正失败。")
                }
            } catch (e: Exception) {
                Log.e(this.javaClass.name, e.message.toString())
                return null
            }
            return this
        }
    }
}
