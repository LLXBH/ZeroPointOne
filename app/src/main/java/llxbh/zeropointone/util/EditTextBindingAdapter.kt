package llxbh.zeropointone.util

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableLong

/**
 * Binding 适配器
 */
object BindingAdapters {

    /**
     * 使 app:text 可以直接接收 ObservableLong
     */
    @BindingAdapter("longText")
    @JvmStatic
    fun setLongText(editText: EditText, value: ObservableLong?) {
        value?.let {
            editText.setText(it.get().toString())
            editText.setSelection(editText.text.length) // 保持光标在末尾，如果需要
        }
    }

}