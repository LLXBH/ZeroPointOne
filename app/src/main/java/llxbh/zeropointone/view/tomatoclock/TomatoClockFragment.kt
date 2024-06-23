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

    private val sTomato = Tomato(
        30,
        4,
        5,
        45
    )
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBinding().apply {
            mTomato = sTomato
            mAllFrequency = "0"
            mTomatoClick = sTomatoClick

            // 监控用户输入的数值，同步修改到 sTomato
            // 练习数值
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
                    onAfterTextChanged(etTomatoClockPracticeTime, s)
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
                    onAfterTextChanged(etTomatoClockPracticeFrequency, s)
                }

            })
            // 休息数值
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
                    onAfterTextChanged(etTomatoClockRestShortTime, s)
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
                    onAfterTextChanged(etTomatoClockRestLongTime, s)
                }

            })

        }
    }

    private fun onAfterTextChanged(view: View, editable: Editable?) {
        val input = editable.toString().trim()
        if (input.isNotEmpty()) {
            try {
                getBinding().apply {
                    when (view.id) {
                        etTomatoClockPracticeTime.id -> {
                            sTomato.practiceTime = input.toInt()
                        }
                        etTomatoClockPracticeFrequency.id -> {
                            sTomato.practiceFrequency = input.toInt()
                        }
                        etTomatoClockRestShortTime.id -> {
                            sTomato.restShortTime = input.toInt()
                        }
                        etTomatoClockRestLongTime.id -> {
                            sTomato.restLongTime = input.toInt()
                        }
                    }
                }
            } catch (e: NullPointerException) {
                // 不变
            }
        }
    }

}