package llxbh.zeropointone

import android.accessibilityservice.AccessibilityService.SoftKeyboardController
import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import llxbh.zeropointone.data.TaskCheck
import llxbh.zeropointone.databinding.TaskContentCheckItemBinding

class TaskContentCheckAdapter: BaseQuickAdapter<TaskCheck, TaskContentCheckAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: TaskContentCheckItemBinding = TaskContentCheckItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: TaskCheck?) {
        holder.binding.apply {
            check = item
            btnTaskContentCheckDelete.setOnClickListener {
                item?.let {
                    remove(it)
                }
            }
            executePendingBindings()
        }
    }

}