package com.example.apacheTika;

import com.example.HTMLCleanerPort;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class HTMLCleaner implements HTMLCleanerPort {

    @Override
    public String clean(String html) {
       return  StringEscapeUtils.unescapeHtml4(html);
    }
}
