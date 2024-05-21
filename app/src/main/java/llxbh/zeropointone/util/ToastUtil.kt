package llxbh.zeropointone.util

import android.content.Context
import android.widget.Toast
import llxbh.zeropointone.app.appContext

object ToastUtil {

    private val sContext: Context = appContext

    fun show(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(
            sContext,
            msg,
            duration
        ).show()
    }

}