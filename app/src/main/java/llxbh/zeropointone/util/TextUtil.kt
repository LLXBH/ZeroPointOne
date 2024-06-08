package llxbh.zeropointone.util

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet

/**
 * Markdown 文本处理器
 */
class TextUtil {

    private val sOptions  = MutableDataSet()
    private val sParser = Parser.builder(sOptions).build()
    private val sRenderer = HtmlRenderer.builder(sOptions).build()

    /**
     * 将 Markdown 转换为 Html
     */
    fun markdownToHtml(markdownText: String): String {
        val document = sParser.parse(markdownText)
        return sRenderer.render(document)
    }

}