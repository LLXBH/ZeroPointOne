package llxbh.zeropointone.view.tomatoclock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import llxbh.zeropointone.R
import llxbh.zeropointone.base.BindingBaseFragment
import llxbh.zeropointone.databinding.FragmentTomatoClockBinding
import llxbh.zeropointone.util.MassageUtil
import llxbh.zeropointone.util.tomato.Tomato
import llxbh.zeropointone.util.tomato.TomatoCloakUtil
import llxbh.zeropointone.util.tomato.TomatoClockInterface
import java.sql.Time

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

            override fun onPracticeEnd(num: Int) {
                getBinding().also {
                    it.mPracticeTiming = getMillisecondsToTimeFormat(0)
                    it.mAllFrequency = num.toString()
                }
                MassageUtil.sendNotification("练习结束！")
            }

            override fun onRestShortTimeStartPrepared(timer: Long) {
                getBinding().also {
                    it.tvTomatoClockMode.text = "小休息"
                    setTimeText(timer)
                }
            }

            override fun onRestShortTimeEnd() {
                MassageUtil.sendNotification("小消息结束！")
            }

            override fun onRestLongTimeStartPrepared(timer: Long) {
                getBinding().also {
                    it.tvTomatoClockMode.text = "大休息"
                    setTimeText(timer)
                }
            }

            override fun onRestLongTimeEnd() {
                MassageUtil.sendNotification("大消息结束！")
            }

            override fun onIntermission(timer: Long) {
                setTimeText(timer)
            }

            fun setTimeText(timer: Long) {
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
            mTomato = sTomato
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

            // 监控用户输入的数值，同步修改到 sTomato
            // 练习
            etTomatoClockPracticeTime.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //TODO("Not yet implemented")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //TODO("Not yet implemented")
                }

                override fun afterTextChanged(s: Editable?) {
                    val input = s.toString().trim()
                    if (input.isNotEmpty()) {
                        try {
                            sTomato.practiceTime = input.toInt()
                        } catch (e: NullPointerException) {
                            // 不变
                        }
                    }
                }

            })
            etTomatoClockPracticeFrequency.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //TODO("Not yet implemented")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //TODO("Not yet implemented")
                }

                override fun afterTextChanged(s: Editable?) {
                    val input = s.toString().trim()
                    if (input.isNotEmpty()) {
                        try {
                            sTomato.practiceFrequency = input.toInt()
                        } catch (e: NullPointerException) {
                            // 不变
                        }
                    }
                }

            })
            // 休息
            etTomatoClockRestShortTime.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //TODO("Not yet implemented")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //TODO("Not yet implemented")
                }

                override fun afterTextChanged(s: Editable?) {
                    val input = s.toString().trim()
                    if (input.isNotEmpty()) {
                        try {
                            sTomato.restShortTime = input.toInt()
                        } catch (e: NullPointerException) {
                            // 不变
                        }
                    }
                }

            })
            etTomatoClockRestLongTime.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //TODO("Not yet implemented")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //TODO("Not yet implemented")
                }

                override fun afterTextChanged(s: Editable?) {
                    val input = s.toString().trim()
                    if (input.isNotEmpty()) {
                        try {
                            sTomato.restLongTime = input.toInt()
                        } catch (e: NullPointerException) {
                            // 不变
                        }
                    }
                }

            })

        }
    }

}