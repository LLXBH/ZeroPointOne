package llxbh.zeropointone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    val taskData: List<TaskData>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(
                R.layout.task_item,
                parent,
                false
            ))
    }

    override fun getItemCount(): Int = taskData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = taskData[position]
        (holder as ViewHolder).taskTitle.apply {
            text = data.title
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val taskTitle = itemView.findViewById<CheckBox>(R.id.cb_taskTitle)
    }

}