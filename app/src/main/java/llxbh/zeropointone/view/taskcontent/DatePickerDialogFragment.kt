package llxbh.zeropointone.view.taskcontent

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment

/**
 * 日期选择视图
 */
class DatePickerDialogFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity!!, this, year, month, day).apply {
            // 默认第一天是周一
            datePicker.firstDayOfWeek = Calendar.MONDAY
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        // 月从 0 开始，+1 修正
        when {
            (activity is TaskContentCreateActivity) -> {
                (activity as TaskContentCreateActivity).setDate(p1, p2+1, p3)
            }

        }
    }

}