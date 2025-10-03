package com.example.housekeeping.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTML工具类
 */
public class HtmlUtil {

    private static final Logger log = LoggerFactory.getLogger(HtmlUtil.class);

    /**
     * 清理HTML内容，移除危险标签和属性
     */
    public static String cleanHtml(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }

        try {
            // 使用Jsoup清理HTML
            Safelist safelist = Safelist.relaxed()
                    .addTags("p", "br", "strong", "em", "u", "h1", "h2", "h3", "h4", "h5", "h6")
                    .addTags("ul", "ol", "li", "blockquote", "pre", "code")
                    .addTags("table", "thead", "tbody", "tr", "th", "td")
                    .addTags("img", "a")
                    .addAttributes("img", "src", "alt", "width", "height")
                    .addAttributes("a", "href", "title")
                    .addProtocols("img", "src", "http", "https")
                    .addProtocols("a", "href", "http", "https");

            return Jsoup.clean(html, safelist);
        } catch (Exception e) {
            log.error("HTML清理失败：{}", e.getMessage(), e);
            return html;
        }
    }

    /**
     * 提取纯文本内容
     */
    public static String extractText(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }

        try {
            Document doc = Jsoup.parse(html);
            return doc.text();
        } catch (Exception e) {
            log.error("文本提取失败：{}", e.getMessage(), e);
            return html;
        }
    }

    /**
     * 生成摘要（截取指定长度的纯文本）
     */
    public static String generateSummary(String html, int maxLength) {
        String text = extractText(html);
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    /**
     * 验证HTML内容是否安全
     */
    public static boolean isSafeHtml(String html) {
        if (html == null || html.trim().isEmpty()) {
            return true;
        }

        try {
            String cleaned = cleanHtml(html);
            return cleaned.equals(html);
        } catch (Exception e) {
            log.error("HTML安全检查失败：{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除所有HTML标签
     */
    public static String stripHtml(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }

        try {
            return Jsoup.parse(html).text();
        } catch (Exception e) {
            log.error("HTML标签移除失败：{}", e.getMessage(), e);
            return html;
        }
    }
}
