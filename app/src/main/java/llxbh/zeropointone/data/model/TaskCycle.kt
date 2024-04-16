package llxbh.zeropointone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import llxbh.zeropointone.data.repository.TaskCheckConverters

@Entity(tableName = "TaskCycle")
@TypeConverters(TaskCheckConverters::class)
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
    val finishedTimes: List<Long> = arrayListOf(),
    val needCompleteNum: Int = 0,
    var addTimeDay: Int = 0,
    var isDelete: Boolean = false
)
