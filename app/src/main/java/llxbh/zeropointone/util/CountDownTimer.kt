package llxbh.zeropointone.util

import android.os.CountDownTimer

/**
 * 倒计时
 *
 * 封装 CountDownTimer 的一个工具，
 * 添加了 “暂停” 的功能
 */
abstract class CountDownTimer(
    private val totalTime: Long,
    private val intervalTime: Long
) {

    // 是否处于暂停
    private var mStopTime = false
    // 剩余的时间
    private var mRemainingTime = totalTime
    // 倒计时工具
    private var mCountDownTimer = getCountDownTimer()

    /**
     * 利用目前的时间创建一个倒计时
     */
    private fun getCountDownTimer(): CountDownTimer {
        return object : CountDownTimer(mRemainingTime, intervalTime) {
            override fun onTick(millisUntilFinished: Long) {
                // 回调自己的抽象方法
                this@CountDownTimer.onInterval(millisUntilFinished)
                // 剩余的时间，用于 “暂停” 的使用
                this@CountDownTimer.mRemainingTime = millisUntilFinished
            }

            override fun onFinish() {
                // 回调自己的抽象方法
                this@CountDownTimer.onFinish()
            }

        }
    }

    /**
     * 开始
     */
    fun onStart() {
        mCountDownTimer.start()
    }

    /**
     * 暂停
     */
    fun onStop() {
        // 注销掉目前的定时器
        onCancel()
        mStopTime = true
        // 利用剩余的时间来创建一个新的定时器
        mCountDownTimer = getCountDownTimer()
    }

    /**
     * 恢复（暂停）
     */
    fun onRest() {
        if (mStopTime) {
            mCountDownTimer.start()
        }
        mStopTime = false
    }

    /**
     * 注销
     */
    fun onCancel() {
        mCountDownTimer.cancel()
    }

    /**
     * 间歇，同 onTick
     */
    abstract fun onInterval(millisUntilFinished: Long)

    /**
     * 结束
     */
    abstract fun onFinish()

}