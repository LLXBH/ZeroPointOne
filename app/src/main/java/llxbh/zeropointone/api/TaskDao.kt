package llxbh.zeropointone.api

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import llxbh.zeropointone.data.model.Task

/**
 * 对于 Task 表的操作
 * Room：数据访问对象（DAO）
 */
@Dao
interface TaskDao {

    /**
     * 获取全部的清单任务
     */
    @Query("select * from Task where isDelete = false")
    fun getAll(): List<Task>

    /**
     * 获取全部的任务，且按时间排序
     */
    @Query("SELECT * FROM Task ORDER BY startTimes")
    fun getAllAndTimeOrder(): List<Task>

    /**
     * 获取全部的任务，且按状态排序
     */
    @Query("SELECT * FROM Task ORDER BY state")
    fun getAllAndStateOrder(): List<Task>

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

    @Query("select * from Task where isDelete = true")
    fun getRecycleBin(): List<Task>
}