package com.example.template;

import java.io.InputStream;

public interface TemplateTextPort {
    public String extract(InputStream stream, String fileName);
}
