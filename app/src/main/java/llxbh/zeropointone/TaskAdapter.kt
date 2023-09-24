package llxbh.zeropointone

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
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

        /**
         * 清单的点击事件
         */
        fun setOnTaskClick(position: Int)

        /**
         * 清单的状态点击事件
         */
        fun setOnTaskStateClick(position: Int, isChecked: Boolean)
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
            taskState.state = task.state
            taskState.setOnClickListener {
                // 反向当前状态
                val newState = !task.state
                taskState.state = newState
                mOnTaskClick?.setOnTaskStateClick(adapterPosition, newState)
            }
            taskTitle.text = task.title
            taskTitle.setOnClickListener {
                mOnTaskClick?.setOnTaskClick(adapterPosition)
            }
            taskDate.text = task.startTimes.let {
                if (it != 0L) {
                    TimeTools.timesToString(it)
                } else {
                    ""
                }
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val taskState: TaskStateButton = itemView.findViewById(R.id.ibtn_taskState)
        val taskTitle: TextView = itemView.findViewById(R.id.tv_taskTitle)
        val taskDate: TextView = itemView.findViewById(R.id.tv_taskDate)
    }

}