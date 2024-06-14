package llxbh.zeropointone.data.model

/**
 * 一个创建循环任务的接口
 *
 * 在 “数据类” 中继承，编写各自独立的相关事项
 */
interface TaskLoopInterface<T> {

    /**
     * 检查是否已经达成循环条件
     *
     * @return 检查结果
     */
    fun onLoopInspect(): Boolean

    /**
     * 返回新的循环数据
     *
     * @return 新的数据
     */
    fun onLoopNewData(): T

}