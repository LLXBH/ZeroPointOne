package llxbh.zeropointone.util.tomato

import android.os.CountDownTimer

/**
 * 番茄钟
 *
 * 番茄钟的具体实现
 *
 */
class TomatoCloakUtil(
    val tomato: Tomato,
    val stateInterface: TomatoClockStateInterface
) {

    private val COUNTDOWN_INTERVAL = 1000L // 每秒递减

    // 练习次数
    private var mPracticeFrequencyNum = 0

    // 倒计时
    private var mCountDownTimer: CountDownTimer? = null

    // 时间记录
    private var mStartTimeInMillis = 0L
    private var mElapsedTimeInMillis = 0L

    private var mIsPractice = true  // 练习？
    private var mIsPause = false    // 暂停？

    /**
     * 开始
     *
     * 开始倒计时，会自动判断是练习还是休息等，然后获取具体的时间来倒数；
     *
     */
    fun onStart() {
        // 没有倒计时的时候才执行下面的操作
        if (mCountDownTimer != null) {
            return
        }

        // 获取时间的同时，如果是暂停的，就将状态反转过来，无须回调 onCountDownTimeStart() 了；
        mStartTimeInMillis = getCountdownTime()
        if (mIsPause) {
            // 将状态扭转过来
            mIsPause = false
        } else {
            onCountDownTimeStart(mStartTimeInMillis)
        }

        // 初始化一些计时的相关变量
        mElapsedTimeInMillis = 0L

        // 根据当前信息初始化倒计时类
        mCountDownTimer = object : CountDownTimer(mStartTimeInMillis, COUNTDOWN_INTERVAL) {

            override fun onTick(p0: Long) {
                if (! mIsPause) {
                    // 判断模式
                    when {
                        // 练习
                        (mIsPractice) -> {
                            stateInterface.onPracticeIntermission(p0)
                        }
                        // 小休息
                        (! isRestLong()) -> {
                            stateInterface.onRestShortTimeIntermission(p0)
                        }
                        // 大休息
                        else -> {
                            stateInterface.onRestLongTimeIntermission(p0)
                        }
                    }

                    // 记录已经消耗的时间
                    mElapsedTimeInMillis += COUNTDOWN_INTERVAL
                } else {
                    // 已被暂停
                }
            }

            override fun onFinish() {
                onCountDownTimeEnd()
            }

        }
        mCountDownTimer?.start()
    }

    /**
     * 暂停
     *
     * 暂停倒计时，会保存剩余的时间，重新运行 onStart() 继续倒计时；
     */
    fun onPause() {
        mIsPause = true
        mCountDownTimer?.cancel()
        mCountDownTimer = null
    }

    /**
     * 结束
     *
     * 结束倒计时，主动结束，而不是时间倒数完后的结束；
     */
    fun onEnd() {
        // 倒计时都为空了，还操作啥？
        if (mCountDownTimer == null) {
            return
        }

        onCountDownTimeEnd()
    }

    /**
     * 倒计时开始前的预备的操作，表示随时会开始倒计时
     *
     * 判断执行接口的方法，回调对应的方法；
     *
     * @param timer 倒计时长（毫秒）
     *
     */
    private fun onCountDownTimePrepared(timer: Long) {
        // 判断模式
        when {
            // 练习
            (mIsPractice) -> {
                stateInterface.onPracticeStartPrepare(timer)
            }
            // 小休息
            (! isRestLong()) -> {
                stateInterface.onRestShortTimeStartPrepared(timer)
            }
            // 大休息
            else -> {
                stateInterface.onRestLongTimeStartPrepared(timer)
            }
        }
    }

    /**
     * 倒计时开始前的操作
     *
     * 判断执行接口的方法，回调对应的方法；
     *
     * @param timer 倒计时长（毫秒）
     *
     */
    private fun onCountDownTimeStart(timer: Long) {
        // 判断模式
        when {
            // 练习
            (mIsPractice) -> {
                stateInterface.onPracticeStart(timer)
            }
            // 小休息
            (! isRestLong()) -> {
                stateInterface.onRestShortTimeStart(timer)
            }
            // 大休息
            else -> {
                stateInterface.onRestLongTimeStart(timer)
            }
        }
    }

    /**
     * 倒计时结束后的操作
     *
     * 判断执行接口的方法，回调对应的方法；
     *
     * @param timer 倒计时长（毫秒）
     *
     */
    private fun onCountDownTimeEnd() {
        // 执行 cancel 的时候，不会回调到 CountDownTimer 类的 onFinish() 方法；
        mCountDownTimer?.cancel()
        mCountDownTimer = null

        // 判断模式
        when {
            // 练习
            (mIsPractice) -> {
                // 练习次数 + 1
                mPracticeFrequencyNum ++
                stateInterface.onPracticeEnd(getPracticeFrequencyNum())
            }
            // 小休息
            (! isRestLong()) -> {
                stateInterface.onRestShortTimeEnd()
            }
            // 大休息
            else -> {
                stateInterface.onRestLongTimeEnd()
            }
        }

        // 反转状态(练习 《=》 休息)
        mIsPractice = !mIsPractice

        // 回调进入准备状态
        onCountDownTimePrepared(getCountdownTime())
    }

    /**
     * 获取倒计时时长
     *
     * 根据当前的状态（练习或休息），以及判断练习的次数是否达到了大休息等，
     * 返回对于的练习时长
     *
     * @return 时长（毫秒）
     *
     */
    private fun getCountdownTime(): Long {
        return when {
            // 暂停过？
            (mIsPause) -> {
                mStartTimeInMillis -= mElapsedTimeInMillis
                mStartTimeInMillis
            }
            // 练习时间
            (mIsPractice) -> {
                tomato.practiceTime * 60 * COUNTDOWN_INTERVAL
            }
            // 小休时间
            (! isRestLong()) -> {
                tomato.restShortTime * 60 * COUNTDOWN_INTERVAL
            }
            // 大休时间
            else -> {
                tomato.restLongTime * 60 * COUNTDOWN_INTERVAL
            }
        }
    }

    /**
     * 判断是否大休息
     *
     * 根据当前记录的已经练习的次数，来判断是否是大休息了
     *
     * @return 是否大休息
     *
     */
    private fun isRestLong(): Boolean {
        return (mPracticeFrequencyNum != 0)
                && (mPracticeFrequencyNum % tomato.practiceFrequency == 0)
    }

    /**
     * 练习的次数
     *
     * @return 次数
     *
     */
    fun getPracticeFrequencyNum(): Int {
        return mPracticeFrequencyNum
    }

}