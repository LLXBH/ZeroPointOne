package llxbh.zeropointone

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.Constraints
import androidx.constraintlayout.solver.state.State.Constraint
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import llxbh.zeropointone.dao.Task
import llxbh.zeropointone.tools.TimeTools
import llxbh.zeropointone.view.TaskStateButton

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
//            taskState.isSelected = task.state
//            taskState.setOnClickListener {
//                // 反向当前状态
//                val newState = !task.state
//                taskState.isSelected = newState
//                mOnTaskClick?.setOnTaskStateClick(adapterPosition, newState)
//            }
            taskState.isChecked = task.state
            taskState.setOnClickListener { _ ->
                // 点击后，反向当前状态
                val newState = !task.state
                taskState.isChecked = newState
                mOnTaskClick?.setOnTaskStateClick(adapterPosition, newState)
            }

            taskTitle.text = task.title
            taskTitle.setOnClickListener {
                mOnTaskClick?.setOnTaskClick(adapterPosition)
            }

            if (task.content.isNotEmpty()) {
                taskContent.height = 60
                taskContent.text = task.content
            } else {
                taskContent.height = 0
            }

            taskDate.text = task.startTimes.let {
                if (it != 0L) {
                    taskDate.height = 60
                    TimeTools.timesToString(it)
                } else {
                    taskDate.height = 0
                    ""
                }
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val taskState: CheckBox = itemView.findViewById(R.id.cb_taskState)
        val taskTitle: TextView = itemView.findViewById(R.id.tv_taskTitle)
        val taskContent: TextView = itemView.findViewById(R.id.tv_taskContent)
        val taskDate: TextView = itemView.findViewById(R.id.tv_taskDate)
    }

}