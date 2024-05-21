package llxbh.zeropointone.util.tomato

/**
 * 番茄钟的状态回调接口，实现后可以在番茄钟的各个阶段进行界面或数据的更新；
 */
interface TomatoClockInterface {

    /**
     * 练习 - 预备
     *
     * 准备状态，随时会开始倒计时
     *
     * @param timer 练习时长
     *
     */
    fun onPracticeStartPrepare(timer: Long)

    /**
     * 练习 - 开始
     *
     * 开始倒计时之前回被调用，例如用于发送开始通知等；
     *
     * @param timer 练习时长（毫秒）
     *
     */
    fun onPracticeStart(timer: Long) {}

    /**
     * 练习 - 间歇
     *
     * 练习的过程中会被调用，并且附带了剩余的时间；
     *
     * @param timer 剩余时间（毫秒）
     *
     */
    fun onPracticeIntermission(timer: Long) {
        onIntermission(timer)
    }

    /**
     * 练习 - 结束
     *
     * 练习结束后被回调
     *
     * @param 已练习的次数
     *
     */
    fun onPracticeEnd(num: Long)

    /**
     * 小休息 - 预备
     *
     * 准备状态，随时会开始倒计时
     *
     * @param timer 休息时间（毫秒）
     *
     */
    fun onRestShortTimeStartPrepared(timer: Long)

    /**
     * 小休息 - 开始
     *
     * 开始倒计时之前回被调用，例如可以用于展示当前是什么模式，时间多少等；
     *
     * @param timer 休息时间（毫秒）
     *
     */
    fun onRestShortTimeStart(timer: Long) {}

    /**
     * 小休息 - 间歇
     *
     * 休息的过程中会被调用，并且附带了剩余的时间；
     *
     * @param timer 剩余时间（毫秒）
     *
     */
    fun onRestShortTimeIntermission(timer: Long) {
        onIntermission(timer)
    }

    /**
     * 小休息 - 结束
     *
     * 休息结束后被回调
     *
     */
    fun onRestShortTimeEnd()

    /**
     * 大休息 - 预备
     *
     * 准备状态，随时会开始倒计时
     *
     * @param timer 休息时间（毫秒）
     *
     */
    fun onRestLongTimeStartPrepared(timer: Long)

    /**
     * 大休息 - 开始
     *
     * 开始倒计时之前回被调用，例如可以用于展示当前是什么模式，时间多少等；
     *
     * @param timer 休息时间（毫秒）
     *
     */
    fun onRestLongTimeStart(timer: Long) {}

    /**
     * 大休息 - 间歇
     *
     * 休息的过程中会被调用，并且附带了剩余的时间；
     *
     * @param timer 剩余时间（毫秒）
     *
     */
    fun onRestLongTimeIntermission(timer: Long) {
        onIntermission(timer)
    }

    /**
     * 小休息 - 结束
     *
     * 休息结束后被回调
     *
     */
    fun onRestLongTimeEnd()

    /**
     * 间歇时间
     *
     * 各个倒计时的间歇的默认调用
     *
     * @param timer 剩余时间（毫秒）
     *
     */
    fun onIntermission(timer: Long)

    /**
     * 毫秒 转换为 时间（00：00：00）
     *
     * @param milliseconds 时间（毫秒）
     *
     * @return 时间（00：00：00）
     *
     */
    fun getMillisecondsToTimeFormat(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60)) % 24

        // 格式化为两位数，不足两位前面补0
        return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

}