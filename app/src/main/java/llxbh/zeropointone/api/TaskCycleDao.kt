package llxbh.zeropointone.api

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import llxbh.zeropointone.data.model.TaskCycle

/**
 * 对 TaskCycle 表的操作
 */
@Dao
interface TaskCycleDao {

    /**
     * 获取全部的清单任务
     */
    @Query("select * from TaskCycle Order by state")
    fun getAll(): List<TaskCycle>

    /**
     * 获取全部的清单任务（完成与否）
     */
    @Query("select * from TaskCycle where isDelete = :isDelete")
    fun getAll(isDelete: Boolean): List<TaskCycle>

    /**
     * 获取指定的清单任务
     */
    @Query("select * from TaskCycle where id = :taskId")
    fun get(taskId: Int): TaskCycle

    /**
     * 插入新的清单任务
     */
    @Insert
    fun insert(task: TaskCycle)

    /**
     * 更新指定的清单任务
     */
    @Update
    fun update(task: TaskCycle)

    /**
     * 删除指定的清单任务
     */
    @Delete
    fun delete(task: TaskCycle)
}