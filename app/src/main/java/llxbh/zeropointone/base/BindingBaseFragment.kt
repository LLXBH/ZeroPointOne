package llxbh.zeropointone.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding

/**
 * 一个专门适配于 ViewDataBinding 的基类
 * 用泛型固定布局，简单地进行布局绑定
 */
abstract class BindingBaseFragment<out T: ViewDataBinding>: BaseFragment() {

    private lateinit var mBinding: T

    /**
     * Binding 的赋值，子类需要定义如何实现
     */
    abstract fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): T

    /**
     * 返回 Binding 来进行布局操作使用
     */
    protected fun getBinding(): T {
        return mBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = setBinding(inflater, container, savedInstanceState)
        return mBinding.root
    }

}