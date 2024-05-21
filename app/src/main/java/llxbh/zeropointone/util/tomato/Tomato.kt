package llxbh.zeropointone.util.tomato

import androidx.databinding.ObservableLong

/**
 * 一个番茄钟的数据类
 *
 * practiceFrequency 定义了练习多少次之后，可以进入大大休息；
 *
 * @param practiceTime 练习时间
 * @param practiceFrequency 练习次数
 * @param restShortTime 小休时间
 * @param restLongTime 大休时间
 */
data class Tomato(
    var practiceTime: Int,
    var practiceFrequency: Int,
    var restShortTime: Int,
    var restLongTime: Int
)
