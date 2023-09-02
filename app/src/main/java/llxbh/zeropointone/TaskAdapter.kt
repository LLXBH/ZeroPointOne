package llxbh.zeropointone

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import llxbh.zeropointone.dao.Task
import java.time.ZoneId

class TaskAdapter(
    private val taskData: List<Task>
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = taskData[position]
        (holder as ViewHolder).apply {
            taskState.isChecked = task.state
            taskTitle.text = task.title
            taskTitle.setOnClickListener {
                mOnTaskClick?.setOnTaskClick(position)
            }
            taskDate.text = task?.date?.let { TimeTools.dateToString(it) }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val taskState: CheckBox = itemView.findViewById(R.id.cb_taskState)
        val taskTitle: TextView = itemView.findViewById(R.id.tv_taskTitle)
        val taskDate: TextView = itemView.findViewById(R.id.tv_taskDate)
    }

}