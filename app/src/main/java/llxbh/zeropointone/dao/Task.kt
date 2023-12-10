package llxbh.zeropointone.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import llxbh.zeropointone.data.TaskCheck
import llxbh.zeropointone.data.TaskCheckConverters
import java.util.Date

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
    var checks: List<TaskCheck>? = null,
    var updateTimes: Long = 0,
    var startTimes: Long = 0,
    var endTimes: Long = 0,
    var addTimeDay: Int = 0
)
