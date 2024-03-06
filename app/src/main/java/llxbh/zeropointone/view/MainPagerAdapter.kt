package llxbh.zeropointone.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import llxbh.zeropointone.view.tasklist.TaskListFragment
import llxbh.zeropointone.view.tomatoclock.TomatoClockFragment

class MainPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> TaskListFragment.newInstance()
            1 -> TomatoClockFragment.newInstance()
            else -> throw IllegalStateException("Unknown position $position")
        }
    }
}