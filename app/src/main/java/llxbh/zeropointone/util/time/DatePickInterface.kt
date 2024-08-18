package llxbh.zeropointone.util.time

/**
 * 日期选择接口
 */
interface DatePickInterface {

    /**
     * 日期数据回调
     *
     * @param year 年
     * @param month 月
     * @param dayOfMonth 日
     */
    fun onDateSet(year: Int, month: Int, dayOfMonth: Int)

}