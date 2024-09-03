package llxbh.zeropointone.view.tomatoclock

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import llxbh.zeropointone.base.BindingBaseFragment
import llxbh.zeropointone.databinding.FragmentTomatoClockBinding
import llxbh.zeropointone.util.MassageUtil
import llxbh.zeropointone.util.tomato.Tomato
import llxbh.zeropointone.util.tomato.TomatoCloakUtil
import llxbh.zeropointone.util.tomato.TomatoClockStateInterface

class TomatoClockFragment: BindingBaseFragment<FragmentTomatoClockBinding>() {

    private val sTomato = Tomato()
    private val sTomatoCloakUtil = TomatoCloakUtil(
        sTomato,
        object : TomatoClockStateInterface {
            override fun onPracticeStartPrepare(timer: Long) {
                getBinding().also {
                    it.tvTomatoClockMode.text = "练习"
                    setTimeText(timer)
                }
            }

            override fun onPracticeEnd(num: Int) {
                getBinding().also {
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
                MassageUtil.sendNotification("小休息结束！")
            }

            override fun onRestLongTimeStartPrepared(timer: Long) {
                getBinding().also {
                    it.tvTomatoClockMode.text = "大休息"
                    setTimeText(timer)
                }
            }

            override fun onRestLongTimeEnd() {
                MassageUtil.sendNotification("大休息结束！")
            }

            override fun onIntermission(timer: Long) {
                setTimeText(timer)
            }

            fun setTimeText(timer: Long) {
                getBinding().tvTomatoClockTime.text = getMillisecondsToTimeFormat(timer)
            }

        }
    )
    private val sTomatoClick = View.OnClickListener {

        getBinding().apply {
            when(it?.id) {
                btnTomatoClockStart.id -> {
                    // 开始倒计时
                    sTomatoCloakUtil.onStart()
                }

                btnTomatoClockSuspend.id -> {
                    // 暂停时间
                    sTomatoCloakUtil.onPause()
                }

                btnTomatoClockFinish.id -> {
                    // 结束时间
                    sTomatoCloakUtil.onEnd()
                }
            }
        }
    }

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

    /**
     * 自定义的 TextWatcher ，主要用于减少样本代码
     */
    abstract class MyTextWatcher : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
            //...
        }

        override fun onTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
            //...
        }

        override fun afterTextChanged(editable: Editable?) {
            val input = editable.toString().trim()
            afterTextChanged(input)
        }

        abstract fun afterTextChanged(text: String)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBinding().apply {
            mTomato = sTomato
            mAllFrequency = "0"
            mTomatoClick = sTomatoClick

            // 监控用户输入的数值，同步修改到 sTomato
            // 练习数值
            etTomatoClockPracticeTime.addTextChangedListener(object : MyTextWatcher() {
                override fun afterTextChanged(text: String) {
                    sTomato.practiceTime = text.toIntOrNull() ?: sTomato.defaultPracticeTime
                }
            })
            etTomatoClockPracticeFrequency.addTextChangedListener(object : MyTextWatcher() {
                override fun afterTextChanged(text: String) {
                    sTomato.practiceFrequency = text.toIntOrNull() ?: sTomato.defaultPracticeFrequency
                }
            })
            // 休息数值
            etTomatoClockRestShortTime.addTextChangedListener(object : MyTextWatcher() {
                override fun afterTextChanged(text: String) {
                    sTomato.restShortTime = text.toIntOrNull() ?: sTomato.defaultRestShortTime
                }
            })
            etTomatoClockRestLongTime.addTextChangedListener(object : MyTextWatcher() {
                override fun afterTextChanged(text: String) {
                    sTomato.restLongTime = text.toIntOrNull() ?: sTomato.defaultRestLongTime
                }
            })
        }
    }
}

