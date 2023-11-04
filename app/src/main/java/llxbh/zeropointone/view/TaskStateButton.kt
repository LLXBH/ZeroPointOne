package llxbh.zeropointone.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import llxbh.zeropointone.R

/**
 * 根据完成或未完成来自动切换不同图片
 */
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