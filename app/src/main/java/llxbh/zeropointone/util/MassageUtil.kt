package llxbh.zeropointone.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import llxbh.zeropointone.R
import llxbh.zeropointone.app.appContext

object MassageUtil {

    /**
     * 发送消息
     *
     * @param msg 消息
     * @param duration 需要展示多久
     */
    fun sendToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                appContext,
                msg,
                duration
            ).show()
        }
    }

    /**
     * 发送通知
     *
     * @param title 标题
     * @param msg 消息
     */
    fun sendNotification(title: String, msg: String) {
        // 创建通知渠道
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 是否大于或等于 安卓8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "Your Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "This is your channel description."
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 准备通知内容
        val notificationBuilder = NotificationCompat.Builder(appContext, "my_channel_id")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // 发送
        val notificationId = 1 // 通知的唯一 ID
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    /**
     * 发送通知
     *
     * @param msg 消息
     */
    fun sendNotification(msg: String) {
        sendNotification(
            appContext.getString(R.string.app_name),
            msg
        )
    }

}