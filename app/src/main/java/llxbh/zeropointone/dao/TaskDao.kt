package llxbh.zeropointone.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * 对于 Task 表的操作
 * Room：数据访问对象（DAO）
 */
@Dao
interface TaskDao {

    /**
     * 获取全部的清单任务
     */
    @Query("SELECT * FROM Task")
    fun getAll(): List<Task>

    /**
     * 获取指定的清单任务
     */
   @Query("SELECT * FROM Task WHERE id = :taskId")
    fun get(taskId: Int): Task

    /**
     * 插入新的清单任务
     */
    @Insert
    fun insert(task: Task)

    /**
     * 更新指定的清单任务
     */
    @Update
    fun update(task: Task)

    /**
     * 删除指定的清单任务
     */
    @Delete
    fun delete(task: Task)

}