package llxbh.zeropointone.data.model

/**
 * 任务的 “循环” 接口，在 “数据类” 中继承，编写各自独立的相关事项
 *
 * 定义了如何判断 “循环” 的逻辑，以及如何复制出一个新的数据
 *
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