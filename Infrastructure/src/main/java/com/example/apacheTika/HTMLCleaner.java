package com.example.apacheTika;

import com.example.HTMLCleanerPort;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class HTMLCleaner implements HTMLCleanerPort {

    @Override
    public String clean(String html) {
        String sanitizedContent = StringEscapeUtils.unescapeHtml4(html);
        return Jsoup.parse(sanitizedContent).text();
    }
}
