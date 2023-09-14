package llxbh.zeropointone

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton

@SuppressLint("AppCompatCustomView")
class TaskStateButton: ImageButton {

    var state = false
        set(value) {
            setImageResource(if (value) {
                R.drawable.task_state_complete_24
            } else {
                R.drawable.task_state_incomplete_24
            })
        }

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        state = context.theme
            .obtainStyledAttributes(R.styleable.TaskStateButton)
            .getBoolean(R.styleable.TaskStateButton_state, false)
    }

}