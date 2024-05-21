package llxbh.zeropointone.view.tomatoclock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import llxbh.zeropointone.R
import llxbh.zeropointone.base.BindingBaseFragment
import llxbh.zeropointone.databinding.FragmentTomatoClockBinding
import llxbh.zeropointone.util.tomato.Tomato
import llxbh.zeropointone.util.tomato.TomatoCloakUtil
import llxbh.zeropointone.util.tomato.TomatoClockInterface

class TomatoClockFragment: BindingBaseFragment<FragmentTomatoClockBinding>() {

    private val sTomato = Tomato(
        30,
        4,
        5,
        45
    )
    private val sTomatoCloakUtil = TomatoCloakUtil(
        sTomato,
        object : TomatoClockInterface {
            override fun onPracticeStartPrepare(timer: Long) {
                getBinding().also {
                    it.tvTomatoClockMode.text = "练习"
                    it.tvTomatoClockTime.text = getMillisecondsToTimeFormat(timer)
                }
            }

            override fun onPracticeEnd(num: Long) {
                getBinding().also {
                    it.mPracticeTiming = getMillisecondsToTimeFormat(0)
                    it.mAllFrequency = num.toString()
                }
            }

            override fun onRestShortTimeStartPrepared(timer: Long) {
                getBinding().also {
                    it.tvTomatoClockMode.text = "小休息"
                    it.tvTomatoClockTime.text = getMillisecondsToTimeFormat(timer)
                }
            }

            override fun onRestShortTimeEnd() {
//                TODO("Not yet implemented")
            }

            override fun onRestLongTimeStartPrepared(timer: Long) {
                getBinding().also {
                    it.tvTomatoClockMode.text = "大休息"
                    it.tvTomatoClockTime.text = getMillisecondsToTimeFormat(timer)
                }
            }

            override fun onRestLongTimeEnd() {
//                TODO("Not yet implemented")
            }

            override fun onIntermission(timer: Long) {
                getBinding().tvTomatoClockTime.text = getMillisecondsToTimeFormat(timer)
            }

        }
    )


    companion object {
        fun newInstance(): TomatoClockFragment {
            return TomatoClockFragment()
        }
    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentTomatoClockBinding {
        return FragmentTomatoClockBinding.inflate(
            inflater,
            container,
            false
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBinding().apply {
            sTomato = this@TomatoClockFragment.sTomato
            mAllFrequency = "0"

            btnTomatoClockStart.setOnClickListener {
                // 开始倒计时
                sTomatoCloakUtil.onStart()
            }

            btnTomatoClockSuspend.setOnClickListener {
                // 暂停时间
                sTomatoCloakUtil.onPause()
            }

            btnTomatoClockFinish.setOnClickListener {
                // 结束当前时间，进入下一个步骤
                sTomatoCloakUtil.onEnd()
            }

        }
    }


    /**
     * 发送通知
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(context: Context) {
        val channelId = "my_channel_id"
        // 创建通知渠道
        val notificationChannel = NotificationChannel(
            channelId,
            "My Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Description of my channel"
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        // 准备通知内容
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("零点壹")
            .setContentText("时间结束了！！！")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // 发送
        val notificationId = 1 // 通知的唯一 ID
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}