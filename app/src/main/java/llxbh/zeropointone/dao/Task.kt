package llxbh.zeropointone.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 清单任务数据类
 * Room：数据实体
 */
@Entity(tableName = "Task")
data class Task(
    @PrimaryKey val id: Int,
    val state: Boolean = false,
    val title: String,
    val content: String = ""
)
