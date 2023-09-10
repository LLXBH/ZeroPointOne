package llxbh.zeropointone.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 清单任务数据类
 * Room：数据实体
 */
@Entity(tableName = "Task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var state: Boolean = false,
    var title: String,
    var content: String = "",
    var date: Date?
)
