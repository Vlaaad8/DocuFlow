package com.example.apacheTika;

import com.example.template.TemplateTextPort;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class ApacheText implements TemplateTextPort {

    private final Tika tika = new Tika();
    @Override
    public String extract(InputStream stream) {
        try {
            return tika.parseToString(stream);
        } catch (IOException | TikaException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean validateExtension(String fileName){
        Set<String> allowedExtensions = Set.of("docx","pdf","txt");
        String extension = getExtension(fileName);
        for (String allowedExtension : allowedExtensions) {
            if (extension.equalsIgnoreCase(allowedExtension)) {
                return true;
            }
        }
        return false;
    }
    private String getExtension(String fileName){
        if (fileName == null){
            return "";
        }
        int idx = fileName.lastIndexOf(".");
        if (idx == -1){
            return "";
        }
        return fileName.substring(idx+1);
    }
}
