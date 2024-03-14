package llxbh.zeropointone.data.model

import android.database.Observable
import androidx.databinding.ObservableField

/**
 * 清单任务里面的检查事项
 */
class TaskCheck(
    val state: ObservableField<Boolean> = ObservableField(false),
    val content: ObservableField<String> = ObservableField("")
)