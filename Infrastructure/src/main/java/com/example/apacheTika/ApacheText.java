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
            String text = tika.parseToString(stream);
            if (text == null) return "";


            text = text.replaceAll("\\h", " ");

            text = text.replaceAll(" +", " ");

            return text.trim();
        } catch (IOException | TikaException e) {
            throw new RuntimeException(e);
        }
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
