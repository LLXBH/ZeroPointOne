package llxbh.zeropointone.view

import android.os.Bundle
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
        getBinding().bnvMainFragmentSwitch.setOnItemReselectedListener {

        }
    }
}