package llxbh.zeropointone.data.model

import android.database.Observable
import androidx.databinding.ObservableField

/**
 * 清单任务里面的检查事项
 */
data class TaskCheck(
    val state: ObservableField<Boolean> = ObservableField(false),
    val content: ObservableField<String> = ObservableField("")
): TaskLoopInterface<TaskCheck> {
    override fun onLoopInspect(): Boolean {
        return false
    }

    override fun onLoopNewData(): TaskCheck {
        val newData = this.copy().also {
            state.set(false)
        }
        return newData
    }

}