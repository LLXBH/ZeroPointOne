package llxbh.zeropointone.util

import android.webkit.WebView
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet

/**
 * 文本处理器
 */
class TextUtil {

    private val sOptions  = MutableDataSet()
    private val sParser = Parser.builder(sOptions).build()
    private val sRenderer = HtmlRenderer.builder(sOptions).build()

    /**
     * 将 Markdown 转换为 Html 样式
     *
     * @param markdownText 文本
     *
     */
    fun onMarkdownToHtml(markdownText: String): String {
        val document = sParser.parse(markdownText)
        return sRenderer.render(document)
    }

    /**
     * 将 Markdown 文本加载到 WebView
     *
     * @param markdownText 文本
     * @param webView 浏览器
     *
     */
    fun onMarkdownToHtmlView(markdownText: String, webView: WebView) {
        webView.loadData(
            onMarkdownToHtml(markdownText),
            "text/html; charset=UTF-8",
            null
        )
    }

}