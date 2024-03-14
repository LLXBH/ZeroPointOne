package llxbh.zeropointone.view.tomatoclock

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import llxbh.zeropointone.R
import llxbh.zeropointone.base.BindingBaseFragment
import llxbh.zeropointone.databinding.FragmentTomatoClockBinding

class TomatoClockFragment: BindingBaseFragment<FragmentTomatoClockBinding>() {

    // 默认时间
    private val sPracticeTimeDefault = 30
    private val sPracticeIntervalDefault = 5
    private val sPracticeFrequencyDefault = 4
    private val sPracticeRestDefault = 45

    // 时间运行状态
    private var mTimingRun = false
    private var mTimingStop = false
    private var mTimingCountdownTimer: CountDownTimer? = null

    // 当前时间
    private var mTiming = arrayListOf(0L, 0L)

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
            mPracticeMode = true

            btnTomatoClockStart.setOnClickListener {
                // 开始倒计时
                startTiming()
            }

            btnTomatoClockSuspend.setOnClickListener {
                // 暂停时间
                stopTiming()
            }

            btnTomatoClockFinish.setOnClickListener {
                // 结束当前时间，进入下一个步骤
                endTiming()
            }

        }
    }

    private fun startTiming() {
        getBinding().apply {
            // 时间是否已在变动？
            if (mTimingRun) {
                Toast.makeText(activity, "时间已经开始了！", Toast.LENGTH_SHORT)
                    .show()
                return
            } else if (mTimingStop) {
                // 之前暂停了时间继续开始
                stopTimingRest()
            } else {
                // 开启新的时间
                mTimingRun = true
                mTimingStop = false
                if (mPracticeMode!!) {
                    // 练习
                    startTimingPractice()
                } else {
                    // 休息
                    startTimingInterval()
                }
//                mPracticeMode = !mPracticeMode!!
            }
        }

    }
    private fun startTimingPractice() {
        Toast.makeText(activity, "开始练习！", Toast.LENGTH_SHORT)
            .show()
        getBinding().etTomatoClockPracticeTime.also {
            if (it.text.isEmpty()) {
                it.setText(sPracticeTimeDefault.toString())
            }
            setTimingCountdownTimer(setTimingTextPractice())
            mTimingCountdownTimer?.start()
        }
    }
    private fun startTimingInterval() {
        Toast.makeText(activity, "开始休息！", Toast.LENGTH_SHORT)
            .show()
        setTimingCountdownTimer(setTimingTextInterval())
        mTimingCountdownTimer?.start()
    }

    private fun setTimingCountdownTimer(minute: Long, second: Long = 0L) {
        // 时间是毫秒，需要转换下
        mTimingCountdownTimer = object : CountDownTimer((minute * 60 * 1000) + (second * 1000), 1000) {

            override fun onTick(p0: Long) {
                setTimingText(
                    p0 / 1000 / 60,
                    (p0 / 1000) % 60
                )
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                endTiming()
            }

        }
    }
    @SuppressLint("SetTextI18n")
    private fun setTimingText(minute: Long, second: Long = 0L) {
        mTiming[0] = minute
        mTiming[1] = second
        getBinding().tvTomatoClockTime.text = "${minute.toString().padStart(2, '0')}：${second.toString().padStart(2, '0')}"
    }
    private fun setTimingTextPractice(): Long {
        getBinding().etTomatoClockPracticeTime.also {
            if (it.text.isEmpty()) {
                it.setText(sPracticeTimeDefault.toString())
            }
            return it.text.toString().toLong()
        }
    }
    private fun setTimingTextInterval(): Long {
        getBinding().apply {
            // 当前
            val forNew = tvTomatoClockPracticeAllFrequency.text.toString().toInt()
            // 设定
            if (etTomatoClockPracticeFrequency.text.isEmpty()) {
                etTomatoClockPracticeFrequency.setText(sPracticeFrequencyDefault.toString())
            }
            val setFrequency = etTomatoClockPracticeFrequency.text.toString().toInt()
            // 判断
            if ((forNew != 0) && (forNew % setFrequency == 0)) {
                // 大休息
                if (etTomatoClockPracticeRest.text.isEmpty()) {
                    etTomatoClockPracticeRest.setText(sPracticeRestDefault.toString())
                }
                return etTomatoClockPracticeRest.text.toString().toLong()
            } else {
                // 小休息
                if (etTomatoClockPracticeInterval.text.isEmpty()) {
                    etTomatoClockPracticeInterval.setText(sPracticeIntervalDefault.toString())
                }
                return etTomatoClockPracticeInterval.text.toString().toLong()
            }
        }
    }

    private fun stopTiming() {
        mTimingRun = false
        mTimingStop = true
        mTimingCountdownTimer?.cancel()
    }
    private fun stopTimingRest() {
        mTimingRun = true
        mTimingStop = false
        setTimingCountdownTimer(mTiming[0], mTiming[1])
        setTimingText(mTiming[0], mTiming[1])
        mTimingCountdownTimer?.start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun endTiming() {
        if (!mTimingRun) {
            Toast.makeText(activity, "时间都没开始！", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // 清理时间
        mTimingRun = false
        mTimingStop = false
        mTimingCountdownTimer?.cancel()
        setTimingCountdownTimer(0, 0)
        setTimingText(0, 0)
        activity?.let { sendNotification(it) }
//        Toast.makeText(activity, "时间结束了！", Toast.LENGTH_SHORT)
//            .show()

        getBinding().apply {
            // 累计练习次数
            if (mPracticeMode!!) {
                if (tvTomatoClockPracticeAllFrequency.text.isEmpty()) {
                    tvTomatoClockPracticeAllFrequency.text = "0"
                }
                var forNew = tvTomatoClockPracticeAllFrequency.text.toString().toInt()
                forNew++
                tvTomatoClockPracticeAllFrequency.text = "$forNew"
            }

            // 反转练习或休息
            mPracticeMode = !mPracticeMode!!

            // 显示时间
            if (mPracticeMode!!) {
                setTimingTextPractice()
            } else {
                setTimingTextInterval()
            }
        }

    }

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