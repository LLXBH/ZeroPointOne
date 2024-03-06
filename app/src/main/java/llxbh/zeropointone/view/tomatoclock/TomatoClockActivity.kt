package llxbh.zeropointone.view.tomatoclock

import android.os.Bundle
import llxbh.zeropointone.base.BindingBaseActivity
import llxbh.zeropointone.databinding.ActivityTomatoClockBinding

class TomatoClockActivity: BindingBaseActivity<ActivityTomatoClockBinding>() {

    override fun setBinding(): ActivityTomatoClockBinding {
        return ActivityTomatoClockBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}