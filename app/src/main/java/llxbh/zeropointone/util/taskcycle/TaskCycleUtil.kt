package llxbh.zeropointone.util.taskcycle

import llxbh.zeropointone.api.TaskCycleDao
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.data.model.TaskCycle

class TaskCycleUtil(
    val taskCycleDao: TaskCycleDao,
    val taskCycleInterface: TaskCycleInterface
) {

    fun get(taskId: Int): TaskCycle = taskCycleDao.get(taskId)

    fun getAll(): List<TaskCycle> = taskCycleDao.getAll()



}