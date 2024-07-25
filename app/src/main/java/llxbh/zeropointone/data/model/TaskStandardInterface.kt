package llxbh.zeropointone.data.model

/**
 * 任务的 “规范” 接口，在 “数据类” 中继承，编写各自独立的相关事项
 *
 * 用于检查数据的是否符合自己定义的规范，且如何进行修正
 *
 */
interface TaskStandardInterface<T> {

    fun onInspect(): Boolean {
        return onInspectContent() && onInspectDateTime()
    }

    /**
     * 检查 内容
     *
     * @return 检查结果
     */
    fun onInspectContent(): Boolean

    /**
     * 修正 内容
     *
     * @return 修改后的新数据
     */
    fun onCorrectContent(): T?

    /**
     * 检查 时间
     *
     * @return 检查结果
     */
    fun onInspectDateTime(): Boolean

    /**
     * 修正 时间
     *
     * @return 修改后的新数据
     */
    fun onCorrectDateTime(): T?

}