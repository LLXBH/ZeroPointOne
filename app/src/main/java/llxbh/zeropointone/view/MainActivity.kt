package llxbh.zeropointone.view

import android.os.Bundle
import android.view.Menu
import androidx.viewpager2.widget.ViewPager2
import llxbh.zeropointone.R
import llxbh.zeropointone.base.BindingBaseActivity
import llxbh.zeropointone.databinding.ActivityMainBinding

/**
 * 主页
 */
class MainActivity: BindingBaseActivity<ActivityMainBinding>() {

    override fun setBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBinding().apply {
            // 绑定 BottomNavigationView 和 ViewPager2

            // 点击到哪个按钮，就通知 ViewPager2 切换到哪个
            bnvMainFragmentSwitch.setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.action_task_list -> vpMainFragment.currentItem = 0
                    R.id.action_tomato_clock -> vpMainFragment.currentItem = 1
                    else -> {throw IllegalStateException("Unknown position ${it.itemId}")}
                }
                true
            }

            // 绑定适配器
            vpMainFragment.adapter = MainPagerAdapter(supportFragmentManager, lifecycle)
            // 切换到哪个，就通知 BottomNavigationView 选择哪个
            vpMainFragment.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    bnvMainFragmentSwitch.selectedItemId = when(position) {
                        0 -> R.id.action_task_list
                        1 -> R.id.action_tomato_clock
                        else -> throw IllegalStateException("Unknown position $position")
                    }
                }
            })
        }
    }
}