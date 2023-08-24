package llxbh.zeropointone

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val taskData: List<TaskData>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnTaskClick: OnTaskClick? = null

    /**
     * 向外暴露的点击接口
     */
    interface OnTaskClick {
        fun setOnTaskClick(position: Int)
    }

    fun setTaskClick(onTaskClick: OnTaskClick) {
        mOnTaskClick = onTaskClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(LayoutInflater.from(parent.context)
          .inflate(R.layout.task_item, parent, false))
    }

    override fun getItemCount(): Int = taskData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = taskData[position]
        (holder as ViewHolder).apply {
            taskState.isChecked = task.state
            taskTitle.text = task.title
            taskTitle.setOnClickListener {
                mOnTaskClick?.setOnTaskClick(position)
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val taskState: CheckBox = itemView.findViewById(R.id.cb_taskState)
        val taskTitle: TextView = itemView.findViewById(R.id.tv_taskTitle)
    }

}