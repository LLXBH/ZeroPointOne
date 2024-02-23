package llxbh.zeropointone.tools

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet

/**
 * Markdown 文本处理器
 */
class MarkdownProcessor {

    val OPTIONS  = MutableDataSet().apply {
//        set(Parser.EXTENSIONS, Arrays.asList(
//            AutolinkExtension.create(),
//            TablesExtension.create()
//        ))
    }

    val sParser = Parser.builder(OPTIONS).build()
    val sRenderer = HtmlRenderer.builder(OPTIONS).build()

    /**
     * 将 Markdown 转换为 Html
     */
    fun markdownToHtml(markdownText: String): String {
        val document = sParser.parse(markdownText)
        return sRenderer.render(document)
    }

}